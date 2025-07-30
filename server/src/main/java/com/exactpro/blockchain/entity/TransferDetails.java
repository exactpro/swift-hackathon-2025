package com.exactpro.blockchain.entity;

import java.math.BigDecimal;

public class TransferDetails {
    private String accountID;
    private String selfIban;
    private String targetFullName;
    private String targetBic;
    private String targetIban;
    private String currency;
    private BigDecimal amount;

    public TransferDetails() {
    }

    public TransferDetails(String accountID,
                           String selfIban,
                           String targetFullName,
                           String targetBic,
                           String targetIban,
                           String currency,
                           BigDecimal amount) {
        this.accountID = accountID;
        this.selfIban = selfIban;
        this.targetFullName = targetFullName;
        this.targetBic = targetBic;
        this.targetIban = targetIban;
        this.currency = currency;
        this.amount = amount;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getSelfIban() {
        return selfIban;
    }

    public void setSelfIban(String selfIban) {
        this.selfIban = selfIban;
    }

    public String getTargetFullName() {
        return targetFullName;
    }

    public void setTargetFullName(String targetFullName) {
        this.targetFullName = targetFullName;
    }

    public String getTargetBic() {
        return targetBic;
    }

    public void setTargetBic(String targetBic) {
        this.targetBic = targetBic;
    }

    public String getTargetIban() {
        return targetIban;
    }

    public void setTargetIban(String targetIban) {
        this.targetIban = targetIban;
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
}
