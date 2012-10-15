package domainmodel;

import infrastructure.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenePutativeRegulatoryRegion {
    private static Map<String, GenePutativeRegulatoryRegion> CODE2COLLECTION = new HashMap<String, GenePutativeRegulatoryRegion>();
    private static GenePutativeRegulatoryRegion UNKNOWN = new GenePutativeRegulatoryRegion("?", "?");

    private static final String TAGNAME = "delineation";
    private static final String ATTRIBUTENAME = "id";

    static {
        final Document document = Configuration.getDocument();
        final NodeList nList = document.getElementsByTagName(TAGNAME);
        for (int i = 0; i < nList.getLength(); i++) {
            final Node nNode = nList.item(i);
            final String description = nNode.getNodeValue();
            final String code = nNode.getAttributes().getNamedItem(ATTRIBUTENAME).getNodeValue();
            CODE2COLLECTION.put(code, new GenePutativeRegulatoryRegion(code, description));
        }
    }

    public static GenePutativeRegulatoryRegion forCode(final String code) {
        return CODE2COLLECTION.containsKey(code) ? CODE2COLLECTION.get(code) : UNKNOWN;
    }

    public static List<GenePutativeRegulatoryRegion> all() {
        return new ArrayList<GenePutativeRegulatoryRegion>(CODE2COLLECTION.values());
    }

    private final String code;
    private final String description;

    private GenePutativeRegulatoryRegion(String code, String description) {
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

        GenePutativeRegulatoryRegion that = (GenePutativeRegulatoryRegion) o;

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
