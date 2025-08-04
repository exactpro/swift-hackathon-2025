package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.BankETHAddress;
import com.exactpro.blockchain.entity.Client;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

public interface BankETHAddressRepository extends ReactiveCrudRepository<BankETHAddress, String> {
    @NonNull Flux<BankETHAddress> findByBic(String bic);

    default @NonNull Mono<BankETHAddress> getByBic(String bic) {
        return
            findByBic(bic)
            .single()
            .onErrorMap(
                NoSuchElementException.class,
                error -> new Exception(String.format("No Ethereum address is associated with bank %s", bic), error)
            );
    }
}
