package domainmodel;

import infrastructure.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import view.IRegulonResourceBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class RankingsDatabase extends IRegulonResourceBundle {
    private static final String DATABASES_TAG_NAME = "databases";
    private static final String DATABASE_TAG_NAME = "database";
    private static final String TYPE_TAG_NAME = "type";
    private static final String NAME_TAG_NAME = "name";
    private static final String SPECIES_TAG_NAME = "species";
    private static final String NOMENCLATURE_TAG_NAME = "nomenclature";
    private static final String MOTIF_COLLECTION_TAG_NAME = "motif-collection";
    private static final String DELINEATION_TAG_NAME = "delineation";
    private static final String NUMBER_OF_SPECIES_TAG_NAME = "number-of-species";
    private static final String DEFAULT_AUC_THRESHOLD_TAG_NAME = "default-auc-threshold";
    private static final String DEFAULT_RANK_THRESHOLD_TAG_NAME = "default-rank-threshold";
    private static final String MAPPINGS_TAG_NAME = "mappings";

    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String REF_ID_ATTRIBUTE_NAME = "refid";

    private static final float DEFAULT_AUC_THRESHOLD = Float.parseFloat(BUNDLE.getString("standard_ROC"));
    private static final int DEFAULT_RANK_THRESHOLD = Integer.parseInt(BUNDLE.getString("standard_visualisation"));

    public static List<RankingsDatabase> loadFromConfiguration() {
        final Document document = Configuration.getDocument();
        final List<RankingsDatabase> result = new ArrayList<RankingsDatabase>();
        for (Element child : findElements(document.getElementsByTagName(DATABASES_TAG_NAME), DATABASE_TAG_NAME)) {
            result.add(createDatabase(child));
        }
        return result;
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

    private static RankingsDatabase createDatabase(Node nNode) {
        final NodeList childNodes = nNode.getChildNodes();

        final String code = nNode.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue();
        final String name = readValue(childNodes, NAME_TAG_NAME);

        final Type type = Type.forCode(readValue(childNodes, TYPE_TAG_NAME));

        final String species = readValue(childNodes, SPECIES_TAG_NAME);
        final int nomenclatureCode = Integer.parseInt(readAttribute(childNodes, NOMENCLATURE_TAG_NAME, REF_ID_ATTRIBUTE_NAME));

        final MotifCollection collection = MotifCollection.forCode(readAttribute(childNodes, MOTIF_COLLECTION_TAG_NAME, REF_ID_ATTRIBUTE_NAME));

        final int speciesCount = Integer.parseInt(readValue(childNodes, NUMBER_OF_SPECIES_TAG_NAME));

        String value = readValue(childNodes, DEFAULT_AUC_THRESHOLD_TAG_NAME);
        final float aucThreshold = value != null
                ? Float.parseFloat(value)
                : DEFAULT_AUC_THRESHOLD;
        value = readValue(childNodes, DEFAULT_RANK_THRESHOLD_TAG_NAME);
        final int rankThreshold = value != null
                ? Integer.parseInt(value)
                : DEFAULT_RANK_THRESHOLD;

        if (Type.GENE.equals(type)) {
            final GenePutativeRegulatoryRegion regulatoryRegion = GenePutativeRegulatoryRegion.forCode(readAttribute(childNodes, DELINEATION_TAG_NAME, REF_ID_ATTRIBUTE_NAME));
            return new RankingsDatabase(code, name, type, nomenclatureCode, collection, speciesCount, regulatoryRegion, Collections.<Delineation>emptyList(), aucThreshold, rankThreshold);
        } else if (Type.REGION.equals(type)) {
            final List<Delineation> delineations = new ArrayList<Delineation>();
            final NodeList mappings = findElement(childNodes, MAPPINGS_TAG_NAME);
            for (int i = 0; i < mappings.getLength(); i++) {
                final Node mapping = mappings.item(i);
                if (mapping instanceof Element) {
                    final Element child = (Element) mapping;
                    delineations.add(new Delineation(child.getAttribute(ID_ATTRIBUTE_NAME), child.getTextContent().trim()));
                }
            }
            return new RankingsDatabase(code, name, type, nomenclatureCode, collection, speciesCount, GenePutativeRegulatoryRegion.UNKNOWN, delineations, aucThreshold, rankThreshold);
        } else {
          throw new IllegalStateException();
        }
    }

    private static NodeList findElement(final NodeList nodeList, final String tagName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node instanceof Element && ((Element) node).getTagName().equals(tagName)) {
                final Element child = (Element) node;
                return child.getChildNodes();
            }
        }
        return null;
    }

    private static String readValue(final NodeList nodeList, final String tagName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node instanceof Element && ((Element) node).getTagName().equals(tagName)) {
                final Element child = (Element) node;
                final String content = child.getTextContent();
                return content == null ? content : content.trim();
            }
        }
        return null;
    }

    private static String readAttribute(final NodeList nodeList, final String tagName, final String attributeName) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node instanceof Element && ((Element) node).getTagName().equals(tagName)) {
                final Element child = (Element) node;
                return child.getAttribute(attributeName);
            }
        }
        return null;
    }

	private final String code;
	private final String name;
    private final Type type;
    private final int speciesNomenclature;
    private final MotifCollection motifCollection;
    private final int speciesCount;
    private final GenePutativeRegulatoryRegion putativeRegulatoryRegion;
    private final List<Delineation> gene2regionDelineations;
	private final float AUCvalue;
	private final int visualisationValue;

    public RankingsDatabase(String code, String name, Type type, int speciesNomenclature, MotifCollection motifCollection, int speciesCount, GenePutativeRegulatoryRegion putativeRegulatoryRegion, List<Delineation> gene2regionDelineations, float AUCvalue, int visualisationValue) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.speciesNomenclature = speciesNomenclature;
        this.motifCollection = motifCollection;
        this.speciesCount = speciesCount;
        this.putativeRegulatoryRegion = putativeRegulatoryRegion;
        this.gene2regionDelineations = gene2regionDelineations;
        this.AUCvalue = AUCvalue;
        this.visualisationValue = visualisationValue;
    }

    public RankingsDatabase(String code, String name, float AUCvalue, int visualisationValue) {
		this(code, name, Type.GENE, -1, MotifCollection.UNKNOWN, 0, GenePutativeRegulatoryRegion.UNKNOWN, Collections.<Delineation>emptyList(), AUCvalue, visualisationValue);
	}
	
	public String getCode(){
		return this.code;
	}
	
	public String getName(){
		return this.name;
	}

    public Type getType() {
        return type;
    }

    public int getSpeciesNomenclature() {
        return speciesNomenclature;
    }

    public MotifCollection getMotifCollection() {
        return motifCollection;
    }

    public int getSpeciesCount() {
        return speciesCount;
    }

    public GenePutativeRegulatoryRegion getPutativeRegulatoryRegion() {
        return putativeRegulatoryRegion;
    }

    public List<Delineation> getGene2regionDelineations() {
        return gene2regionDelineations;
    }

    public float getAUCvalue(){
		return this.AUCvalue;
	}
	
	public int getVisualisationValue(){
		return this.visualisationValue;
	}
	
	public String toString(){
		return this.name;
	}
	
	public static enum Type {
        GENE("genes", "gene-based"), REGION("regions", "region-based");

        private final String code;
        private final String description;

        private Type(final String code, final String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Type forCode(final String code) {
            if (REGION.getCode().equals(code)) return REGION;
            else if (GENE.getCode().equals(code)) return GENE;
            else throw new IllegalStateException();
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
