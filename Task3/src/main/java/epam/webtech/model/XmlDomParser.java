package epam.webtech.model;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public interface XmlDomParser {

    default Document buildDocument(File file) throws IOException, ParserConfigurationException,
            org.xml.sax.SAXException {
        Document document;
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        document = documentBuilder.parse(file);
        document.normalize();
        return document;
    }
}
