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

    private static File getFile() {
		final java.net.URL url = Configuration.class.getResource(CONFIGURATION_XML_FILENAME);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static Document getDocument() {
        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(getFile());
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
