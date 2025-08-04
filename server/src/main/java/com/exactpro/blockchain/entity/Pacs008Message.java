package com.exactpro.blockchain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public class Pacs008Message {

    @JsonProperty("MsgId")
    private String messageId;

    @JsonProperty("CreDtTm")
    private Instant timestamp;

    @JsonProperty("EndToEndId")
    private String endToEndId;

    @JsonProperty("IntrBkSttlmCcy")
    private String currency;

    @JsonProperty("IntrBkSttlmAmt")
    private BigDecimal amount;

    @JsonProperty("IntrBkSttlmDt")
    private LocalDate settlementDate;

    @JsonProperty("DebtorNm")
    private String debtorFullName;

    @JsonProperty("DebtorIBAN")
    private String debtorIban;

    @JsonProperty("DebtorBICFI")
    private String debtorBic;

    @JsonProperty("CreditorNm")
    private String creditorFullName;

    @JsonProperty("CreditorIBAN")
    private String creditorIban;

    @JsonProperty("CreditorBICFI")
    private String creditorBic;

    @JsonProperty("RmtInf")
    private String remittanceInfo;

    private String chargeBearer;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getDebtorFullName() {
        return debtorFullName;
    }

    public void setDebtorFullName(String debtorFullName) {
        this.debtorFullName = debtorFullName;
    }

    public String getDebtorIban() {
        return debtorIban;
    }

    public void setDebtorIban(String debtorIban) {
        this.debtorIban = debtorIban;
    }

    public String getDebtorBic() {
        return debtorBic;
    }

    public void setDebtorBic(String debtorBic) {
        this.debtorBic = debtorBic;
    }

    public String getCreditorFullName() {
        return creditorFullName;
    }

    public void setCreditorFullName(String creditorFullName) {
        this.creditorFullName = creditorFullName;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public void setCreditorIban(String creditorIban) {
        this.creditorIban = creditorIban;
    }

    public String getCreditorBic() {
        return creditorBic;
    }

    public void setCreditorBic(String creditorBic) {
        this.creditorBic = creditorBic;
    }

    public String getRemittanceInfo() {
        return remittanceInfo;
    }

    public void setRemittanceInfo(String remittanceInfo) {
        this.remittanceInfo = remittanceInfo;
    }

    @JsonProperty("ChrgBr")
    public String getChargeBearer() {
        return chargeBearer != null ? chargeBearer : "SLEV";
    }

    public void setChargeBearer(String chargeBearer) {
        this.chargeBearer = chargeBearer;
    }

    public Pacs008Message() {
    }

    public Pacs008Message(Builder builder) {
        this.messageId = builder.messageId;
        this.timestamp = builder.timestamp;
        this.endToEndId = builder.endToEndId;
        this.currency = builder.currency;
        this.amount = builder.amount;
        this.settlementDate = builder.settlementDate;
        this.debtorFullName = builder.debtorFullName;
        this.debtorIban = builder.debtorIban;
        this.debtorBic = builder.debtorBic;
        this.creditorFullName = builder.creditorFullName;
        this.creditorIban = builder.creditorIban;
        this.creditorBic = builder.creditorBic;
        this.remittanceInfo = builder.remittanceInfo;
        this.chargeBearer = builder.chargeBearer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String messageId;

        private Instant timestamp;

        private String endToEndId;

        private String currency;

        private BigDecimal amount;

        private LocalDate settlementDate;

        private String debtorFullName;

        private String debtorIban;

        private String debtorBic;

        private String creditorFullName;

        private String creditorIban;

        private String creditorBic;

        private String remittanceInfo;

        private String chargeBearer;

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder endToEndId(String endToEndId) {
            this.endToEndId = endToEndId;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder settlementDate(LocalDate settlementDate) {
            this.settlementDate = settlementDate;
            return this;
        }

        public Builder debtorFullName(String debtorFullName) {
            this.debtorFullName = debtorFullName;
            return this;
        }

        public Builder debtorIban(String debtorIban) {
            this.debtorIban = debtorIban;
            return this;
        }

        public Builder debtorBic(String debtorBic) {
            this.debtorBic = debtorBic;
            return this;
        }

        public Builder creditorFullName(String creditorFullName) {
            this.creditorFullName = creditorFullName;
            return this;
        }

        public Builder creditorIban(String creditorIban) {
            this.creditorIban = creditorIban;
            return this;
        }

        public Builder creditorBic(String creditorBic) {
            this.creditorBic = creditorBic;
            return this;
        }

        public Builder remittanceInfo(String remittanceInfo) {
            this.remittanceInfo = remittanceInfo;
            return this;
        }

        public Builder chargeBearer(String chargeBearer) {
            this.chargeBearer = chargeBearer;
            return this;
        }

        public Pacs008Message build() {
            return new Pacs008Message(this);
        }

    }

}
