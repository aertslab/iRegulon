package transcriptionfactorview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import domainModel.GeneIdentifier;
import domainModel.Motif;
import domainModel.Results;
import domainModel.TranscriptionFactor;

public class EnrichedTranscriptionFactorCollection {
	private final Map<GeneIdentifier, EnrichedTranscriptionFactor> enrichedTranscriptionFactors;
	
	public EnrichedTranscriptionFactorCollection(final Results result){
		final Collection<Motif> enrichedMotifs = result.getMotifs();
		enrichedTranscriptionFactors = new HashMap<GeneIdentifier, EnrichedTranscriptionFactor>();
		for (Motif motif : enrichedMotifs) {
			for (TranscriptionFactor tf : motif.getTranscriptionFactors()){
				if (enrichedTranscriptionFactors.containsKey(tf.getGeneID())) {
					enrichedTranscriptionFactors.get(tf.getGeneID()).addMotif(motif);
				} else {
					final EnrichedTranscriptionFactor predict = new EnrichedTranscriptionFactor(tf.getGeneID(), enrichedMotifs.size());
					predict.addMotif(motif);
					enrichedTranscriptionFactors.put(tf.getGeneID(), predict);
				}
			}
		}
	}
	
	public Map<GeneIdentifier, EnrichedTranscriptionFactor> getPredictedTFs(){
		return this.enrichedTranscriptionFactors;
	}
	
	public Collection<EnrichedTranscriptionFactor> getCollectionPredictedTFs(){
		return this.enrichedTranscriptionFactors.values();
	}
}
