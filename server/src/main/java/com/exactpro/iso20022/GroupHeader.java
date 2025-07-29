package com.exactpro.iso20022;

import com.exactpro.iso20022.jaxb.InstantAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.springframework.lang.NonNull;

import java.time.Instant;

@XmlType(propOrder = {"messageId", "timestamp"})
@XmlAccessorType(XmlAccessType.FIELD)
public class GroupHeader {
    @XmlElement(name = "MsgId")
    private @NonNull String messageId;

    @XmlElement(name = "CreDtTm")
    @XmlJavaTypeAdapter(InstantAdapter.class)
    private @NonNull Instant timestamp;
}
