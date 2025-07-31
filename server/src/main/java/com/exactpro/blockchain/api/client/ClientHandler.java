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
import com.exactpro.blockchain.repository.ConversionRateRepository;
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
import java.math.BigDecimal;
import java.text.MessageFormat;

@Component
public class ClientHandler {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    @Value("${client.bic}")
    private String clientBic;
    @Value("${default.user}")
    private Integer clientId;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final ConversionRateRepository conversionRateRepository;
    private final XmlCodec xmlCodec;
    private final TransferRepository transferRepository;
    private final CustomerCreditTransferConverter converter;
    private final KafkaPublisher kafkaPublisher;

    public ClientHandler(AccountRepository accountRepository,
                         ClientRepository clientRepository,
                         ConversionRateRepository conversionRateRepository,
                         XmlCodec xmlCodec,
                         TransferRepository transferRepository,
                         CustomerCreditTransferConverter converter,
                         KafkaPublisher kafkaPublisher) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.conversionRateRepository = conversionRateRepository;
        this.xmlCodec = xmlCodec;
        this.transferRepository = transferRepository;
        this.converter = converter;
        this.kafkaPublisher = kafkaPublisher;
    }

    public Mono<ServerResponse> getAccountsByClientId(ServerRequest request) {
        return ServerResponse.ok().body(accountRepository.findByClientId(clientId), Account.class);
    }

    public Mono<ServerResponse> getTransfersByClientId(ServerRequest request) {
        return ServerResponse.ok().body(transferRepository.findByClientId(clientId), Transfer.class);
    }

    public Mono<ServerResponse> transfer(ServerRequest request) {
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
                BigDecimal amountToDebit = transferDetails.getAmount();
                String transferCurrency = transferDetails.getCurrency();
                String accountCurrency = account.getCurrencyCode();

                if (!accountCurrency.equals(transferCurrency)) {
                    return conversionRateRepository.findByBaseCurrencyAndTargetCurrency(transferCurrency, accountCurrency)
                        .singleOrEmpty()
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(MessageFormat.format(
                            "Conversion rate not found from {0} to {1}", transferCurrency, accountCurrency))))
                        .flatMap(rate -> {
                            BigDecimal convertedAmount = transferDetails.getAmount().multiply(rate.getRate());
                            logger.info("Converted amount for debit: {} {} (from {} {}) using rate {}",
                                convertedAmount, accountCurrency, amountToDebit, transferCurrency, rate.getRate());
                            return performDebit(account, convertedAmount);
                        });
                } else {
                    return performDebit(account, amountToDebit);
                }
            })
            .doOnSuccess(debitedAccount -> logger.info("Balance successfully debited for client ID {} (IBAN: {}). New balance: {}. Amount: {}",
                clientId, debitedAccount.getIban(), debitedAccount.getBalance(), transferDetails.getAmount()));
    }

    private Mono<Account> performDebit(Account account, BigDecimal amountToDebit) {
        if (account.getBalance().compareTo(amountToDebit) < 0) {
            return Mono.error(new IllegalArgumentException(MessageFormat.format(
                "Insufficient funds for account {0}. Current balance: {1}, requested: {2}",
                account.getIban(), account.getBalance(), amountToDebit
            )));
        }
        account.setBalance(account.getBalance().subtract(amountToDebit));
        return accountRepository.save(account);
    }

    private Mono<Account> addToRecipientBalanceMono(TransferDetails transferDetails) {
        return accountRepository.findByIban(transferDetails.getTargetIban())
            .singleOrEmpty()
            .switchIfEmpty(Mono.error(
                new IllegalArgumentException(MessageFormat.format(
                    "Creditor Account not found for IBAN: {0}", transferDetails.getTargetIban()))))
            .flatMap(account -> {
                BigDecimal amountToCredit = transferDetails.getAmount();
                String transferCurrency = transferDetails.getCurrency();
                String accountCurrency = account.getCurrencyCode();

                if (!accountCurrency.equals(transferCurrency)) {
                    return conversionRateRepository.findByBaseCurrencyAndTargetCurrency(transferCurrency, accountCurrency)
                        .singleOrEmpty()
                        .switchIfEmpty(Mono.error(new IllegalArgumentException(MessageFormat.format(
                            "Conversion rate not found from {0} to {1}", transferCurrency, accountCurrency))))
                        .flatMap(rate -> {
                            BigDecimal convertedAmount = transferDetails.getAmount().multiply(rate.getRate());
                            logger.info("Converted amount for credit: {} {} (from {} {}) using rate {}",
                                convertedAmount, accountCurrency, amountToCredit, transferCurrency, rate.getRate());
                            return performCredit(account, convertedAmount);
                        });
                } else {
                    return performCredit(account, amountToCredit);
                }
            })
            .doOnSuccess(creditedAccount -> logger.info("Balance successfully credited to recipient account (IBAN: {}). New balance: {}. Amount: {}",
                creditedAccount.getIban(), creditedAccount.getBalance(), transferDetails.getAmount()));
    }

    private Mono<Account> performCredit(Account account, BigDecimal amountToCredit) {
        account.setBalance(account.getBalance().add(amountToCredit));
        return accountRepository.save(account);
    }
}
