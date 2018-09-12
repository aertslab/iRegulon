package domainmodel;

import infrastructure.IRegulonResourceBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Motif extends AbstractMotif implements Comparable<Motif> {
    private static final ResourceBundle RESOURCE_BUNDLE = IRegulonResourceBundle.getBundle();
    private static final String MC_V3_V6_LOGO_FOLDERNAME = RESOURCE_BUNDLE.getString("mc_v3_v6_logo_folder");
    private static final String MC_V7_AND_HIGHER_LOGO_FOLDERNAME = RESOURCE_BUNDLE.getString("mc_v7_and_higher_logo_folder");
    private static final String LOGO_EXTENSION = RESOURCE_BUNDLE.getString("logo_extension");

    final private String name;
    final private int rank;
    final private String description;
    final private int featureID;
    private final float neScore;
    private final float aucValue;

    public Motif(String name, List<CandidateTargetGene> candidateTargetGenes,
                 List<TranscriptionFactor> transcriptionFactors, float neScore, String clusterCode, int clusterNumber,
                 float aucValue, int rank, String description, int featureID, int jobID) {
        super(clusterCode, candidateTargetGenes, transcriptionFactors);
        this.name = name;
        Collections.sort(new ArrayList<CandidateTargetGene>(this.candidateTargetGenes));
        Collections.sort(new ArrayList<TranscriptionFactor>(this.transcriptionFactors));
        this.neScore = neScore;
        setClusterNumber(clusterNumber);
        this.aucValue = aucValue;
        this.rank = rank;
        this.description = description;
        this.featureID = featureID;
    }

    public int getRank() {
        return this.rank;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public float getAUCValue() {
        return this.aucValue;
    }

    public float getNEScore() {
        return this.neScore;
    }

    public int getDatabaseID() {
        return this.featureID;
    }

    public Motif getBestMotif() {
        return this;
    }

    public List<Motif> getMotifs() {
        return Collections.singletonList(this);
    }

    public String getMotifNameWithoutMotifCollection() {
        final int motifNameMotifCollectionCouldBe7OrHigherIndex = this.name.indexOf("__");
        final int motifNameMotifCollectionCouldBe3To6Index = this.name.indexOf("-");

        if (motifNameMotifCollectionCouldBe7OrHigherIndex != -1) {
            if (motifNameMotifCollectionCouldBe3To6Index != -1) {
                /*
                 * If the motif name contains both "__" and "-", check which one occurs first
                 * to determine to which motif collection it belongs to.
                 */
                if (motifNameMotifCollectionCouldBe7OrHigherIndex < motifNameMotifCollectionCouldBe3To6Index) {
                    return this.name.substring(motifNameMotifCollectionCouldBe7OrHigherIndex + 2);
                } else {
                    return this.name.substring(motifNameMotifCollectionCouldBe3To6Index + 1);
                }
            } else {
                return this.name.substring(motifNameMotifCollectionCouldBe7OrHigherIndex + 2);
            }
        } else {
            return this.name.substring(motifNameMotifCollectionCouldBe3To6Index + 1);
        }
    }

    static public String getLogoPath(String motifName) {
        final int motifNameMotifCollectionCouldBe7OrHigherIndex = motifName.indexOf("__");
        final int motifNameMotifCollectionCouldBe3To6Index = motifName.indexOf("-");

        if (motifNameMotifCollectionCouldBe7OrHigherIndex != -1) {
            if (motifNameMotifCollectionCouldBe3To6Index != -1) {
                /*
                 * If the motif name contains both "__" and "-", check which one occurs first
                 * to determine to which motif collection it belongs to.
                 */
                if (motifNameMotifCollectionCouldBe7OrHigherIndex < motifNameMotifCollectionCouldBe3To6Index) {
                    return MC_V7_AND_HIGHER_LOGO_FOLDERNAME + "/" + motifName + "." + LOGO_EXTENSION;
                } else {
                    return MC_V3_V6_LOGO_FOLDERNAME + "/" + motifName + "." + LOGO_EXTENSION;
                }
            } else {
                return MC_V7_AND_HIGHER_LOGO_FOLDERNAME + "/" + motifName + "." + LOGO_EXTENSION;
            }
        } else {
            return MC_V3_V6_LOGO_FOLDERNAME + "/" + motifName + "." + LOGO_EXTENSION;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Motif motif = (Motif) o;

        if (!name.equals(motif.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(final Motif other) {
        return this.getName().compareTo(other.getName());
    }
}
