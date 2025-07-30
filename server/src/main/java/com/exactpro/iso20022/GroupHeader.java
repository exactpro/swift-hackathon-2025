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

    public GroupHeader() {
    }

    private GroupHeader(Builder builder) {
        this.messageId = builder.messageId;
        this.timestamp = builder.timestamp;
    }

    @NonNull
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
    }

    @NonNull
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String messageId;
        private Instant timestamp;

        public Builder messageId(@NonNull String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder timestamp(@NonNull Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public GroupHeader build() {
            if (messageId == null || messageId.isEmpty()) {
                throw new IllegalStateException("Message ID cannot be null or empty.");
            }
            if (timestamp == null) {
                throw new IllegalStateException("Timestamp cannot be null.");
            }

            return new GroupHeader(this);
        }
    }
}
