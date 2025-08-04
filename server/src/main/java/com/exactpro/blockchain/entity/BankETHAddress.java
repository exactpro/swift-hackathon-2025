package com.exactpro.blockchain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("BankETHAddress")
public class BankETHAddress {
    @Id
    private String bic;

    @Column("ethAddress")
    private String ethAddress;

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }
}
