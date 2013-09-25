package servercommunication;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeVersion;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import domainmodel.*;
import infrastructure.Logger;
import servercommunication.protocols.HTTPProtocol;
import servercommunication.protocols.Protocol;
import servercommunication.protocols.State;
import view.IRegulonResourceBundle;

import java.io.*;
import java.net.*;
import java.util.*;


public class ComputationalServiceHTTP extends IRegulonResourceBundle implements ComputationalService {
    private static final boolean DEBUG = false;
    private static final String PARAMETER_NAME = "featureIDandTarget=";

    private final String userAgent = this.getBundle().getString("User_Agent")
            + " Cytoscape: " + CytoscapeVersion.version
            + "; " + System.getProperty("os.name")
            + "; " + System.getProperty("os.version")
            + "; " + System.getProperty("os.arch") + ')';

    private final Protocol service = new HTTPProtocol();

    @Override
    public List<Motif> findPredictedRegulators(InputParameters input) throws ServerCommunicationException {

        final FindPredictedRegulatorsTask task = new FindPredictedRegulatorsTask(service, input);

        final JTaskConfig taskConfig = new JTaskConfig();
        taskConfig.setOwner(Cytoscape.getDesktop());
        taskConfig.displayCloseButton(true);
        taskConfig.displayCancelButton(true);
        taskConfig.displayStatus(true);
        taskConfig.setAutoDispose(true);

        TaskManager.executeTask(task, taskConfig);

        if (task.getFinishedState().equals(State.ERROR) && !task.getIsInterupted()) {
            throw new ServerCommunicationException(task.getErrorMessage());
        } else {
            final Collection<Motif> motifs = task.getMotifs();
            return new ArrayList<Motif>(motifs);
        }
    }

