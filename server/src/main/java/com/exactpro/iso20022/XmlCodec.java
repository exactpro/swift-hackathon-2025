package com.exactpro.iso20022;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshallerHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Objects;

@Component
public class XmlCodec {
    private final @NonNull JAXBContext jaxbContext;

    private final @NonNull Schema pacs008Schema;

    private final @NonNull Templates pacs008InputXslt;

    private final @NonNull Templates pacs008OutputXslt;

    private final @NonNull DOMImplementation domImplementation;

    public XmlCodec(
        @NonNull Schema pacs008Schema,
        @NonNull @Qualifier("pacs008InputXslt") Templates pacs008InputXslt,
        @NonNull @Qualifier("pacs008OutputXslt") Templates pacs008OutputXslt
    )
        throws JAXBException, ParserConfigurationException
    {
        this.pacs008Schema = Objects.requireNonNull(pacs008Schema);
        this.pacs008InputXslt = Objects.requireNonNull(pacs008InputXslt);
        this.pacs008OutputXslt = Objects.requireNonNull(pacs008OutputXslt);
        this.jaxbContext = JAXBContext.newInstance(CustomerCreditTransfer.class);
        this.domImplementation = DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation();
    }

    public @NonNull CustomerCreditTransfer decode(@NonNull String xml)
        throws IOException, JAXBException, SAXException, TransformerException
    {
        Validator validator = pacs008Schema.newValidator();
        validator.validate(new StreamSource(new StringReader(xml)));

        var saxResult = new SAXResult();
        UnmarshallerHandler unmarshallerHandler = jaxbContext.createUnmarshaller().getUnmarshallerHandler();
        saxResult.setHandler(unmarshallerHandler);
        pacs008InputXslt.newTransformer().transform(
            new StreamSource(new StringReader(xml)),
            saxResult
        );
        return (CustomerCreditTransfer) unmarshallerHandler.getResult();
    }

    public @NonNull String encode(@NonNull CustomerCreditTransfer entity)
        throws JAXBException, TransformerException
    {
        Document document = domImplementation.createDocument(null, null, null);
        jaxbContext.createMarshaller().marshal(entity, document);
        Transformer transformer = pacs008OutputXslt.newTransformer();
        var stringWriter = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
        return stringWriter.toString();
    }
}