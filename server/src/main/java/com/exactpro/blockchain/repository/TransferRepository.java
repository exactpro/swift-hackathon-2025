package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Transfer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

public interface TransferRepository extends ReactiveCrudRepository<Transfer, Integer> {
    @NonNull Flux<Transfer> findByTransferId(int transferId);

    @NonNull Flux<Transfer> findByClientId(int clientId);

    default @NonNull Mono<Transfer> getByTransferId(int transferId) {
        return
            findByTransferId(transferId)
            .single()
            .onErrorMap(
                NoSuchElementException.class,
                error -> new Exception(String.format("Transfer with transferId '%d' doesn't exist", transferId), error)
            );
    }
}
