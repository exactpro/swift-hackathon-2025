package com.exactpro.blockchain.api.bank;

import com.exactpro.blockchain.entity.Account;
import com.exactpro.blockchain.entity.Transfer;
import com.exactpro.blockchain.repository.AccountRepository;
import com.exactpro.blockchain.repository.TransferRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class BankHandler {
    private final @NonNull AccountRepository accountRepository;
    private final @NonNull TransferRepository transferRepository;

    public BankHandler(
        AccountRepository accountRepository,
        TransferRepository transferRepository
    ) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.transferRepository = Objects.requireNonNull(transferRepository);
    }

    public Mono<ServerResponse> getAllTransfers(ServerRequest request) {
        return ServerResponse.ok().body(transferRepository.findAll(), Transfer.class);
    }

    public Mono<ServerResponse> getAllIbans(ServerRequest request) {
        return ServerResponse.ok().body(
            accountRepository.findAll().map(Account::getIban).collectList(),
            new ParameterizedTypeReference<>() {}
        );
    }
}
