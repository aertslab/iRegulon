package domainmodel;


import java.util.*;

import infrastructure.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public final class MotifCollection implements Comparable<MotifCollection> {
    private static Map<String, MotifCollection> CODE2COLLECTION = new LinkedHashMap<String, MotifCollection>();
    public static MotifCollection NONE = new MotifCollection("none", "No motif collection");
    public static MotifCollection UNKNOWN = new MotifCollection("unknown", "Unknown motif collection");

    public static MotifCollection defaultMotifCollection;

    private static final String GROUP_TAGNAME = "motif-collections";
    private static final String TAGNAME = "motif-collection";
    private static final String ATTRIBUTENAME = "id";
    private static final String DEFAULT_ATTRIBUTE_NAME = "default";

    static {
        final Document document = Configuration.getDocument();
        for (Element child : findElements(document.getElementsByTagName(GROUP_TAGNAME), TAGNAME)) {
            final String description = child.getTextContent().trim();
            final String code = child.getAttribute(ATTRIBUTENAME);
            CODE2COLLECTION.put(code, new MotifCollection(code, description));
            if (child.hasAttribute(DEFAULT_ATTRIBUTE_NAME) && child.getAttribute(DEFAULT_ATTRIBUTE_NAME).toLowerCase().equals("true")) {
                defaultMotifCollection = new MotifCollection(code, description);
            }
        }
    }

    private static List<Element> findElements(final NodeList nodeList, final String tagName) {
        final List<Element> elements = new ArrayList<Element>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final NodeList nodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < nodes.getLength(); j++) {
                final Node node = nodes.item(j);
                if (node instanceof Element && ((Element) node).getTagName().equals(tagName)) {
                    elements.add((Element) node);
                }
            }
        }
        return elements;
    }

    public static MotifCollection forCode(final String code) {
        return CODE2COLLECTION.containsKey(code) ? CODE2COLLECTION.get(code) : UNKNOWN;
    }

    public static List<MotifCollection> all() {
        return new ArrayList<MotifCollection>(CODE2COLLECTION.values());
    }

    private final String code;
    private final String description;

    private MotifCollection(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotifCollection that = (MotifCollection) o;

        if (!code.equals(that.code)) return false;
        if (!description.equals(that.description)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int compareTo(MotifCollection o) {
        return description.compareTo(o.description);
    }
}
