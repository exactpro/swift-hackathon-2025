package com.exactpro.iso20022;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@XmlType(propOrder = {"endToEndId", "currency", "amount", "debtor", "creditor", "remittanceInfo"})
@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionInfo {
    @XmlElement(name = "EndToEndId")
    private @NonNull String endToEndId;

    @XmlElement(name = "IntrBkSttlmCcy")
    private @NonNull String currency;

    @XmlElement(name = "IntrBkSttlmAmt")
    private @NonNull BigDecimal amount;

    @XmlElement(name = "Dbtr")
    private @NonNull Participant debtor;

    @XmlElement(name = "Cdtr")
    private @NonNull Participant creditor;

    @XmlElement(name = "RmtInf")
    private @Nullable String remittanceInfo;
}
