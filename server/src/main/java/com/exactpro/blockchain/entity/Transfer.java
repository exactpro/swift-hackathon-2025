package com.exactpro.blockchain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

@Table("Transfer")
public class Transfer {
    @Id
    @Column("transferId")
    private Integer transferId;

    private BigDecimal amount;

    @Column("clientId")
    private Integer clientId;

    @Column("creditorBic")
    private String creditorBic;

    @Column("creditorFullName")
    private String creditorFullName;

    @Column("creditorIban")
    private String creditorIban;

    @Column("currencyCode")
    private String currencyCode;

    @Column("debtorBic")
    private String debtorBic;

    @Column("debtorFullName")
    private String debtorFullName;

    @Column("debtorIban")
    private String debtorIban;

    @Column("endToEndId")
    private String endToEndId;

    @Column("remittanceInfo")
    private String remittanceInfo;

    @Column("settlementDate")
    private LocalDate settlementDate;

    private TransferStatus status;

    @Column("transferTimestamp")
    private Instant transferTimestamp;

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getClientId() {
        return clientId;
    }

    public String getCreditorBic() {
        return creditorBic;
    }

    public String getCreditorFullName() {
        return creditorFullName;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getDebtorBic() {
        return debtorBic;
    }

    public String getDebtorFullName() {
        return debtorFullName;
    }

    public String getDebtorIban() {
        return debtorIban;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public String getRemittanceInfo() {
        return remittanceInfo;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public TransferStatus getStatus() {
        return status;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public Instant getTransferTimestamp() {
        return transferTimestamp;
    }

    public @NonNull Transfer withStatus(@NonNull TransferStatus newStatus) {
        Transfer updated = new Transfer();
        updated.transferId = this.transferId;
        updated.amount = this.amount;
        updated.clientId = this.clientId;
        updated.creditorBic = this.creditorBic;
        updated.creditorFullName = this.creditorFullName;
        updated.creditorIban = this.creditorIban;
        updated.currencyCode = this.currencyCode;
        updated.debtorBic = this.debtorBic;
        updated.debtorFullName = this.debtorFullName;
        updated.debtorIban = this.debtorIban;
        updated.endToEndId = this.endToEndId;
        updated.remittanceInfo = this.remittanceInfo;
        updated.settlementDate = this.settlementDate;
        updated.status = newStatus;
        updated.transferTimestamp = this.transferTimestamp;
        return updated;
    }

    protected Transfer() {
    }

    private Transfer(Builder builder) {
        this.amount = Objects.requireNonNull(builder.amount, "amount must not be null");
        this.clientId = builder.clientId;
        this.creditorBic = Objects.requireNonNull(builder.creditorBic, "creditorBic must not be null");
        this.creditorFullName = Objects.requireNonNull(builder.creditorFullName, "creditorFullName must not be null");
        this.creditorIban = Objects.requireNonNull(builder.creditorIban, "creditorIban must not be null");
        this.currencyCode = Objects.requireNonNull(builder.currencyCode, "currency must not be null");
        this.debtorBic = Objects.requireNonNull(builder.debtorBic, "debtorBic must not be null");
        this.debtorFullName = Objects.requireNonNull(builder.debtorFullName, "debtorFullName must not be null");
        this.debtorIban = Objects.requireNonNull(builder.debtorIban, "debtorIban must not be null");
        this.endToEndId = Objects.requireNonNull(builder.endToEndId, "endToEndId must not be null");
        this.remittanceInfo = builder.remittanceInfo;
        this.settlementDate = Objects.requireNonNull(builder.settlementDate, "settlementDate must not be null");
        this.status = Objects.requireNonNull(builder.status, "status must not be null");
        this.transferTimestamp = Objects.requireNonNull(builder.transferTimestamp, "transferTimestamp must not be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal amount;
        private Integer clientId;
        private String creditorBic;
        private String creditorFullName;
        private String creditorIban;
        private String currencyCode;
        private String debtorBic;
        private String debtorFullName;
        private String debtorIban;
        private String endToEndId;
        private String remittanceInfo;
        private LocalDate settlementDate;
        private TransferStatus status;
        private Instant transferTimestamp;

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder clientId(Integer clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder creditorBic(String creditorBic) {
            this.creditorBic = creditorBic;
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

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder debtorBic(String debtorBic) {
            this.debtorBic = debtorBic;
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

        public Builder endToEndId(String endToEndId) {
            this.endToEndId = endToEndId;
            return this;
        }

        public Builder remittanceInfo(String remittanceInfo) {
            this.remittanceInfo = remittanceInfo;
            return this;
        }

        public Builder settlementDate(LocalDate settlementDate) {
            this.settlementDate = settlementDate;
            return this;
        }

        public Builder status(TransferStatus status) {
            this.status = status;
            return this;
        }

        public Builder transferTimestamp(Instant transferTimestamp) {
            this.transferTimestamp = transferTimestamp;
            return this;
        }

        public Transfer build() {
            return new Transfer(this);
        }
    }
}

