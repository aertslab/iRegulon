package httpConnection;


import java.util.*;


import domainModel.GeneIdentifier;
import domainModel.Input;
import domainModel.Motif;





public interface ComputationalService {
	public List<Motif> findPredictedRegulators(Input input);
	
	public List<Motif> queryDatabaseForRegulators(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);
	
	public List<Motif> queryDatabaseForTargetome(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);
	
	public List<Motif> queryDatabaseNetworkAnnotations(Collection<Collection<GeneIdentifier>> geneIDPairs, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);	

}
