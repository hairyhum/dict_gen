package dict_gen;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class document_data_write_test {

    @Test
    void write_ok_test() throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
        DocumentData doc = new DocumentData();
        doc.DocumentName = "Doc name";
        doc.Version = "Version";
        doc.FileName = "file.xml";
        doc.Length = 100;

        StringWriter writer_client = new StringWriter();
        StringWriter writer_server = new StringWriter();
            XMLOutputFactory write_factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xml_writer_client = write_factory.createXMLStreamWriter(writer_client);
            XMLStreamWriter xml_writer_server = write_factory.createXMLStreamWriter(writer_server);

            xml_writer_client.writeStartDocument();
            doc.WriteXmlElement(xml_writer_client, false);
            xml_writer_server.writeEndDocument();

            xml_writer_server.writeStartDocument();
            doc.WriteXmlElement(xml_writer_server, true);
            xml_writer_server.writeEndDocument();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            String result_client = writer_client.toString();

            DocumentBuilder builder_client = factory.newDocumentBuilder();
            Document dom_client = builder_client.parse(new InputSource(new StringReader(result_client)));

            Element root_client = dom_client.getDocumentElement();
            Assertions.assertEquals("DOCUMENT", root_client.getNodeName());

            Node doc_name = root_client.getElementsByTagName("DOCUMENT_NAME").item(0);
            Assertions.assertEquals(doc.DocumentName, doc_name.getTextContent());

            Node version = root_client.getElementsByTagName("VERSION").item(0);
            Assertions.assertEquals(doc.Version, version.getTextContent());

            Assertions.assertEquals(0, root_client.getElementsByTagName("PATH_TO_FILE").getLength());


            Node length = root_client.getElementsByTagName("LENGTH").item(0);
            Assertions.assertEquals(Integer.toString(doc.Length), length.getTextContent());


            String result_server = writer_server.toString();

            DocumentBuilder builder_server = factory.newDocumentBuilder();
            Document dom_server = builder_server.parse(new InputSource(new StringReader(result_server)));

            Element root_server = dom_server.getDocumentElement();
            Assertions.assertEquals("DOCUMENT", root_server.getNodeName());

            doc_name = root_server.getElementsByTagName("DOCUMENT_NAME").item(0);
            Assertions.assertEquals(doc.DocumentName, doc_name.getTextContent());

            version = root_server.getElementsByTagName("VERSION").item(0);
            Assertions.assertEquals(doc.Version, version.getTextContent());

            Node path = root_server.getElementsByTagName("PATH_TO_FILE").item(0);
            Assertions.assertEquals(doc.FileName, path.getTextContent());


            length = root_server.getElementsByTagName("LENGTH").item(0);
            Assertions.assertEquals(Integer.toString(doc.Length), length.getTextContent());

    }

}
