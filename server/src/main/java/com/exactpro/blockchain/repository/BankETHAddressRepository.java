package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.BankETHAddress;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

public interface BankETHAddressRepository extends ReactiveCrudRepository<BankETHAddress, String> {
    @NonNull Flux<BankETHAddress> findByBic(String bic);
}
