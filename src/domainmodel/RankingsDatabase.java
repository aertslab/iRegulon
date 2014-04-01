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

public class RankingsDatabase extends IRegulonResourceBundle {
    private static final String DATABASES_TAG_NAME = "databases";
    private static final String DATABASE_TAG_NAME = "database";
    private static final String TYPE_TAG_NAME = "type";
    private static final String NAME_TAG_NAME = "name";
    private static final String COLLECTION_TYPE_TAG_NAME = "collection";
    private static final String SPECIES_TAG_NAME = "species";
    private static final String NOMENCLATURE_TAG_NAME = "nomenclature";
    private static final String DELINEATION_TAG_NAME = "delineation";
    private static final String NUMBER_OF_SPECIES_TAG_NAME = "number-of-species";
    private static final String DEFAULT_NES_THRESHOLD_TAG_NAME = "default-nes-threshold";
    private static final String DEFAULT_AUC_THRESHOLD_TAG_NAME = "default-auc-threshold";
    private static final String DEFAULT_RANK_THRESHOLD_TAG_NAME = "default-rank-threshold";
    private static final String MAPPINGS_TAG_NAME = "mappings";

    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String REF_ID_ATTRIBUTE_NAME = "refid";
    private static final String DEFAULT_ATTRIBUTE_NAME = "default";
    private static final String TYPE_ATTRIBUTE_NAME = "type";

    private static final float DEFAULT_NES_THRESHOLD = Float.parseFloat(BUNDLE.getString("standard_escore"));
    private static final float DEFAULT_AUC_THRESHOLD = Float.parseFloat(BUNDLE.getString("standard_ROC"));
    private static final int DEFAULT_RANK_THRESHOLD = Integer.parseInt(BUNDLE.getString("standard_visualisation"));

