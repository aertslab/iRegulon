package domainModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Motif {

	final private String enrichedMotifID;
	final private int clusterCode;
	final private float neScore;
	final private Collection<CandidateTargetGene> candidateTargetGenes;
	final private Collection<TranscriptionFactor> transcriptionFactors;
	final private float aucValue;
	final private int rank;
	final private String description;
	final private int featureID;
	final private int jobID;
	
	/**
	 * 
	 * @param enrichedMotifID
	 * @param candidateTargetGenes
	 * @param transcriptionFactors
	 * @param NEScore
	 * @param clusterCode
	 * @param AUCValue
	 */
	public Motif(String enrichedMotifID, Collection<CandidateTargetGene> candidateTargetGenes,
			Collection<TranscriptionFactor> transcriptionFactors, float neScore, int clusterCode
			, float aucValue, int rank, String description, int featureID, int jobID){
		this.enrichedMotifID = enrichedMotifID;
		this.candidateTargetGenes = candidateTargetGenes;
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
	
	/**
	 * @return String the enriched motif
	 */
	public String getEnrichedMotifID(){
		return this.enrichedMotifID;
	}
	
	public int getClusterCode(){
		return this.clusterCode;
	}
	
	public float getNeScore(){
		return this.neScore;
	}
	
	public Collection<CandidateTargetGene> getCandidateTargetGenes(){
		return this.candidateTargetGenes;
	}
	
	public Collection<TranscriptionFactor> getTranscriptionFactors(){
		return this.transcriptionFactors;
	}
	
	public float getAucValue(){
		return this.aucValue;
	}
	
	public int getRank(){
		return this.rank;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public TranscriptionFactor getMostTrustedTF(){
		TranscriptionFactor mttf = null;
		for (TranscriptionFactor tf : this.getTranscriptionFactors()){
			int trustedTF = mttf.compareTo(tf);
			if (trustedTF == -1){
				mttf = tf;
			}
		}
		return mttf;
	}
	
	public int getFeatureID(){
		return this.featureID;
	}
	
	public int getJobID(){
		return this.jobID;
	}
	
	
}
