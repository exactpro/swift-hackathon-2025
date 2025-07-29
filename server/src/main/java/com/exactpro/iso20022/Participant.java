package com.exactpro.iso20022;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.springframework.lang.NonNull;

@XmlType(propOrder = {"fullName", "iban", "bic"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Participant {
    @XmlElement(name = "Nm")
    private @NonNull String fullName;

    @XmlElement(name = "IBAN")
    private @NonNull String iban;

    @XmlElement(name = "BICFI")
    private @NonNull String bic;
}
