package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

public interface ClientRepository extends ReactiveCrudRepository<Client, Integer> {
    @NonNull Flux<Client> findByClientId(int clientId);

    default @NonNull Mono<Client> getByClientId(int clientId) {
        return
            findByClientId(clientId)
            .single()
            .onErrorMap(
                NoSuchElementException.class,
                error -> new Exception(String.format("Client with id %d doesn't exist", clientId), error)
            );
    }
}
