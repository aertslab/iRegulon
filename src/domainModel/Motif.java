package domainmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Motif implements Comparable<Motif> {
	final private String enrichedMotifID;
	final private int clusterCode;
	final private float neScore;
	final private List<CandidateTargetGene> candidateTargetGenes;
	final private List<TranscriptionFactor> transcriptionFactors;
	final private float aucValue;
	final private int rank;
	final private String description;
	final private int featureID;
	final private int jobID;

	public Motif(String enrichedMotifID, List<CandidateTargetGene> candidateTargetGenes,
			List<TranscriptionFactor> transcriptionFactors, float neScore, int clusterCode
			, float aucValue, int rank, String description, int featureID, int jobID){
		this.enrichedMotifID = enrichedMotifID;
		this.candidateTargetGenes = candidateTargetGenes;
		Collections.sort(new ArrayList<CandidateTargetGene>(this.candidateTargetGenes));
		this.transcriptionFactors = transcriptionFactors;
		Collections.sort(new ArrayList<TranscriptionFactor>(this.transcriptionFactors));
		this.neScore = neScore;
		this.clusterCode = clusterCode;
		this.aucValue = aucValue;
		this.rank = rank;
		this.description = description;
		this.featureID = featureID;
		this.jobID = jobID;
	}
	
	public String getID() {
		return this.enrichedMotifID;
	}
	
	public int getClusterCode(){
		return this.clusterCode;
	}
	
	public float getNEScore(){
		return this.neScore;
	}
	
	public List<CandidateTargetGene> getCandidateTargetGenes(){
		return this.candidateTargetGenes;
	}
	
	public List<TranscriptionFactor> getTranscriptionFactors(){
		return this.transcriptionFactors;
	}

    public TranscriptionFactor getBestTranscriptionFactor() {
        if (getTranscriptionFactors().isEmpty()) return null;
        else return getTranscriptionFactors().get(0);
    }
	
	public float getAUCValue(){
		return this.aucValue;
	}
	
	public int getRank(){
		return this.rank;
	}

    public String getDescription(){
		return this.description;
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
