package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    Flux<Account> findByClientId(Integer clientId);
}
