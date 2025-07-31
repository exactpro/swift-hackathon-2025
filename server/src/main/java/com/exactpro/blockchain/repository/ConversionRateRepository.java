package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.ConversionRate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;

public interface ConversionRateRepository extends ReactiveCrudRepository<ConversionRate, Integer> {
    @NonNull Flux<ConversionRate> findByBaseCurrencyAndTargetCurrency(String baseCurrency, String targetCurrency);
}
