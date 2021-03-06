package domainmodel;

import view.parametersform.IRegulonType;

import java.util.Collection;


public class PredictRegulatorsParameters {


    /* All input variables */
    private final Collection<GeneIdentifier> genes;
    private final float eScore;
    private final int thresholdForVisualisation;
    private final float rocThresholdAUC;
    private final SpeciesNomenclature speciesNomenclature;
    private final IRegulonType iRegulonType;
    private final String name;
    private final String motifCollection;
    private final String trackCollection;
    private final float minOrthologous;
    private final float maxMotifSimilarityFDR;

    /* Database */
    private final boolean isRegionBased;
    private final RankingsDatabase motifRankingsDatabase;
    private final RankingsDatabase trackRankingsDatabase;
    private final float overlap;
    private final Delineation delineation;
    private final int upstream;
    private final int downstream;

    private final String attributeName;


    public PredictRegulatorsParameters(Collection<GeneIdentifier> genes, float escore, float ROCthresholdAUC,
                                       int visualisationThreshold, SpeciesNomenclature speciesNomenclature,
                                       IRegulonType iRegulonType, String runName, String motifCollection, String trackCollection,
                                       float minOrthologous, float maxMotifSimilarityFDR, boolean isRegionBased,
                                       RankingsDatabase motifRankingsDatabase, RankingsDatabase trackRankingsDatabase,
                                       float overlap, Delineation delineation, int upstream, int downstream, String attributeName) {
        this.genes = genes;
        this.eScore = escore;
        this.thresholdForVisualisation = visualisationThreshold;
        this.rocThresholdAUC = ROCthresholdAUC;
        this.speciesNomenclature = speciesNomenclature;
        this.iRegulonType = iRegulonType;
        this.name = runName;
        this.motifCollection = motifCollection;
        this.trackCollection = trackCollection;
        this.minOrthologous = minOrthologous;
        this.maxMotifSimilarityFDR = maxMotifSimilarityFDR;
        this.isRegionBased = isRegionBased;
        this.motifRankingsDatabase = motifRankingsDatabase;
        this.trackRankingsDatabase = trackRankingsDatabase;
        this.overlap = overlap;
        this.delineation = delineation;
        this.upstream = upstream;
        this.downstream = downstream;
        this.attributeName = attributeName;
    }

    /**
     * @return an array of all the nodes selected as input
     */
    public Collection<GeneIdentifier> getGenes() {
        return this.genes;
    }

    /**
     * @return the Enrichment score threshold
     */
    public float getEScore() {
        return this.eScore;
    }

    /**
     * @return the x-axis cut-off for visualisation
     */
    public int getThresholdForVisualisation() {
        return this.thresholdForVisualisation;
    }

    /**
     * @return the region rank cut-off (ROC), where to calculate the Area Under the Curve (AUC)
     */
    public float getROCthresholdAUC() {
        return this.rocThresholdAUC;
    }

    public SpeciesNomenclature getSpeciesNomenclature() {
        return this.speciesNomenclature;
    }

    /**
     * @return the type of the iRegulon action
     */
    public IRegulonType getIRegulonType() {
        return this.iRegulonType;
    }

    /**
     * @return the name of the run
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the name of the motif collection
     */
    public String getMotifCollection() {
        return this.motifCollection;
    }

    /**
     * @return the name of the track collection
     */
    public String getTrackCollection() {
        return this.trackCollection;
    }

    /**
     * @return the minimal orthology
     */
    public float getMinOrthologous() {
        return this.minOrthologous;
    }

    /**
     * @return the maximal motif Similarity FDR
     */
    public float getMaxMotifSimilarityFDR() {
        return this.maxMotifSimilarityFDR;
    }

    /**
     * @return true if region based
     */
    public boolean isRegionBased() {
        return this.isRegionBased;
    }

    /**
     * @return true if gene based
     */
    public boolean isGeneBased() {
        return !this.isRegionBased;
    }

    /**
     * @return the motif rankings database
     */
    public RankingsDatabase getMotifRankingsDatabase() {
        return this.motifRankingsDatabase;
    }

