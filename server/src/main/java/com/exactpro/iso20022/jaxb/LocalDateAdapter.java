package com.exactpro.iso20022.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Date field cannot be empty or null. Expected format: YYYY-MM-DD.");
        }
        try {
            return LocalDate.parse(v, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for '" + v + "'. Expected format: YYYY-MM-DD.", e);
        }
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        if (v == null) {
            throw new IllegalArgumentException("Cannot marshal a null LocalDate. Date field cannot be null.");
        }
        return v.format(FORMATTER);
    }
}