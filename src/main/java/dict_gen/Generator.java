package dict_gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class Generator {
    public static void main(String[] args) throws Exception {
        if(args.length != 3) {
            throw new Exception("Invalid arguments. Expected 3 arguments: source directory, client filename and server filename");
        }
        String src_dir = args[0];
        String dest_client = args[1];
        String dest_server = args[2];
        Generate(src_dir, dest_client, dest_server);
    }

    public static void Generate(String src_dir,
                                String dest_client,
                                String dest_server)
            throws XMLStreamException, IOException, DocumentParsingException {

        File[] xml_files = getXmlFiles(src_dir);

        try(FileWriter client_writer = new FileWriter(dest_client);
            FileWriter server_writer = new FileWriter(dest_server)){

            XMLOutputFactory write_factory = XMLOutputFactory.newInstance();
            XMLStreamWriter xml_client_writer =
                    write_factory.createXMLStreamWriter(client_writer);
            XMLStreamWriter xml_server_writer =
                    write_factory.createXMLStreamWriter(server_writer);

            writeDictionaryHeader(xml_client_writer);
            writeDictionaryHeader(xml_server_writer);

            XMLInputFactory read_factory = XMLInputFactory.newInstance();

            for (File file : xml_files) {
                try {
                    DocumentData doc = new DocumentData(file, read_factory);
                    doc.WriteXmlElement(xml_client_writer, false);
                    doc.WriteXmlElement(xml_server_writer, true);
                }
                catch (IOException | XMLStreamException ex) {
                    System.err.printf("Error reading the xml file: %s. Reason: %s%n",
                                      file.getName(), ex.getMessage());
                    ex.printStackTrace();
                }
                catch (DocumentParsingException ex) {
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                }
            }

            writeDictionaryFooter(xml_client_writer);
            writeDictionaryFooter(xml_server_writer);

            flushAndClose(xml_client_writer);
            flushAndClose(xml_server_writer);
        }
    }

    static void writeDictionaryFooter(XMLStreamWriter writer) throws XMLStreamException{
        writer.writeEndElement();
        writer.writeEndDocument();
    }

    static void writeDictionaryHeader(XMLStreamWriter writer) throws XMLStreamException{
        writer.writeStartDocument();
        writer.writeStartElement("DICTIONARY");
    }

    static void flushAndClose(XMLStreamWriter writer) throws XMLStreamException{
        writer.flush();
        writer.close();
    }

    static File[] getXmlFiles(String src_dir) {
        File dir = new File(src_dir);
        return dir.listFiles((d, name) -> name.endsWith(".xml"));
    }
}