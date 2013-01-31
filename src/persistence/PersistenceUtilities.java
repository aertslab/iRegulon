package persistence;

import view.parametersform.IRegulonType;

import java.util.Collection;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;

import domainmodel.*;


public class PersistenceUtilities {
    public static final String NATIVE_FILE_EXTENSION = ".irf";
    public static final String TSV_FILE_EXTENSION = ".tsv";

	private PersistenceUtilities() {
	}
	
	public static String convertResultsToXML(final Results result) {
		final XStream xstream = new XStream();
		xstream.alias("motif", Motif.class);
		xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		xstream.alias("geneIdentifier", GeneIdentifier.class);
		xstream.alias("transcriptionFactor", TranscriptionFactor.class);
		xstream.alias("inputParameters", InputParameters.class);
		xstream.alias("iRegulonType", IRegulonType.class);
        xstream.omitField(CandidateTargetGene.class, "numberOfMotifs");
        xstream.omitField(TranscriptionFactor.class, "motifs");
        xstream.omitField(RankingsDatabase.class, "type");
        xstream.omitField(RankingsDatabase.class, "speciesNomenclature");
        xstream.omitField(RankingsDatabase.class, "motifCollection");
        xstream.omitField(RankingsDatabase.class, "speciesCount");
        xstream.omitField(RankingsDatabase.class, "putativeRegulatoryRegion");
        xstream.omitField(RankingsDatabase.class, "gene2regionDelineations");

		xstream.setMode(XStream.NO_REFERENCES);
		xstream.registerConverter(new SpeciesNomenclatureConverter());
		
		return xstream.toXML(result);
	}
	
	public static Results loadResultsFromXML(final String xml) throws LoadException {
		final XStream xstream = new XStream();
		xstream.alias("motif", Motif.class);
		xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		xstream.alias("geneIdentifier", GeneIdentifier.class);
		xstream.alias("transcriptionFactor", TranscriptionFactor.class);
		xstream.alias("inputParameters", InputParameters.class);
		xstream.alias("iRegulonType", IRegulonType.class);
        xstream.registerConverter(new SpeciesNomenclatureConverter());

        xstream.aliasField("cisCisTargetType", InputParameters.class, "iRegulonType");

        xstream.omitField(Motif.class, "jobID");
        xstream.omitField(CandidateTargetGene.class, "numberOfMotifs");
        xstream.omitField(TranscriptionFactor.class, "motifs");
        xstream.aliasField("parameters", Results.class, "inputParameters");
        xstream.aliasField("enrichedMotifID", Motif.class, "name");
        xstream.aliasField("minOrthologous", TranscriptionFactor.class, "minOrthologousIdentity");

        xstream.omitField(RankingsDatabase.class, "type");
        xstream.omitField(RankingsDatabase.class, "speciesNomenclature");
        xstream.omitField(RankingsDatabase.class, "motifCollection");
        xstream.omitField(RankingsDatabase.class, "speciesCount");
        xstream.omitField(RankingsDatabase.class, "putativeRegulatoryRegion");
        xstream.omitField(RankingsDatabase.class, "gene2regionDelineations");

        try {
            return (Results) xstream.fromXML(xml);
        } catch (Exception e) {
            throw new LoadException("The IRF file has an invalid format.", e);
        }
	}
	
	public static String convertResultsToTSV(final Results results) {
        final Collection<Motif> motifs = results.getMotifs();

        if (!results.hasParameters()) return "";

        // iRegulon parameters.
        String tabfile = "Name\t" + results.getName() + "\n"
                            + "Species and nomenclature\t" + results.getSpeciesNomenclature().toString() + "\n"
                            + "Mimumum NEScore\t" + results.getEScore() + "\n"
                            + "Rank threshold for visualisation\t" + results.getThresholdForVisualisation() + "\n"
                            + "ROC threshold for AUC calculation (%)\t" + results.getROCthresholdAUC() + "\n"
                            + "Minimum orthologous identity (%)\t" + results.getMinOrthologous() + "\n"
                            + "Maximum motif similarity (FDR)\t" + results.getMaxMotifSimilarityFDR() + "\n"
                            + "Database\t" + results.getDatabaseName() + "\n";

        if (results.isRegionBased()) {
            tabfile += "Overlap fraction\t" + results.getOverlap() + "\n";
            if (results.isDelineationBased()) {
                tabfile += "Putative regulatory region\t" + results.getDelineationName() + "\n";
            } else {
                tabfile += "Putative regulatory region\t[TSS-" + results.getUpstream() + "kb,TSS+" + results.getDownstream() + "kb]\n";
            }
        }

		// Column headers.
		tabfile += "\nRank\t"
					+ "Motif id\t"
					+ "AUC\t"
					+ "NES\t"
					+ "ClusterCode\t"
					+ "Transcription factor\t"
					+ "Target genes\n";

        // Print the info for one motif on one line.
		for (Motif motif : motifs){
			// Motif info.
			tabfile += motif.getRank() + "\t"
					+ motif.getName() + "\t"
					+ motif.getAUCValue() + "\t"
					+ motif.getNEScore() + "\t"
					+ motif.getClusterCode() + "\t";
			// A comma separated list of transcription factors.
			Iterator<TranscriptionFactor> tfIterator = motif.getTranscriptionFactors().iterator();
			while (tfIterator.hasNext()){
				tabfile += tfIterator.next().getName();
				if (tfIterator.hasNext()){
					tabfile += ",";
				}
			}
			tabfile += "\t";
			// A comma separated list of target genes.
			Iterator<CandidateTargetGene> tgIterator = motif.getCandidateTargetGenes().iterator();
			while (tgIterator.hasNext()){
				tabfile += tgIterator.next().getGeneName();
				if (tgIterator.hasNext()){
					tabfile += ",";
				}
			}
			// End of the motif line.
			tabfile += "\n";
		}
		return tabfile;
	}
}
