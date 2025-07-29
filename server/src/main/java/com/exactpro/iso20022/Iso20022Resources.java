package com.exactpro.iso20022;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class Iso20022Resources {
    private static final String PACS_008_SCHEMA = "pacs.008.001.13.xsd";

    private static final String PACS_008_INPUT_XSLT = "pacs.008.001.13.input.xslt";

    private static final String PACS_008_OUTPUT_XSLT = "pacs.008.001.13.output.xslt";

    private final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    @Bean
    public @NonNull Schema pacs008Schema() throws IOException, SAXException {
        try (InputStream inputStream = getResourceStream(PACS_008_SCHEMA)) {
            return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(new StreamSource(getResourceStream(PACS_008_SCHEMA)));
        }
    }

    @Bean("pacs008InputXslt")
    public @NonNull Templates pacs008InputXslt() throws IOException, TransformerConfigurationException {
        try (InputStream inputStream = getResourceStream(PACS_008_INPUT_XSLT)) {
            return transformerFactory.newTemplates(new StreamSource(inputStream));
        }
    }

    @Bean("pacs008OutputXslt")
    public @NonNull Templates pacs008OutputXslt() throws IOException, TransformerConfigurationException {
        try (InputStream inputStream = getResourceStream(PACS_008_OUTPUT_XSLT)) {
            return transformerFactory.newTemplates(new StreamSource(inputStream));
        }
    }

    private @NonNull InputStream getResourceStream(@NonNull String resourceName) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new IOException(String.format("Resource %s isn't found", resourceName));
        }
        return inputStream;
    }
}
