package cisTargetConnection;

import java.util.*;

import domainModel.GeneIdentifier;
import domainModel.Motif;





public interface ComputationalService {
	public List<Motif> findPredictedRegulators(String name, Collection<GeneIdentifier> geneIDs, float AUCThreshold, int rankThreshold, float NESThreshold, float minOrthologous, 
			float maxMotifSimilarityFDR);
	
	public List<Motif> queryDatabaseForRegulators(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);
	
	public List<Motif> queryDatabaseForTargetome(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);
	
	public List<Motif> queryDatabaseNetworkAnnotations(Collection<Collection<GeneIdentifier>> geneIDPairs, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);	

}
