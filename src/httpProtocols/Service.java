package httpProtocols;

import java.util.Collection;

import domainModel.GeneIdentifier;
import domainModel.Motif;

public interface Service {

	public int sentJob(String name, Collection<GeneIdentifier> geneIDs, float AUCThreshold, int rankThreshold, float NESThreshold, float minOrthologous, float maxMotifSimilarityFDR);
	
	public State getState(int jobID);
	
	public int getJobsBeforeThis(int jobID);
	
	public Collection<Motif> getMotifs(int jobID);
	
	public String getErrorMessage(int jobID);
		
	
}
