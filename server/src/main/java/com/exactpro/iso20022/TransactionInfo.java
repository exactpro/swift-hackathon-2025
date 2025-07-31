package com.exactpro.iso20022;

import com.exactpro.iso20022.jaxb.LocalDateAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;

@XmlType(propOrder = {"endToEndId", "currency", "amount", "settlementDate", "debtor", "creditor", "remittanceInfo"})
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionInfo {
    @XmlElement(name = "EndToEndId")
    private @NonNull String endToEndId;

    @XmlElement(name = "IntrBkSttlmCcy")
    private @NonNull String currency;

    @XmlElement(name = "IntrBkSttlmAmt")
    private @NonNull BigDecimal amount;

    @XmlElement(name = "IntrBkSttlmDt")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private @NonNull LocalDate settlementDate;

    @XmlElement(name = "Dbtr")
    private @NonNull Participant debtor;

    @XmlElement(name = "Cdtr")
    private @NonNull Participant creditor;

    @XmlElement(name = "RmtInf")
    private @Nullable String remittanceInfo;

    public TransactionInfo() {
    }

    private TransactionInfo(Builder builder) {
        this.endToEndId = builder.endToEndId;
        this.currency = builder.currency;
        this.amount = builder.amount;
        this.settlementDate = builder.settlementDate;
        this.debtor = builder.debtor;
        this.creditor = builder.creditor;
        this.remittanceInfo = builder.remittanceInfo;
    }

    @NonNull
    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(@NonNull String endToEndId) {
        this.endToEndId = endToEndId;
    }

    @NonNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@NonNull String currency) {
        this.currency = currency;
    }

    @NonNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NonNull BigDecimal amount) {
        this.amount = amount;
    }

    @NonNull
    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(@NonNull LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    @NonNull
    public Participant getDebtor() {
        return debtor;
    }

    public void setDebtor(@NonNull Participant debtor) {
        this.debtor = debtor;
    }

    @NonNull
    public Participant getCreditor() {
        return creditor;
    }

    public void setCreditor(@NonNull Participant creditor) {
        this.creditor = creditor;
    }

    @Nullable
    public String getRemittanceInfo() {
        return remittanceInfo;
    }

    public void setRemittanceInfo(@Nullable String remittanceInfo) {
        this.remittanceInfo = remittanceInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String endToEndId;
        private String currency;
        private BigDecimal amount;
        private LocalDate settlementDate;
        private Participant debtor;
        private Participant creditor;
        private String remittanceInfo;

        public Builder endToEndId(@NonNull String endToEndId) {
            this.endToEndId = endToEndId;
            return this;
        }

        public Builder currency(@NonNull String currency) {
            this.currency = currency;
            return this;
        }

        public Builder amount(@NonNull BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder settlementDate(@NonNull LocalDate settlementDate) {
            this.settlementDate = settlementDate;
            return this;
        }

        public Builder debtor(@NonNull Participant debtor) {
            this.debtor = debtor;
            return this;
        }

        public Builder creditor(@NonNull Participant creditor) {
            this.creditor = creditor;
            return this;
        }

        public Builder remittanceInfo(@Nullable String remittanceInfo) {
            this.remittanceInfo = remittanceInfo;
            return this;
        }

        public TransactionInfo build() {
            if (endToEndId == null || endToEndId.isEmpty()) {
                throw new IllegalStateException("EndToEndID cannot be null or empty.");
            }
            if (currency == null || currency.isEmpty()) {
                throw new IllegalStateException("Currency cannot be null or empty.");
            }
            if (amount == null) {
                throw new IllegalStateException("Amount cannot be null.");
            }
            if (settlementDate == null) {
                throw new IllegalStateException("SettlementDate cannot be null.");
            }
            if (debtor == null) {
                throw new IllegalStateException("Debtor cannot be null.");
            }
            if (creditor == null) {
                throw new IllegalStateException("Creditor cannot be null.");
            }

            return new TransactionInfo(this);
        }
    }
}
