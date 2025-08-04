package com.exactpro.blockchain.api.bank;

import com.exactpro.blockchain.entity.Account;
import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.repository.AccountRepository;
import com.exactpro.blockchain.repository.MessageRepository;
import com.exactpro.blockchain.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
public class BankHandler {
    @Value("${default.user}")
    private Integer clientId;

    private final @NonNull AccountRepository accountRepository;
    private final @NonNull MessageRepository messageRepository;
    private final @NonNull TransferRepository transferRepository;

    public BankHandler(
        AccountRepository accountRepository,
        MessageRepository messageRepository,
        TransferRepository transferRepository
    ) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.messageRepository = Objects.requireNonNull(messageRepository);
        this.transferRepository = Objects.requireNonNull(transferRepository);
    }

    public Mono<ServerResponse> getAllTransfers(ServerRequest request) {
        return ServerResponse.ok().body(transferRepository.findAll(), Transfer.class);
    }

    public Mono<ServerResponse> getTransfer(ServerRequest request) {
        String endToEndId = request.pathVariable("endToEndId");
        return ServerResponse.ok().body(transferRepository.getByEndToEndId(endToEndId), Transfer.class);
    }

    public Mono<ServerResponse> getMessages(ServerRequest request) {
        String endToEndId = request.pathVariable("endToEndId");
        return ServerResponse.ok().body(messageRepository.findByEndToEndId(endToEndId), Transfer.class);
    }

    public Mono<ServerResponse> getAllIbans(ServerRequest request) {
        return ServerResponse.ok().body(
            accountRepository.findByClientId(clientId).map(Account::getIban).collectList(),
            new ParameterizedTypeReference<>() {}
        );
    }
}
