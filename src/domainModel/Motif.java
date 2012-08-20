package domainmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Motif extends AbstractMotif implements Comparable<Motif> {
    final private String enrichedMotifID;
    final private int rank;
	final private String description;
	final private int featureID;
	final private int jobID;
    private final float neScore;
    private final float aucValue;

	public Motif(String enrichedMotifID, List<CandidateTargetGene> candidateTargetGenes,
			List<TranscriptionFactor> transcriptionFactors, float neScore, int clusterCode
			, float aucValue, int rank, String description, int featureID, int jobID){
        super(clusterCode, candidateTargetGenes, transcriptionFactors);
        this.enrichedMotifID = enrichedMotifID;
        Collections.sort(new ArrayList<CandidateTargetGene>(this.candidateTargetGenes));
        Collections.sort(new ArrayList<TranscriptionFactor>(this.transcriptionFactors));
        this.rank = rank;
		this.description = description;
		this.featureID = featureID;
		this.jobID = jobID;
                this.neScore = neScore;
        this.aucValue = aucValue;
	}

    public int getRank(){
		return this.rank;
	}

    public String getID() {
        return this.enrichedMotifID;
    }

    public String getDescription(){
		return this.description;
	}

    public float getAUCValue() {
        return this.aucValue;
    }

    public float getNEScore(){
        return this.neScore;
    }

	public int getFeatureID(){
		return this.featureID;
	}

	public int getJobID(){
		return this.jobID;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Motif motif = (Motif) o;

        if (!enrichedMotifID.equals(motif.enrichedMotifID)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return enrichedMotifID.hashCode();
    }

    public int compareTo(final Motif other) {
        return this.getID().compareTo(other.getID());
    }
}
