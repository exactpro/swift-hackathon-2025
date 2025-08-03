package com.exactpro.blockchain.entity;

import java.math.BigDecimal;

public class TransferRequest {
    private String debtorIban;
    private String creditorFullName;
    private String creditorBic;
    private String creditorIban;
    private String currencyCode;
    private BigDecimal amount;
    private String comment;

    public TransferRequest() {
    }

    public TransferRequest(String debtorIban,
                           String creditorFullName,
                           String creditorBic,
                           String creditorIban,
                           String currencyCode,
                           BigDecimal amount,
                           String comment) {
        this.debtorIban = debtorIban;
        this.creditorFullName = creditorFullName;
        this.creditorBic = creditorBic;
        this.creditorIban = creditorIban;
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.comment = comment;
    }

    public String getDebtorIban() {
        return debtorIban;
    }

    public void setDebtorIban(String debtorIban) {
        this.debtorIban = debtorIban;
    }

    public String getCreditorFullName() {
        return creditorFullName;
    }

    public void setCreditorFullName(String creditorFullName) {
        this.creditorFullName = creditorFullName;
    }

    public String getCreditorBic() {
        return creditorBic;
    }

    public void setCreditorBic(String creditorBic) {
        this.creditorBic = creditorBic;
    }

    public String getCreditorIban() {
        return creditorIban;
    }

    public void setCreditorIban(String creditorIban) {
        this.creditorIban = creditorIban;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
