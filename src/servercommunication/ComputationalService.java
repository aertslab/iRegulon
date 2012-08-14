package servercommunication;


import java.util.*;


import domainmodel.GeneIdentifier;
import domainmodel.Input;
import domainmodel.Motif;





public interface ComputationalService {
	public List<Motif> findPredictedRegulators(Input input);
	
	public List<Motif> queryDatabaseForRegulators(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);
	
	public List<Motif> queryDatabaseForTargetome(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);
	
	public List<Motif> queryDatabaseNetworkAnnotations(Collection<Collection<GeneIdentifier>> geneIDPairs, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR);	

}
