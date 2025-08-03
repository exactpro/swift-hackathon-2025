package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    @NonNull Flux<Account> findByClientId(int clientId);
    @NonNull Flux<Account> findByIban(String iban);
    @NonNull Flux<Account> findByClientIdAndIban(int clientId, String iban);

    default Mono<Account> getByClientIdAndIban(int clientId, String iban) {
        return
            findByClientIdAndIban(clientId, iban)
            .single()
            .onErrorMap(
                NoSuchElementException.class,
                error -> new Exception(String.format("Client %d doesn't have account '%s'", clientId, iban), error)
            );
    }
}