    /**
     * @return the track rankings database
     */
    public RankingsDatabase getTrackRankingsDatabase() {
        return this.trackRankingsDatabase;
    }

    /**
     * @return the percentage of overlap (only regionbased)
     */
    public float getOverlap() {
        return this.overlap;
    }

    /**
     * @return the name of the delineation (only regionbased)
     *         null when no delineation
     */
    public Delineation getDelineation() {
        return this.delineation;
    }

    /**
     * @return true if delineation is used
     */
    public boolean isDelineationBased() {
        return !(this.delineation == null);
    }

    /**
     * @return the bp upstream (only regionbased)
     *         -1 when upstream isn't used
     */
    public int getUpstream() {
        return this.upstream;
    }

    /**
     * @return the bp downstream (only regionbased)
     *         -1 when downstream isn't used
     */
    public int getDownstream() {
        return this.downstream;
    }


    /**
     * @return true if the parameters are valid
     */
    public boolean parametersAreValid() {
        boolean parametersAreOkay = true;
        if (this.getMotifCollection().equals(MotifCollection.NONE.getDescription()) && this.getTrackCollection().equals(TrackCollection.NONE.getDescription())) {
            parametersAreOkay = false;
        }
        if (0 > this.getMaxMotifSimilarityFDR() || this.getMaxMotifSimilarityFDR() > 1) {
            parametersAreOkay = false;
        }
        if (0 > this.getMinOrthologous() || this.getMaxMotifSimilarityFDR() > 1) {
            parametersAreOkay = false;
        }
        if (1 > this.getThresholdForVisualisation()) {
            parametersAreOkay = false;
        }
        if (0 > this.getROCthresholdAUC() || this.getROCthresholdAUC() > 1) {
            parametersAreOkay = false;
        }
        if (1.5 > this.getEScore()) {
            parametersAreOkay = false;
        }
        if (this.getName().isEmpty() ||
                this.getName().toLowerCase().equalsIgnoreCase("iRegulon name")) {
            parametersAreOkay = false;
        }
        return parametersAreOkay;
    }

    /**
     * @return the message where the error occurred
     */
    public String getErrorMessage() {
        boolean parametersAreOkay = true;
        String message = "<html> Error: You have filled in a wrong parameter value: <br /> <br />";
        if (this.getMotifCollection().equals(MotifCollection.NONE.getDescription()) && this.getTrackCollection().equals(TrackCollection.NONE.getDescription())) {
            parametersAreOkay = false;
            message = message + "Choose motif and/or track collection. <br /> <br />";
        }
        if (0 > this.getMaxMotifSimilarityFDR() || this.getMaxMotifSimilarityFDR() > 1) {
            parametersAreOkay = false;
            message = message + "Max motif Similarity FDR must be between 0 and 1. <br /> <br />";
        }
        if (0 > this.getMinOrthologous() || this.getMaxMotifSimilarityFDR() > 1) {
            parametersAreOkay = false;
            message = message + "Min Orthologous must be between 0 and 1. <br /> <br />";
        }
        if (1 > this.getThresholdForVisualisation()) {
            parametersAreOkay = false;
            message = message + "The threshold for visualisation must be greater then 1. <br /> <br />";
        }
        if (0 > this.getROCthresholdAUC() || this.getROCthresholdAUC() > 1) {
            parametersAreOkay = false;
            message = message + "The AUC threshold must be between 0 and 1. <br /> <br />";
        }
        if (1.5 > this.getEScore()) {
            parametersAreOkay = false;
            message = message + "The E score must be greater then 1.5, else the prediction" +
                    "of your motifs and transcription factors will take a lot of time. <br /> <br />";
        }
        if (this.getName().isEmpty() ||
                this.getName().toLowerCase().equalsIgnoreCase("iRegulon name")) {
            parametersAreOkay = false;
            message = message + "You must choose a name for your research. <br /> <br />";
        }
        if (parametersAreOkay) {
            return "No error message.";
        }
        message = message + "</html>";
        return message;
    }

    public String getAttributeName() {
        return this.attributeName;
    }


}
