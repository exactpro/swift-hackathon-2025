package com.exactpro.blockchain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

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

    public DltMessage() {
    }

    public DltMessage(Builder builder) {
        this.endToEndId = builder.endToEndId;
        this.currencyId = builder.currencyId;
        this.amount = builder.amount;
        this.debtorAddress = builder.debtorAddress;
        this.creditorAddress = builder.creditorAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String endToEndId;

        private String currencyId;

        private BigDecimal amount;

        private String debtorAddress;

        private String creditorAddress;

        public Builder endToEndId(String endToEndId) {
            this.endToEndId = endToEndId;
            return this;
        }

        public Builder currencyId(String currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder debtorAddress(String debtorAddress) {
            this.debtorAddress = debtorAddress;
            return this;
        }

        public Builder creditorAddress(String creditorAddress) {
            this.creditorAddress = creditorAddress;
            return this;
        }

        public DltMessage build() {
            return new DltMessage(this);
        }
    }
}
