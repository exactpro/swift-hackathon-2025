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

    public Participant() {
    }

    public Participant(Builder builder) {
        this.fullName = builder.fullName;
        this.iban = builder.iban;
        this.bic = builder.bic;
    }

    @NonNull
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }

    @NonNull
    public String getIban() {
        return iban;
    }

    public void setIban(@NonNull String iban) {
        this.iban = iban;
    }

    @NonNull
    public String getBic() {
        return bic;
    }

    public void setBic(@NonNull String bic) {
        this.bic = bic;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fullName;
        private String iban;
        private String bic;

        public Builder fullName(@NonNull String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder iban(@NonNull String iban) {
            this.iban = iban;
            return this;
        }

        public Builder bic(@NonNull String bic) {
            this.bic = bic;
            return this;
        }

        public Participant build() {
            if (fullName == null || fullName.isEmpty()) {
                throw new IllegalStateException("Full Name cannot be null or empty.");
            }
            if (iban == null || iban.isEmpty()) {
                throw new IllegalStateException("IBAN cannot be null or empty.");
            }
            if (bic == null || bic.isEmpty()) {
                throw new IllegalStateException("BIC cannot be null or empty.");
            }

            return new Participant(this);
        }

    }
}
