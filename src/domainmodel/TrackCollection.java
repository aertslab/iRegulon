package domainmodel;


import infrastructure.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public final class TrackCollection implements Comparable<TrackCollection> {
    private static Map<String, TrackCollection> CODE2COLLECTION = new LinkedHashMap<String, TrackCollection>();
    public static TrackCollection NONE = new TrackCollection("none", "No track collection");
    public static TrackCollection UNKNOWN = new TrackCollection("unknown", "Unknown track collection");

    public static TrackCollection defaultTrackCollection;

    private static final String GROUP_TAGNAME = "track-collections";
    private static final String TAGNAME = "track-collection";
    private static final String ATTRIBUTENAME = "id";
    private static final String DEFAULT_ATTRIBUTE_NAME = "default";

    static {
        final Document document = Configuration.getDocument();
        for (Element child : findElements(document.getElementsByTagName(GROUP_TAGNAME), TAGNAME)) {
            final String description = child.getTextContent().trim();
            final String code = child.getAttribute(ATTRIBUTENAME);
            CODE2COLLECTION.put(code, new TrackCollection(code, description));
            if (child.hasAttribute(DEFAULT_ATTRIBUTE_NAME) && child.getAttribute(DEFAULT_ATTRIBUTE_NAME).toLowerCase().equals("true")) {
                defaultTrackCollection = new TrackCollection(code, description);
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

    public static TrackCollection forCode(final String code) {
        return CODE2COLLECTION.containsKey(code) ? CODE2COLLECTION.get(code) : UNKNOWN;
    }

    public static List<TrackCollection> all() {
        return new ArrayList<TrackCollection>(CODE2COLLECTION.values());
    }

    private final String code;
    private final String description;

    private TrackCollection(String code, String description) {
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

        TrackCollection that = (TrackCollection) o;

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
    public int compareTo(TrackCollection o) {
        return description.compareTo(o.description);
    }
}
