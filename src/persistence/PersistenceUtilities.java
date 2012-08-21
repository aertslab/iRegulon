package persistence;

import view.parametersform.IRegulonType;

import java.util.Collection;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;

import domainmodel.*;


public class PersistenceUtilities {
    public static final String NATIVE_FILE_EXTENSION = ".irf";
    public static final String TSV_FILE_EXTENSION = ".txt";

	private PersistenceUtilities() {
	}
	
	public static String convertResultsToXML(Results result) {
		final XStream xstream = new XStream();
		xstream.alias("motif", Motif.class);
		xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		xstream.alias("geneIdentifier", GeneIdentifier.class);
		xstream.alias("transcriptionFactor", TranscriptionFactor.class);
		xstream.alias("inputParameters", InputParameters.class);
		xstream.alias("iRegulonType", IRegulonType.class);

		xstream.setMode(XStream.NO_REFERENCES);
		xstream.registerConverter(new SpeciesNomenclatureConverter());
		
		return xstream.toXML(result);
	}
	
	public static Results loadResultsFromXML(String xml) throws LoadException {
		final XStream xstream = new XStream();
		xstream.alias("motif", Motif.class);
		xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		xstream.alias("geneIdentifier", GeneIdentifier.class);
		xstream.alias("transcriptionFactor", TranscriptionFactor.class);
		xstream.alias("inputParameters", InputParameters.class);
		xstream.alias("iRegulonType", IRegulonType.class);
		xstream.aliasField("cisCisTargetType", InputParameters.class, "iRegulonType");
        xstream.registerConverter(new SpeciesNomenclatureConverter());

        try{
			return (Results) xstream.fromXML(xml);
		}catch (Exception e){
            throw new LoadException("The file can't be loaded: wrong format", e);
		}
	}
	
	public static String convertResultsToTSV(final Results results) {
        final Collection<Motif> motifs = results.getMotifs();
		//Header
		String tabfile = "Rank" + "\t"
						+ "Motif id" + "\t"
						+ "AUC" + "\t"
						+ "NES" + "\t"
						+ "ClusterCode" + "\t"
						+ "Transcription factor" + "\t"
						+ "Target genes" + "\n";
		for (Motif motif : motifs){
			//Motif specifications
			tabfile += motif.getRank() + "\t"
					+ motif.getName() + "\t"
					+ motif.getAUCValue() + "\t"
					+ motif.getNEScore() + "\t"
					+ motif.getClusterCode() + "\t";
			// a komma seperated list of TFs
			Iterator<TranscriptionFactor> tfIterator = motif.getTranscriptionFactors().iterator();
			while (tfIterator.hasNext()){
				tabfile += tfIterator.next().getName();
				if (tfIterator.hasNext()){
					tabfile += ",";
				}
			}
			tabfile += "\t";
			// a komma seperated list of TGs
			Iterator<CandidateTargetGene> tgIterator = motif.getCandidateTargetGenes().iterator();
			while (tgIterator.hasNext()){
				tabfile += tgIterator.next().getGeneName();
				if (tgIterator.hasNext()){
					tabfile += ",";
				}
			}
			// end of the line (of the motif)
			tabfile += "\n";
		}
		return tabfile;
	}
}
