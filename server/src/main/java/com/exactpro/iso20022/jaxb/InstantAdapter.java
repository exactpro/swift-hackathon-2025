package com.exactpro.iso20022.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class InstantAdapter extends XmlAdapter<String, Instant> {
    @Override
    public Instant unmarshal(String v) throws Exception {
        try {
            // XXX ISO20022 allows timestamp without zone qualifier
            return DateTimeFormatter.ISO_INSTANT.parse(v, Instant::from);
        } catch (DateTimeParseException ex) {
            throw new Exception(
                String.format("Invalid timestamp '%s': must match ISO_INSTANT format (e.g. 2025-07-28T18:00:00Z)", v),
                ex
            );
        }
    }

    @Override
    public String marshal(Instant v) {
        return DateTimeFormatter.ISO_INSTANT.format(v);
    }
}
