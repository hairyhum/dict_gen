package dict_gen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

class DocumentData {
    public String DocumentName = null;
    public String Version = null;
    public int Length = -1;
    public String FileName;

    public DocumentData() {
    	// This data is invalid. Validate will throw.
    }
    public DocumentData(File file, XMLInputFactory read_factory)
            throws XMLStreamException, DocumentParsingException, IOException {
        try(FileReader file_reader = new FileReader(file)){
            XMLEventReader event_reader =
                    read_factory.createXMLEventReader(file_reader);
            String current_element = null;
            boolean in_document = false;
            // Enter the root element DOCUMENT:
            while(event_reader.hasNext()) {
                XMLEvent event = event_reader.nextEvent();
                if(event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElement start = event.asStartElement();
                    String element_name = start.getName().getLocalPart().toLowerCase();
                    if(element_name.equals("document")) {
                        in_document = true;
                        break;
                    } else {
                        throw new DocumentParsingException(file, String.format("Expected root element 'DOCUMENT', found '%s'", element_name));
                    }
                }
            }

            if(! in_document) {
                throw new DocumentParsingException(file, "Root element 'DOCUMENT' is missing");
            }

            while(event_reader.hasNext()) {
                XMLEvent event = event_reader.nextEvent();
                switch(event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement start = event.asStartElement();
                        String element_name = start.getName().getLocalPart().toLowerCase();
                        if(current_element == null) {
                            current_element = element_name;
                        } else {
                            int line_number = event.getLocation().getLineNumber();
                            String error = String.format("Child element %s on line %d",
                                                         element_name, line_number);
                            throw new DocumentParsingException(file, error);
                        }
                        switch(element_name){
                            case "document_name":
                                this.DocumentName = "";
                                break;
                            case "version":
                                this.Version = "";
                                break;
                            case "data":
                                this.Length = 0;
                                break;
                            default:
                                Location loc = event.getLocation();
                                String error = String.format("Unknown element %s on line %d",
                                                             current_element, loc.getLineNumber());
                                throw new DocumentParsingException(file, error);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = event.asCharacters();
                        if(current_element != null) {
                            switch(current_element) {
                                case "document_name":
                                    this.DocumentName = characters.getData();
                                    break;
                                case "version":
                                    this.Version = characters.getData();
                                    break;
                                case "data":
                                    this.Length = characters.getData().length();
                                    break;
                                default:
                                    // Technically, we should not get here
                                    break;
                            }
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        EndElement end = event.asEndElement();
                        String end_element_name = end.getName().getLocalPart().toLowerCase();
                        if(end_element_name.equals("document")) {
                            in_document = false;
                        }
                        current_element = null;
                        break;
                    default:
                        // Do nothing
                        break;
                }
                if(! in_document) {
                    break;
                }
            }
            event_reader.close();
            this.FileName = file.getAbsolutePath();
            this.Validate();
        }
    }
    public void Validate() throws DocumentParsingException {
        if(DocumentName == null) {
            throw new DocumentParsingException(this.FileName, "Missing document_name tag");
        }
        if(Version == null) {
            throw new DocumentParsingException(this.FileName, "Missing version tag");
        }
        if(Length == -1) {
            throw new DocumentParsingException(this.FileName, "Missing data tag");
        }
    }

    void WriteXmlElement(XMLStreamWriter xml_writer, boolean write_filename)
            throws XMLStreamException {
        xml_writer.writeStartElement("DOCUMENT");

        xml_writer.writeStartElement("DOCUMENT_NAME");
        xml_writer.writeCharacters(this.DocumentName);
        xml_writer.writeEndElement();

        xml_writer.writeStartElement("VERSION");
        xml_writer.writeCharacters(this.Version);
        xml_writer.writeEndElement();

        xml_writer.writeStartElement("LENGTH");
        xml_writer.writeCharacters(Integer.toString(this.Length));
        xml_writer.writeEndElement();

        if(write_filename) {
            xml_writer.writeStartElement("PATH_TO_FILE");
            xml_writer.writeCharacters(this.FileName);
            xml_writer.writeEndElement();
        }

        xml_writer.writeEndElement();
    }

}