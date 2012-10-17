package infrastructure;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public final class Configuration {
    private static final String CONFIGURATION_XML_FILENAME = "configuration.xml";

    public static Document getDocument() {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(Configuration.class.getResourceAsStream(CONFIGURATION_XML_FILENAME));
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
