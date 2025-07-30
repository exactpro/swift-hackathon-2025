package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Transfer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

public interface TransferRepository extends ReactiveCrudRepository<Transfer, Integer> {
    @NonNull
    Flux<Transfer> findByTransferId(int transferId);
}
