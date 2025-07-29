package com.exactpro.blockchain.api.client;

import com.exactpro.blockchain.repository.AccountRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ClientHandler {
    private final AccountRepository accountRepository;

    public ClientHandler(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Mono<ServerResponse> getAccountsByClientId(ServerRequest request) {
        int clientId = Integer.parseInt(request.pathVariable("clientId"));
        return ServerResponse.ok().body(accountRepository.findByClientId(clientId), com.exactpro.blockchain.entity.Account.class);
    }
}
