package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveCrudRepository<Account, String> {
    @NonNull Flux<Account> findByClientId(int clientId);
    @NonNull Flux<Account> findByIban(String iban);
    @NonNull Flux<Account> findByClientIdAndIban(int clientId, String iban);
}
