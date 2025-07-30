package com.exactpro.blockchain.api.client;

import com.exactpro.blockchain.entity.Account;
import com.exactpro.blockchain.entity.Client;
import com.exactpro.blockchain.entity.TransferDetails;
import com.exactpro.blockchain.kafka.KafkaPublisher;
import com.exactpro.blockchain.repository.AccountRepository;
import com.exactpro.blockchain.repository.ClientRepository;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.GroupHeader;
import com.exactpro.iso20022.Participant;
import com.exactpro.iso20022.TransactionInfo;
import com.exactpro.iso20022.XmlCodec;
import jakarta.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.xml.transform.TransformerException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;

@Component
public class ClientHandler {
    private final String clientBic;
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final XmlCodec xmlCodec;
    private final KafkaPublisher kafkaPublisher;

    public ClientHandler(@Value("${client.bic}") String clientBic,
                         AccountRepository accountRepository,
                         ClientRepository clientRepository,
                         XmlCodec xmlCodec,
                         KafkaPublisher kafkaPublisher) {
        this.clientBic = clientBic;
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.xmlCodec = xmlCodec;
        this.kafkaPublisher = kafkaPublisher;
    }

    public Mono<ServerResponse> getAccountsByClientId(ServerRequest request) {
        int clientId = Integer.parseInt(request.pathVariable("clientId"));
        return ServerResponse.ok().body(accountRepository.findByClientId(clientId), com.exactpro.blockchain.entity.Account.class);
    }

    public Mono<ServerResponse> transfer(ServerRequest request) {
        int clientId = Integer.parseInt(request.pathVariable("clientId"));
        return Mono.zip(request.bodyToMono(TransferDetails.class),
                accountRepository.findByClientId(clientId).singleOrEmpty(),
                clientRepository.findByClientId(clientId).singleOrEmpty())
            .flatMap(data -> {

                TransferDetails transferDetails = data.getT1();
                Account clientAccount = data.getT2();
                Client client = data.getT3();

                GroupHeader groupHeader = GroupHeader.builder().messageId("").timestamp(Instant.now()).build();

                Participant debtor = Participant.builder()
                    .fullName(client.getFullName())
                    .iban(clientAccount.getIban())
                    .bic(clientBic)
                    .build();

                Participant creditor = Participant.builder()
                    .fullName(transferDetails.getFullName())
                    .iban(transferDetails.getIban())
                    .bic(transferDetails.getBic())
                    .build();

                TransactionInfo transactionInfo = TransactionInfo.builder()
                    .endToEndId("")
                    .currency(transferDetails.getCurrency())
                    .amount(transferDetails.getAmount())
                    .settlementDate(LocalDate.now())
                    .debtor(debtor)
                    .creditor(creditor)
                    .remittanceInfo("")
                    .build();

                CustomerCreditTransfer customerCreditTransfer = new CustomerCreditTransfer(
                    groupHeader,
                    Collections.singletonList(transactionInfo)
                );

                String encodedTransfer;
                try {
                    encodedTransfer = xmlCodec.encode(customerCreditTransfer);
                } catch (JAXBException | TransformerException e) {
                    throw new RuntimeException(e);
                }

                return kafkaPublisher.publishMessage(transferDetails.getBic(), encodedTransfer)
                    .then(ServerResponse.accepted()
                        .bodyValue("Transfer successful for client " + clientId + ". Details: " + transferDetails));
            })
            .onErrorResume(RuntimeException.class, e -> ServerResponse.status(500).bodyValue("Internal Server Error"))
            .switchIfEmpty(ServerResponse.badRequest().bodyValue("Invalid request"));
    }
}