    @Override
    public Set<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome(final SpeciesNomenclature speciesNomenclature) throws ServerCommunicationException {
        if (speciesNomenclature == null) throw new IllegalArgumentException();
        final HttpURLConnection connection;
        try {
            connection = createConnection4BundleKey("URL_metatargetomes_query_factors");
        } catch (IOException e) {
            throw new ServerCommunicationException("Getting transcription factors with predicted targetome failed. Server is not available.");
        }

        try {
            /* Send the nomenclature code. */
            final StringBuilder builder = new StringBuilder();
            builder.append("SpeciesNomenclatureCode=");
            builder.append(speciesNomenclature.getCode());
            send(connection, builder.toString());

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ServerCommunicationException("Retrieving '" + this.getBundle().getString("URL_metatargetomes_query_factors") + "' failed.");
            }

            /* Get the response. */
            final Set<GeneIdentifier> result = new HashSet<GeneIdentifier>();

            read(connection, new LineProcessor() {
                @Override
                public void process(final String line) throws ServerCommunicationException {
                    String[] columns = line.split("\t");

                    if (columns.length == 2) {
                        if (columns[0].equals("TF:")) {
                            result.add(new GeneIdentifier(columns[1], speciesNomenclature));
                        } else if (columns[0].equals("ERROR:")) {
                            throw new ServerCommunicationException(columns[1]);
                        }
                    }
                }
            });

            return result;
        } catch (IOException e) {
            Logger.getInstance().error(e);
            throw new ServerCommunicationException("Error while trying to communicate with server: \"" + e.getMessage() + "\".", e);
        }
    }

    @Override
    public List<CandidateTargetGene> queryPredictedTargetome(final GeneIdentifier factor, List<TargetomeDatabase> databases,
                                                             final int occurrenceCountThreshold, final int maxNodeCount)
            throws ServerCommunicationException {

        if (factor == null || databases == null) {
            throw new IllegalArgumentException();
        }
        final HttpURLConnection connection;
        try {
            connection = createConnection4BundleKey("URL_metatargetomes_query_targetome");
        } catch (IOException e) {
            throw new ServerCommunicationException("Getting predicted targetome failed. Server is not available.");
        }

        try {
            /* Send the necessary information. */
            final StringBuilder builder = new StringBuilder();
            builder.append("SpeciesNomenclatureCode=");
            builder.append(factor.getSpeciesNomenclature().getCode());
            builder.append("&");
            builder.append("GeneIdentifier=");
            builder.append(factor.getGeneName());
            builder.append("&");
            builder.append("TargetomeDatabaseCode=");
            if (!databases.isEmpty()) {
                builder.append(databases.get(0).getDbCode());
            }
            for (TargetomeDatabase database : databases.subList(1, databases.size())) {
                builder.append(",");
                builder.append(database.getDbCode());
            }

            send(connection, builder.toString());

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new ServerCommunicationException("Retrieving '" + this.getBundle().getString("URL_metatargetomes_query_targetome") + "' failed.");
            }

            /* Get the response. */
            final List<CandidateTargetGene> result = new ArrayList<CandidateTargetGene>();

            read(connection, new LineProcessor() {
                @Override
                public void process(final String line) throws ServerCommunicationException {
                    String[] columns = line.split("\t");

                    if (columns.length == 3) {
                        if (columns[0].equals("gene_occurrence:")) {
                            try {
                                int occurrenceCount = Integer.parseInt(columns[2]);

                                if (occurrenceCount < occurrenceCountThreshold) return;

                                result.add(new CandidateTargetGene(
                                        new GeneIdentifier(columns[1],
                                                factor.getSpeciesNomenclature()),
                                        occurrenceCount));
                            } catch (NumberFormatException e) {
                                throw new ServerCommunicationException("Gene occurrence count is not a number.");
                            }
                        }
                    } else if (columns.length == 2) {
                        if (columns[0].equals("ERROR:")) {
                            throw new ServerCommunicationException(columns[1]);
                        }

                    }
                }
            });

            Collections.sort(result, new Comparator<CandidateTargetGene>() {
                @Override
                public int compare(CandidateTargetGene o1, CandidateTargetGene o2) {
                    int r = new Integer(o2.getRank()).compareTo(o1.getRank());
                    if (r != 0) return r;
                    return o1.getGeneName().compareToIgnoreCase(o2.getGeneName());
                }
            });

            if ((maxNodeCount <= 0) || (result.size() <= maxNodeCount)) {
                return result;
            } else {
                final int minOccurrenceCount = result.get(maxNodeCount - 1).getRank();
                return filter(result, minOccurrenceCount);
            }
        } catch (IOException e) {
            Logger.getInstance().error(e);
            throw new ServerCommunicationException("Error while trying to communicate with server: \"" + e.getMessage() + "\".", e);
        }
    }

    private List<CandidateTargetGene> filter(List<CandidateTargetGene> genes, int minOccurrenceCount) {
        final List<CandidateTargetGene> results = new ArrayList<CandidateTargetGene>();
        for (CandidateTargetGene gene : genes) {
            if (gene.getRank() >= minOccurrenceCount) results.add(gene);
        }
        return results;
    }

    private HttpURLConnection createConnection4BundleKey(String bundleKey) throws IOException {
        return createConnection(getBundle().getString(bundleKey));
    }

    private HttpURLConnection createConnection(String resource) throws IOException {
        final URL url = new URL(resource);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private void send(final URLConnection connection, String message) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(message);
        writer.flush();
        writer.close();
    }

    private void read(final URLConnection connection, final LineProcessor processor) throws IOException, ServerCommunicationException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            processor.process(line.trim());
        }
        reader.close();
    }

    private static interface LineProcessor {
        public void process(String line) throws ServerCommunicationException;
    }

    @Override
    public List<EnhancerRegion> getEnhancerRegions(final AbstractMotif motif) throws ServerCommunicationException {
        final HttpURLConnection connection;
        try {
            final String uri = getBundle().getString("URL_motifBedGenerator") + "?" + generateParameters(motif);
            connection = createConnection(uri);
        } catch (IOException e) {
            throw new ServerCommunicationException("Unable to get enhancer regions. Server is not available.");
        }

        try {
            final List<EnhancerRegion> result = new ArrayList<EnhancerRegion>();
            read(connection, new LineProcessor() {
                @Override
                public void process(final String line) throws ServerCommunicationException {
                    if (DEBUG) Logger.getInstance().warning(line);

                    if (line.startsWith("#") || line.trim().equals("")) return;
                    if (line.startsWith("browser") || line.startsWith("track")) return;

                    final EnhancerRegion region = EnhancerRegion.fromText(line);
                    if (region != null) {
                        result.add(region);
                    } else {
                        throw new ServerCommunicationException("Invalid format of message received from server: \"" + line + "\".");
                    }
                }
            });
            Collections.sort(result);
            return result;
        } catch (IOException e) {
            Logger.getInstance().error(e);
            throw new ServerCommunicationException("Error while trying to communicate with server: \"" + e.getMessage() + "\".", e);
        }
    }

    @Override
    public URI getLink2GenomeBrowser4EnhancerRegions(AbstractMotif motif) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getBundle().getString("URL_UCSC_Regions_part1"));
        builder.append(motif.getCandidateTargetGenes().get(0).getSpeciesNomenclature().getAssembly());
        builder.append(getBundle().getString("URL_UCSC_Regions_part2"));
        builder.append(getBundle().getString("URL_motifBedGenerator"));
        builder.append("?");
        builder.append(generateParameters(motif));
        try {
            return new URI(builder.toString());
        } catch (URISyntaxException e) {
            Logger.getInstance().error("Wrong URI = " + builder.toString());
            return null;
        }
    }

    private String generateParameters(final AbstractMotif motif) {
        final StringBuilder parameters = new StringBuilder();
        parameters.append(PARAMETER_NAME);
        parameters.append(motif.getDatabaseID());
        parameters.append(':');
        if (motif.getCandidateTargetGenes().size() >= 1) {
            parameters.append(motif.getCandidateTargetGenes().get(0).getGeneName());
        }
        parameters.append(':');
        if (!motif.getTranscriptionFactors().isEmpty()) {
            parameters.append(motif.getTranscriptionFactors().get(0));
        }
        for (TranscriptionFactor tf : motif.getTranscriptionFactors().subList(0, motif.getTranscriptionFactors().size())) {
            parameters.append(",");
            parameters.append(tf.getName());
        }
        return parameters.toString();
    }
}
