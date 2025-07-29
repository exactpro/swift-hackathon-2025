package com.exactpro.iso20022;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {XmlCodec.class, Iso20022Resources.class})
class XmlCodecTest {
    @Autowired
    private XmlCodec xmlCodec;

    private String pacs008XmlContent;

    @BeforeEach
    void setUp() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("pacs.008.xml")) {
            Objects.requireNonNull(is, "pacs.008.xml resource not found");
            pacs008XmlContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void decode_validPacs008Xml_noExceptionThrown() {
        assertDoesNotThrow(() -> {
            var foo = xmlCodec.decode(pacs008XmlContent);
            var bar = xmlCodec.encode(foo);
            System.out.println(bar);
            xmlCodec.decode(bar);
        });
    }
}
