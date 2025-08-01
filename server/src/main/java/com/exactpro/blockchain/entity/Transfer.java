package com.exactpro.blockchain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Table("Transfer")
public class Transfer {
    @Id
    @Column("transferId")
    private Integer transferId;
    @Column("clientId")
    private Integer clientId;
    private TransferStatus status;
    @Column("messageId")
    private String messageId;
    @Column("transferTimestamp")
    private Instant transferTimestamp;
    @Column("endToEndId")
    private String endToEndId;
    private String currency;
    private BigDecimal amount;
    @Column("settlementDate")
    private LocalDate settlementDate;
    @Column("debtorFullName")
    private String debtorFullName;
    @Column("debtorIban")
    private String debtorIban;
    @Column("debtorBic")
    private String debtorBic;
    @Column("creditorFullName")
    private String creditorFullName;
    @Column("creditorIban")
    private String creditorIban;
    @Column("creditorBic")
    private String creditorBic;
    @Column("remittanceInfo")
    private String remittanceInfo;

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public void setStatus(TransferStatus status) {
        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Instant getTransferTimestamp() {
        return transferTimestamp;
    }

    public void setTransferTimestamp(Instant transferTimestamp) {
        this.transferTimestamp = transferTimestamp;
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

    public Transfer() {
    }

    public Transfer(Builder builder) {
        this.clientId = builder.clientId;
        this.status = builder.status;
        this.messageId = builder.messageId;
        this.transferTimestamp = builder.transferTimestamp;
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
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer clientId;
        private TransferStatus status;
        private String messageId;
        private Instant transferTimestamp;
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

        public Builder clientId(Integer clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder status(TransferStatus status) {
            this.status = status;
            return this;
        }

        public Builder messageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder transferTimestamp(Instant transferTimestamp) {
            this.transferTimestamp = transferTimestamp;
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

        public Transfer build() {
            return new Transfer(this);
        }
    }
}
