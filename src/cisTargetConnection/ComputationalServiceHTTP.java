package cisTargetConnection;

import httpProtocols.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import domainModel.GeneIdentifier;
import domainModel.Motif;

public class ComputationalServiceHTTP implements ComputationalService{

	@Override
	public List<Motif> findPredictedRegulators(String name,
			Collection<GeneIdentifier> geneIDs, float AUCThreshold,
			int rankThreshold, float NESThreshold, float minOrthologous, 
			float maxMotifSimilarityFDR) {
		// TODO Auto-generated method stub
		Service service = new HTTPService();
		ClassicalTask task = new ClassicalTask(service, name, geneIDs, AUCThreshold, rankThreshold, 
				NESThreshold, minOrthologous, maxMotifSimilarityFDR);
		
		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);

		jTaskConfig.displayCancelButton(true);

		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);

		// Execute Task in New Thread; pops open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
		
		Collection<Motif> motifColl = task.getMotifs();
		List<Motif> motifs = new ArrayList<Motif>();
		for (Motif mtf : motifColl){
			motifs.add(mtf);
		}
		if (task.getFinishedState().equals(State.ERROR) && ! task.getIsInterupted()){
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), task.getErrorMessage());
			motifs = Collections.EMPTY_LIST;
		}
		return motifs;
	}

	@Override
	public List<Motif> queryDatabaseForRegulators(GeneIdentifier geneID,
			float minNEScore, float minOrthologousIdentity,
			float minSimilarityFDR) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Motif> queryDatabaseForTargetome(GeneIdentifier geneID,
			float minNEScore, float minOrthologousIdentity,
			float minSimilarityFDR) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Motif> queryDatabaseNetworkAnnotations(
			Collection<Collection<GeneIdentifier>> geneIDPairs,
			float minNEScore, float minOrthologousIdentity,
			float minSimilarityFDR) {
		// TODO Auto-generated method stub
		return null;
	}

}
