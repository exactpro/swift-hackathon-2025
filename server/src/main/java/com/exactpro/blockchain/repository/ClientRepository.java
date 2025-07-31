package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

public interface ClientRepository extends ReactiveCrudRepository<Client, Integer> {
    @NonNull Flux<Client> findByClientId(int clientId);
}
