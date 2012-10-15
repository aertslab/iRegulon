package domainmodel;


import java.util.*;

import infrastructure.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;


public final class MotifCollection {
    private static Map<String, MotifCollection> CODE2COLLECTION = new HashMap<String, MotifCollection>();
    private static MotifCollection UNKNOWN = new MotifCollection("?", "?");

    private static final String TAGNAME = "motif-collection";
    private static final String ATTRIBUTENAME = "id";

    static {
        final Document document = Configuration.getDocument();
        final NodeList nList = document.getElementsByTagName(TAGNAME);
        for (int i = 0; i < nList.getLength(); i++) {
            final Node nNode = nList.item(i);
            final String description = nNode.getNodeValue();
            final String code = nNode.getAttributes().getNamedItem(ATTRIBUTENAME).getNodeValue();
            CODE2COLLECTION.put(code, new MotifCollection(code, description));
        }
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
}
