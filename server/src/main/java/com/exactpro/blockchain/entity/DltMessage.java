package com.exactpro.blockchain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class DltMessage {
    @JsonProperty("endToEndId")
    private String endToEndId;

    @JsonProperty("currencyId")
    private String currencyId;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("debtorAddress")
    private String debtorAddress;

    @JsonProperty("creditorAddress")
    private String creditorAddress;
}