    public static List<RankingsDatabase> loadFromConfiguration() {
        final Document document = Configuration.getDocument();
        final List<RankingsDatabase> motifRankingsDatabases = new ArrayList<RankingsDatabase>();
        for (Element child : findElements(document.getElementsByTagName(DATABASES_TAG_NAME), DATABASE_TAG_NAME)) {
            motifRankingsDatabases.add(createMotifDatabase(child));
        }
        return motifRankingsDatabases;
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

    private static RankingsDatabase createMotifDatabase(Node nNode) {
        final NodeList childNodes = nNode.getChildNodes();

        final String code = nNode.getAttributes().getNamedItem(ID_ATTRIBUTE_NAME).getNodeValue();
        final String name = readValue(childNodes, NAME_TAG_NAME);

        final CollectionType collectionType = CollectionType.forCode(readAttribute(childNodes, COLLECTION_TYPE_TAG_NAME, TYPE_ATTRIBUTE_NAME));
        final Type type = Type.forCode(readValue(childNodes, TYPE_TAG_NAME));

        final String species = readValue(childNodes, SPECIES_TAG_NAME);
        final int nomenclatureCode = Integer.parseInt(readAttribute(childNodes, NOMENCLATURE_TAG_NAME, REF_ID_ATTRIBUTE_NAME));

        final TrackCollection trackCollection;
        final MotifCollection motifCollection;

        if (CollectionType.MOTIF.equals(collectionType)) {
            motifCollection = MotifCollection.forCode(readAttribute(childNodes, COLLECTION_TYPE_TAG_NAME, REF_ID_ATTRIBUTE_NAME));
            trackCollection = TrackCollection.NONE;
        } else if (CollectionType.TRACK.equals(collectionType)) {
            motifCollection = MotifCollection.NONE;
            trackCollection = TrackCollection.forCode(readAttribute(childNodes, COLLECTION_TYPE_TAG_NAME, REF_ID_ATTRIBUTE_NAME));
        } else {
            throw new IllegalStateException();
        }

        final int speciesCount = Integer.parseInt(readValue(childNodes, NUMBER_OF_SPECIES_TAG_NAME));

        String value = readValue(childNodes, DEFAULT_NES_THRESHOLD_TAG_NAME);
        final float nesThreshold = value != null
                ? Float.parseFloat(value)
                : DEFAULT_NES_THRESHOLD;

        value = readValue(childNodes, DEFAULT_AUC_THRESHOLD_TAG_NAME);
        final float aucThreshold = value != null
                ? Float.parseFloat(value)
                : DEFAULT_AUC_THRESHOLD;

        value = readValue(childNodes, DEFAULT_RANK_THRESHOLD_TAG_NAME);
        final int rankThreshold = value != null
                ? Integer.parseInt(value)
                : DEFAULT_RANK_THRESHOLD;

        Delineation delineationDefault = new Delineation("", "");

        if (Type.GENE.equals(type)) {
            final GenePutativeRegulatoryRegion regulatoryRegion = GenePutativeRegulatoryRegion.forCode(readAttribute(childNodes, DELINEATION_TAG_NAME, REF_ID_ATTRIBUTE_NAME));
            return new RankingsDatabase(code, name, type, nomenclatureCode, motifCollection, trackCollection, speciesCount, regulatoryRegion, Collections.<Delineation>emptyList(), delineationDefault, nesThreshold, aucThreshold, rankThreshold);
        } else if (Type.REGION.equals(type)) {
            final List<Delineation> delineations = new ArrayList<Delineation>();
            final NodeList mappings = findElement(childNodes, MAPPINGS_TAG_NAME);
            for (int i = 0; i < mappings.getLength(); i++) {
                final Node mapping = mappings.item(i);
                if (mapping instanceof Element) {
                    final Element child = (Element) mapping;
                    final Delineation delineationCurrent = new Delineation(child.getAttribute(ID_ATTRIBUTE_NAME), child.getTextContent().trim());
                    delineations.add(delineationCurrent);
                    if (child.hasAttribute(DEFAULT_ATTRIBUTE_NAME) && child.getAttribute(DEFAULT_ATTRIBUTE_NAME).toLowerCase().equals("true")) {
                        delineationDefault = delineationCurrent;
                    }
                }
            }
            return new RankingsDatabase(code, name, type, nomenclatureCode, motifCollection, trackCollection, speciesCount, GenePutativeRegulatoryRegion.NONE, delineations, delineationDefault, nesThreshold, aucThreshold, rankThreshold);
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
    private final TrackCollection trackCollection;
    private final int speciesCount;
    private final GenePutativeRegulatoryRegion putativeRegulatoryRegion;
    private final List<Delineation> gene2regionDelineations;
    private final Delineation delineationDefault;
    private final float NESvalue;
    private final float AUCvalue;
    private final int visualisationValue;

    public RankingsDatabase(String code, String name, Type type, int speciesNomenclature, MotifCollection motifCollection, TrackCollection trackCollection, int speciesCount, GenePutativeRegulatoryRegion putativeRegulatoryRegion, List<Delineation> gene2regionDelineations, Delineation delineationDefault, float NESvalue, float AUCvalue, int visualisationValue) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.speciesNomenclature = speciesNomenclature;
        this.motifCollection = motifCollection;
        this.trackCollection = trackCollection;
        this.speciesCount = speciesCount;
        this.putativeRegulatoryRegion = putativeRegulatoryRegion;
        this.gene2regionDelineations = gene2regionDelineations;
        this.delineationDefault = delineationDefault;
        this.NESvalue = NESvalue;
        this.AUCvalue = AUCvalue;
        this.visualisationValue = visualisationValue;
    }

    public RankingsDatabase(String code, String name, Delineation delineationDefault, float NESvalue, float AUCvalue, int visualisationValue) {
        this(code, name, Type.GENE, -1, MotifCollection.NONE, TrackCollection.NONE, 0, GenePutativeRegulatoryRegion.NONE, Collections.<Delineation>emptyList(), delineationDefault, NESvalue, AUCvalue, visualisationValue);
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
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

    public boolean hasMotifCollection() {
        return !motifCollection.equals(MotifCollection.NONE);
    }

    public TrackCollection getTrackCollection() {
        return trackCollection;
    }

    public boolean hasTrackCollection() {
        return !trackCollection.equals(TrackCollection.NONE);
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

    public Delineation getDelineationDefault() {
        return this.delineationDefault;
    }

    public float getNESvalue() {
        return this.NESvalue;
    }

    public float getAUCvalue() {
        return this.AUCvalue;
    }

    public int getVisualisationValue() {
        return this.visualisationValue;
    }

    public String toString() {
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

    public static enum CollectionType {
        MOTIF("motif", "motif collection"), TRACK("track", "track collection");

        private final String code;
        private final String description;

        private CollectionType(final String code, final String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static CollectionType forCode(final String code) {
            if (MOTIF.getCode().equals(code)) return MOTIF;
            else if (TRACK.getCode().equals(code)) return TRACK;
            else throw new IllegalStateException();
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
