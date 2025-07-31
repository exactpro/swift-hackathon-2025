package com.exactpro.blockchain.api.client;

import com.exactpro.blockchain.CustomerCreditTransferConverter;
import com.exactpro.blockchain.entity.Account;
import com.exactpro.blockchain.entity.Client;
import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.entity.TransferDetails;
import com.exactpro.blockchain.entity.TransferStatus;
import com.exactpro.blockchain.kafka.KafkaPublisher;
import com.exactpro.blockchain.repository.AccountRepository;
import com.exactpro.blockchain.repository.ClientRepository;
import com.exactpro.blockchain.repository.TransferRepository;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.XmlCodec;
import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.xml.transform.TransformerException;
import java.text.MessageFormat;

@Component
public class ClientHandler {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final String clientBic;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final XmlCodec xmlCodec;
    private final TransferRepository transferRepository;
    private final CustomerCreditTransferConverter converter;
    private final KafkaPublisher kafkaPublisher;

    public ClientHandler(@Value("${client.bic}") String clientBic,
                         AccountRepository accountRepository,
                         ClientRepository clientRepository,
                         XmlCodec xmlCodec,
                         TransferRepository transferRepository,
                         CustomerCreditTransferConverter converter,
                         KafkaPublisher kafkaPublisher) {
        this.clientBic = clientBic;
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.xmlCodec = xmlCodec;
        this.transferRepository = transferRepository;
        this.converter = converter;
        this.kafkaPublisher = kafkaPublisher;
    }

    public Mono<ServerResponse> getAccountsByClientId(ServerRequest request) {
        int clientId = Integer.parseInt(request.pathVariable("clientId"));
        return ServerResponse.ok().body(accountRepository.findByClientId(clientId), com.exactpro.blockchain.entity.Account.class);
    }

    public Mono<ServerResponse> transfer(ServerRequest request) {
        int clientId = Integer.parseInt(request.pathVariable("clientId"));
        return Mono.zip(request.bodyToMono(TransferDetails.class),
                clientRepository.findByClientId(clientId).singleOrEmpty())
            .flatMap(data -> {

                TransferDetails transferDetails = data.getT1();
                Client client = data.getT2();

                return subtractSelfBalanceMono(clientId, transferDetails)
                    .flatMap(debitedAccount -> addToRecipientBalanceMono(transferDetails)
                        .flatMap(creditedAccount -> {

                            CustomerCreditTransfer customerCreditTransfer = converter.convertFromClientAndTransferDetails(client, clientBic, transferDetails);

                            Transfer transferToSave;
                            try {
                                transferToSave = converter.convertToTransfer(customerCreditTransfer, TransferStatus.PENDING).get(0);
                            } catch (IllegalArgumentException e) {
                                return Mono.error(new RuntimeException("Failed to convert CustomerCreditTransfer to Transfer", e));
                            }

                            return transferRepository.save(transferToSave)
                                .flatMap(transfer -> {
                                    String encodedTransfer;
                                    try {
                                        encodedTransfer = xmlCodec.encode(customerCreditTransfer);
                                    } catch (JAXBException | TransformerException e) {
                                        return Mono.error(new RuntimeException("Failed to encode to XML", e));
                                    }

                                    return kafkaPublisher.publishMessage(transferDetails.getTargetBic(), encodedTransfer)
                                        .doOnSuccess(senderResult -> {
                                            transfer.setStatus(TransferStatus.COMPLETED);
                                            transferRepository.save(transfer).subscribe();
                                        })
                                        .onErrorResume(e -> {
                                            transfer.setStatus(TransferStatus.FAILED);
                                            transferRepository.save(transfer).subscribe();
                                            return Mono.error(new RuntimeException("Kafka send failed", e));
                                        })
                                        .then(ServerResponse.accepted()
                                            .bodyValue(MessageFormat.format("Transfer successful for client {0}. Details: {1}", clientId, transferDetails)));
                                });
                        }));
            })
            .onErrorResume(RuntimeException.class, e -> ServerResponse.status(500).bodyValue("Internal Server Error"))
            .switchIfEmpty(ServerResponse.badRequest().bodyValue("Invalid request"));
    }

    private Mono<Account> subtractSelfBalanceMono(int clientId, TransferDetails transferDetails) {
        return accountRepository.findByClientIdAndIban(clientId, transferDetails.getSelfIban())
            .singleOrEmpty()
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException(MessageFormat.format(
                    "Debtor Account not found for client ID {0} and IBAN: {1}", clientId, transferDetails.getSelfIban()))))
            .flatMap(account -> {
                if (account.getBalance().compareTo(transferDetails.getAmount()) < 0) {
                    return Mono.error(new IllegalArgumentException(MessageFormat.format(
                        "Insufficient funds for account {0}. Current balance: {1}, requested: {2}",
                        transferDetails.getSelfIban(), account.getBalance(), transferDetails.getAmount()
                    )));
                }
                account.setBalance(account.getBalance().subtract(transferDetails.getAmount()));
                return accountRepository.save(account);
            })
            .doOnSuccess(debitedAccount -> logger.info("Balance successfully debited for client ID {} (IBAN: {}). New balance: {}. Amount: {}",
                clientId, debitedAccount.getIban(), debitedAccount.getBalance(), transferDetails.getAmount()));
    }

    private Mono<Account> addToRecipientBalanceMono(TransferDetails transferDetails) {
        return accountRepository.findByIban(transferDetails.getTargetIban())
            .singleOrEmpty()
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException(MessageFormat.format(
                    "Creditor Account not found for IBAN: {0}", transferDetails.getTargetIban()))))
            .flatMap(creditorAccount -> {
                creditorAccount.setBalance(creditorAccount.getBalance().add(transferDetails.getAmount()));
                return accountRepository.save(creditorAccount);
            })
            .doOnSuccess(creditedAccount -> logger.info("Balance successfully credited to recipient account (IBAN: {}). New balance: {}. Amount: {}",
                creditedAccount.getIban(), creditedAccount.getBalance(), transferDetails.getAmount()));
    }
}
