package persistence;

import com.thoughtworks.xstream.XStream;
import domainmodel.*;
import view.parametersform.IRegulonType;

import java.util.Collection;
import java.util.Iterator;

import static view.ResourceAction.getBundle;


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
        xstream.alias("predictRegulatorsParameters", PredictRegulatorsParameters.class);
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
        xstream.alias("predictRegulatorsParameters", PredictRegulatorsParameters.class);
        xstream.alias("iRegulonType", IRegulonType.class);
        xstream.registerConverter(new SpeciesNomenclatureConverter());

        xstream.aliasField("cisCisTargetType", PredictRegulatorsParameters.class, "iRegulonType");

        xstream.omitField(Motif.class, "jobID");
        xstream.omitField(CandidateTargetGene.class, "numberOfMotifs");
        xstream.omitField(TranscriptionFactor.class, "motifs");
        xstream.aliasField("parameters", Results.class, "predictRegulatorsParameters");
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
        final boolean hasMotifCollection = results.hasMotifCollection();
        final boolean hasTrackCollection = results.hasTrackCollection();

        if (!results.hasParameters()) return "";

        StringBuilder results_tsv_builder = new StringBuilder();
        
        /* iRegulon parameters. */
        results_tsv_builder.append("; Name\t" + results.getName() + "\n"
                + "; Species and nomenclature\t" + results.getSpeciesNomenclature().toString() + "\n");

        if (hasMotifCollection) {
            results_tsv_builder.append("; Motif collection\t" + results.getMotifCollection() + "\n");
        }

        if (hasTrackCollection) {
            results_tsv_builder.append("; Track collection\t" + results.getTrackCollection() + "\n");
        }

        results_tsv_builder.append("; Minimum NEScore\t" + results.getEScore() + "\n"
                + "; Rank threshold for visualization\t" + results.getThresholdForVisualisation() + "\n"
                + "; ROC threshold for AUC calculation (%)\t" + results.getROCthresholdAUC() + "\n");

        if (hasMotifCollection) {
            results_tsv_builder.append("; Minimum identity between orthologous genes\t" + results.getMinOrthologous() + "\n"
                    + "; Maximum false discovery rate (FDR) on motif similarity\t" + results.getMaxMotifSimilarityFDR() + "\n"
                    + "; Motif rankings database\t" + results.getMotifRankingsDatabaseName() + "\n");
        }

        if (hasTrackCollection) {
            results_tsv_builder.append("; Track rankings database\t" + results.getTrackRankingsDatabaseName() + "\n");
        }

        results_tsv_builder.append("; Number of valid nodes\t" + results.getGenes().size() + "\n"
                + "; iRegulon version\t" + getBundle().getString("plugin_name_version_release") + "\n");

        if (results.isRegionBased()) {
            results_tsv_builder.append("; Overlap fraction\t" + results.getOverlap() + "\n");
            if (results.isDelineationBased()) {
                results_tsv_builder.append("; Putative regulatory region\t" + results.getDelineationName() + "\n");
            } else {
                results_tsv_builder.append("; Putative regulatory region\t[TSS-" + results.getUpstream() + "kb,TSS+" + results.getDownstream() + "kb]\n");
            }
        }

        if (hasMotifCollection) {
            /* Column headers for the motif lines. */
            results_tsv_builder.append("# Rank\t"
                    + "Motif id\t"
                    + "AUC\t"
                    + "NES\t"
                    + "ClusterCode\t"
                    + "Transcription factor\t"
                    + "Target genes\n");

            final Collection<Motif> motifs = results.getMotifs();

            /* Print the info for one motif on one line. */
            for (Motif motif : motifs) {
                /* Motif info. */
                results_tsv_builder.append(motif.getRank() + "\t"
                        + motif.getName() + "\t"
                        + motif.getAUCValue() + "\t"
                        + motif.getNEScore() + "\t"
                        + motif.getClusterCode() + "\t");

                /* A comma-separated list of transcription factors. */
                Iterator<TranscriptionFactor> tfIterator = motif.getTranscriptionFactors().iterator();
                while (tfIterator.hasNext()) {
                    results_tsv_builder.append(tfIterator.next().getName());
                    if (tfIterator.hasNext()) {
                        results_tsv_builder.append(",");
                    }
                }
                results_tsv_builder.append("\t");

                /* A comma-separated list of target genes. */
                Iterator<CandidateTargetGene> tgIterator = motif.getCandidateTargetGenes().iterator();
                while (tgIterator.hasNext()) {
                    results_tsv_builder.append(tgIterator.next().getGeneName());
                    if (tgIterator.hasNext()) {
                        results_tsv_builder.append(",");
                    }
                }

                /* End of the motif line. */
                results_tsv_builder.append("\n");
            }
        }

        if (hasTrackCollection) {
            /* Column headers for the track lines. */
            results_tsv_builder.append("# Rank\t"
                    + "Track id\t"
                    + "AUC\t"
                    + "NES\t"
                    + "ClusterCode\t"
                    + "Transcription factor\t"
                    + "Target genes\n");

            final Collection<Track> tracks = results.getTracks();

            /* Print the info for one track on one line. */
            for (Track track : tracks) {
                /* Motif info. */
                results_tsv_builder.append(track.getRank() + "\t"
                        + track.getName() + "\t"
                        + track.getAUCValue() + "\t"
                        + track.getNEScore() + "\t"
                        + track.getClusterCode() + "\t");

                /* A comma-separated list of transcription factors. */
                Iterator<TranscriptionFactor> tfIterator = track.getTranscriptionFactors().iterator();
                while (tfIterator.hasNext()) {
                    results_tsv_builder.append(tfIterator.next().getName());
                    if (tfIterator.hasNext()) {
                        results_tsv_builder.append(",");
                    }
                }
                results_tsv_builder.append("\t");

                /* A comma-separated list of target genes. */
                Iterator<CandidateTargetGene> tgIterator = track.getCandidateTargetGenes().iterator();
                while (tgIterator.hasNext()) {
                    results_tsv_builder.append(tgIterator.next().getGeneName());
                    if (tgIterator.hasNext()) {
                        results_tsv_builder.append(",");
                    }
                }

                /* End of the track line. */
                results_tsv_builder.append("\n");
            }
        }

        return results_tsv_builder.toString();
    }
}
