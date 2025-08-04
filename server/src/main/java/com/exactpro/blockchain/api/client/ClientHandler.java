package com.exactpro.blockchain.api.client;

import com.exactpro.blockchain.CustomerCreditTransferConverter;
import com.exactpro.blockchain.coincento.CoincentoWallet;
import com.exactpro.blockchain.entity.*;
import com.exactpro.blockchain.kafka.KafkaPublisher;
import com.exactpro.blockchain.repository.*;
import com.exactpro.iso20022.CustomerCreditTransfer;
import com.exactpro.iso20022.XmlCodec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
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
    private final CurrencyRepository currencyRepository;
    private final TransferRepository transferRepository;
    private final ObjectMapper objectMapper;
    private final MessageRepository messageRepository;
    private final XmlCodec xmlCodec;
    private final CustomerCreditTransferConverter converter;
    private final KafkaPublisher kafkaPublisher;
    private final CoincentoWallet wallet;

    public ClientHandler(
        AccountRepository accountRepository,
        ClientRepository clientRepository,
        CurrencyRepository currencyRepository,
        ConversionRateRepository conversionRateRepository,
        TransferRepository transferRepository,
        MessageRepository messageRepository,
        XmlCodec xmlCodec,
        CustomerCreditTransferConverter converter,
        KafkaPublisher kafkaPublisher,
        CoincentoWallet wallet
    ) {
        this.accountRepository = accountRepository;
        this.clientRepository = clientRepository;
        this.conversionRateRepository = conversionRateRepository;
        this.transferRepository = transferRepository;
        this.objectMapper = new ObjectMapper();
        this.messageRepository = messageRepository;
        this.currencyRepository = currencyRepository;
        this.xmlCodec = xmlCodec;
        this.converter = converter;
        this.kafkaPublisher = kafkaPublisher;
        this.wallet = wallet;
    }

    public Mono<ServerResponse> getAccountsByClientId(ServerRequest request) {
        return ServerResponse.ok().body(accountRepository.findByClientId(clientId), Account.class);
    }

    public Mono<ServerResponse> getTransfersByClientId(ServerRequest request) {
        return ServerResponse.ok().body(transferRepository.findByClientId(clientId), Transfer.class);
    }

    public Mono<ServerResponse> transfer(ServerRequest request) {
        return handleTransfer(request, (transferRequest, client, account, currency) ->
            saveTransferRequest(client, transferRequest)
            .flatMap(transfer ->
                debitAccount(account, transferRequest)
                .then(Mono.defer(() -> saveMessageAndPublishPacs008(transfer)))
                .then(Mono.defer(() -> transferRepository.save(transfer.withStatus(TransferStatus.COMPLETED))))
                .then(Mono.defer(() -> {
                    if (currency.getAddress() == null) {
                        return Mono.empty();
                    }
                    return wallet.transfer(
                        transfer.getCreditorBic(), transfer.getEndToEndId(), currency, transfer.getAmount()
                    );
                }))
                .onErrorResume(ex ->
                    transferRepository.save(transfer.withStatus(TransferStatus.FAILED))
                    .flatMap(failedTransfer -> Mono.error(new Exception("Kafka send failed", ex)))
                )
                .then()
            )
        );
    }

    private @NonNull Mono<Void> debitAccount(@NonNull Account account, @NonNull TransferRequest transferRequest) {
        BigDecimal transferAmount = transferRequest.getAmount();
        String transferCurrency = transferRequest.getCurrencyCode();
        String accountCurrency = account.getCurrencyCode();

        Mono<BigDecimal> amountMono;
        if (accountCurrency.equals(transferCurrency)) {
            amountMono = Mono.just(transferAmount);
        } else {
            amountMono =
                conversionRateRepository.findByBaseCurrencyAndTargetCurrency(transferCurrency, accountCurrency)
                .singleOrEmpty()
                .switchIfEmpty(
                    Mono.error(new Exception(
                        MessageFormat.format(
                            "Conversion rate not found from {0} to {1}",
                            transferCurrency, accountCurrency
                        )
                    ))
                )
                .map(rate -> {
                    BigDecimal convertedAmount = transferAmount.multiply(rate.getRate());
                    logger.info("Converted amount for debit: {} {} (from {} {}) using rate {}",
                            convertedAmount, accountCurrency, transferAmount, transferCurrency, rate.getRate());
                    return convertedAmount;
                });
        }
        return
            amountMono
            .flatMap(amount -> debitAccount(account, amount))
            .doOnSuccess(debitedAccount ->
                logger.info("Balance successfully debited for client ID {} (IBAN: {}). New balance: {}. Amount: {}",
                    clientId, debitedAccount.getIban(), debitedAccount.getBalance(), transferRequest.getAmount()
                )
            )
            .then();
    }

    private @NonNull Mono<Account> debitAccount(@NonNull Account account, @NonNull BigDecimal amountToDebit) {
        if (account.getBalance().compareTo(amountToDebit) < 0) {
            return Mono.error(new IllegalArgumentException(MessageFormat.format(
                "Insufficient funds for account {0}. Current balance: {1}, requested: {2}",
                account.getIban(), account.getBalance(), amountToDebit
            )));
        }
        account.setBalance(account.getBalance().subtract(amountToDebit));
        return accountRepository.save(account);
    }

    @FunctionalInterface
    private interface TransferHandler {
        Mono<Void> handle(
            @NonNull TransferRequest transferRequest,
            @NonNull Client client,
            @NonNull Account account,
            @NonNull Currency currency
        );
    }

    private @NonNull Mono<ServerResponse> handleTransfer(ServerRequest request, @NonNull TransferHandler handler) {
        return
            request.bodyToMono(TransferRequest.class)
            .flatMap(transferRequest ->
                Mono.zip(
                    Mono.just(transferRequest),
                    clientRepository.getByClientId(clientId),
                    accountRepository.getByClientIdAndIban(clientId, transferRequest.getDebtorIban()),
                    currencyRepository.getByCurrencyCode(transferRequest.getCurrencyCode())
                )
            )
            .flatMap(data -> handler.handle(data.getT1(), data.getT2(), data.getT3(), data.getT4()))
            .then(
                ServerResponse.accepted().bodyValue(String.format("Transfer succeeded for client %d", clientId))
            )
            .onErrorResume(RuntimeException.class, ex -> {
                logger.error("Unable to handle Credit Transfer Request", ex);
                return ServerResponse.status(500).bodyValue("Internal Server Error");
            });
    }

    private @NonNull Mono<Transfer> saveTransferRequest(
            @NonNull Client client, @NonNull TransferRequest transferRequest
    ) {
        return transferRepository.save(converter.newTransfer(client, transferRequest));
    }

    private @NonNull Mono<Void> saveMessageAndPublishPacs008(@NonNull Transfer transfer) {
        CustomerCreditTransfer customerCreditTransfer = converter.toCustomerCreditTransfer(transfer);

        String transferJson;
        try {
            transferJson = objectMapper.writeValueAsString(transfer);
        } catch (JsonProcessingException e) {
            return Mono.error(new Exception("Failed to convert Transfer to JSON", e));
        }

        Message message = new Message(
            customerCreditTransfer.getGroupHeader().getMessageId(),
            transfer.getTransferId(),
            transferJson);

        return messageRepository.save(message)
            .flatMap(savedMessage -> {
                logger.info("Message saved with ID: {}", savedMessage.getMessageId());

                String pacs008XmlString;
                try {
                    pacs008XmlString = xmlCodec.encode(customerCreditTransfer);
                } catch (JAXBException | TransformerException ex) {
                    return Mono.error(new Exception("Failed to encode transfer to pacs.008 XML", ex));
                }

                return kafkaPublisher.publishMessage(transfer.getCreditorBic(), pacs008XmlString);
            })
            .then();
    }
}
