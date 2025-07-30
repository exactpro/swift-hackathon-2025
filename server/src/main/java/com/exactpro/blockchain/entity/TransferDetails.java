package com.exactpro.blockchain.entity;

import java.math.BigDecimal;

public class TransferDetails {
    private String accountID;
    private String bic;
    private String iban;
    private String currency;
    private BigDecimal amount;

    public TransferDetails() {
    }

    public TransferDetails(String accountID, String bic, String iban, String currency, BigDecimal amount) {
        this.accountID = accountID;
        this.bic = bic;
        this.iban = iban;
        this.currency = currency;
        this.amount = amount;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
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
