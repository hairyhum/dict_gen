package dict_gen;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.rules.TemporaryFolder;

class document_data_read_test {

    @TempDir
    static Path sharedTempDir;

    @Test
    void read_ok_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        String ok_data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<DOCUMENT>\n" +
                "    <DOCUMENT_NAME>name</DOCUMENT_NAME>\n" +
                "    <VERSION>version</VERSION>\n" +
                "    <DATA>data</DATA>\n" +
                "</DOCUMENT>";
        File tempFile = sharedTempDir.resolve("file.xml").toFile();
        try (PrintWriter out = new PrintWriter(tempFile)) {
            out.println(ok_data);
        }

        new DocumentData(tempFile, XMLInputFactory.newFactory());
    }

    @Test
    void data_tag_missing_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        String no_data_data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<DOCUMENT>\n" +
                "    <DOCUMENT_NAME>name</DOCUMENT_NAME>\n" +
                "    <VERSION>version</VERSION>\n" +
                "</DOCUMENT>";
        File no_data_file = sharedTempDir.resolve("no_data_file.xml").toFile();
        try (PrintWriter out = new PrintWriter(no_data_file)) {
            out.println(no_data_data);
        }

        Assertions.assertThrows(DocumentParsingException.class, () -> {
            new DocumentData(no_data_file, XMLInputFactory.newFactory());
        });
    }

    @Test
    void name_tag_missing_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {

        String no_name_data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<DOCUMENT>\n" +
                "    <VERSION>version</VERSION>\n" +
                "    <DATA>blah</DATA>" +
                "</DOCUMENT>";
        File no_name_file = sharedTempDir.resolve("no_name_file.xml").toFile();
        try (PrintWriter out = new PrintWriter(no_name_file)) {
            out.println(no_name_data);
        }

        Assertions.assertThrows(DocumentParsingException.class, () -> {
            new DocumentData(no_name_file, XMLInputFactory.newFactory());
        });
    }

    @Test
    void version_tag_missing_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        String no_version_data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<DOCUMENT>\n" +
                "    <DOCUMENT_NAME>name</DOCUMENT_NAME>\n" +
                "    <DATA>blah</DATA>" +
                "</DOCUMENT>";
        File no_version_file = sharedTempDir.resolve("no_version_file.xml").toFile();
        try (PrintWriter out = new PrintWriter(no_version_file)) {
            out.println(no_version_data);
        }

        Assertions.assertThrows(DocumentParsingException.class, () -> {
            new DocumentData(no_version_file, XMLInputFactory.newFactory());
        });
    }

    @Test
    void wrong_root_tag_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        String wrong_root_data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<NOT_DOCUMENT>\n" +
                "    <DOCUMENT_NAME>name</DOCUMENT_NAME>\n" +
                "    <VERSION>version</VERSION>\n" +
                "    <DATA>blah</DATA>" +
                "</NOT_DOCUMENT>";
        File wrong_root_file = sharedTempDir.resolve("wrong_root_file.xml").toFile();
        try (PrintWriter out = new PrintWriter(wrong_root_file)) {
            out.println(wrong_root_data);
        }

        Assertions.assertThrows(DocumentParsingException.class, () -> {
            new DocumentData(wrong_root_file, XMLInputFactory.newFactory());
        });
    }

    @Test
    void inner_tag_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        String inner_tag_data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "<DOCUMENT>\n" +
                "    <DOCUMENT_NAME><INNER></INNER></DOCUMENT_NAME>\n" +
                "    <VERSION>version</VERSION>\n" +
                "    <DATA>blah</DATA>" +
                "</DOCUMENT>";
        File wrong_root_file = sharedTempDir.resolve("wrong_root_file.xml").toFile();
        try (PrintWriter out = new PrintWriter(wrong_root_file)) {
            out.println(inner_tag_data);
        }

        Assertions.assertThrows(DocumentParsingException.class, () -> {
            new DocumentData(wrong_root_file, XMLInputFactory.newFactory());
        });

    }

    @Test
    void empty_doc_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        String empty_data = "";
        File empty_file = sharedTempDir.resolve("empty_file.xml").toFile();
        try (PrintWriter out = new PrintWriter(empty_file)) {
            out.println(empty_data);
        }

        Assertions.assertThrows(XMLStreamException.class, () -> {
            new DocumentData(empty_file, XMLInputFactory.newFactory());
        });

    }

    @Test
    void no_file_test() throws IOException, XMLStreamException, DocumentParsingException, FactoryConfigurationError {
        File no_file = sharedTempDir.resolve("no_file.xml").toFile();
        Assertions.assertThrows(IOException.class, () -> {
            new DocumentData(no_file, XMLInputFactory.newFactory());
        });

    }

}
