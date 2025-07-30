package com.exactpro.iso20022;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import org.springframework.lang.NonNull;

import java.util.List;

@XmlRootElement(name = "FIToFICstmrCdtTrf")
@XmlType(propOrder = {"groupHeader", "transactionInfos"})
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerCreditTransfer {
    @XmlElement(name = "GrpHdr")
    private @NonNull GroupHeader groupHeader;

    @XmlElement(name = "CdtTrfTxInf")
    private @NonNull List<TransactionInfo> transactionInfos;

    @NonNull
    public GroupHeader getGroupHeader() {
        return groupHeader;
    }

    @NonNull
    public List<TransactionInfo> getTransactionInfos() {
        return transactionInfos;
    }

    public CustomerCreditTransfer() {
    }

    public CustomerCreditTransfer(@NonNull GroupHeader groupHeader, @NonNull List<TransactionInfo> transactionInfos) {
        this.groupHeader = groupHeader;
        this.transactionInfos = transactionInfos;
    }
}
