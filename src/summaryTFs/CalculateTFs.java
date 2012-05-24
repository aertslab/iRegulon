package summaryTFs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domainModel.Motif;
import domainModel.Results;
import domainModel.TranscriptionFactor;

public class CalculateTFs {

	private Map<String, PredictedTF> tfs;
	
	public CalculateTFs(Results result){
		Collection<Motif> motifs = result.getMotifs();
		tfs = new HashMap<String, PredictedTF>();
		for (Motif motif : motifs){
			for (TranscriptionFactor tf : motif.getTranscriptionFactors()){
				if (tfs.containsKey(tf.getName())){
					tfs.get(tf.getName()).addMotif(motif);
				}else{
					PredictedTF predict = new PredictedTF(tf.getGeneID(), motifs.size());
					predict.addMotif(motif);
					tfs.put(tf.getName(), predict);
				}
			}
		}
	}
	
	public Map<String, PredictedTF> getPredictedTFs(){
		return this.tfs;
	}
	
	public Collection<PredictedTF> getCollectionPredictedTFs(){
		return this.tfs.values();
	}
	
}
