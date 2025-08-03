package com.exactpro.blockchain.repository;

import com.exactpro.blockchain.entity.Currency;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

public interface CurrencyRepository extends R2dbcRepository<Currency, String> {
    @Query("SELECT cc.code, t.address FROM CurrencyCode cc LEFT JOIN Token t ON cc.code = t.currencyCode WHERE cc.code = :currencyCode")
    Mono<Currency> findByCurrencyCode(@Param("currencyCode") String currencyCode);

    default Mono<Currency> getByCurrencyCode(String currencyCode) {
        return
            findByCurrencyCode(currencyCode)
            .single()
            .onErrorMap(
                NoSuchElementException.class,
                error -> new Exception(String.format("Currency code '%s' is invalid", currencyCode), error)
            );
    }
}
