package servercommunication;

import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import domainmodel.*;
import servercommunication.protocols.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;


import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import view.IRegulonResourceBundle;


public class ComputationalServiceHTTP extends IRegulonResourceBundle implements ComputationalService {
    private static final boolean DEBUG = true;

    private final CyLogHandler logger = ConsoleLogger.getLogger();
    private final Service service = new HTTPService();

    @Override
	public List<Motif> findPredictedRegulators(InputParameters input) {
		final ClassicalTask task = new ClassicalTask(service, input);
		
		final JTaskConfig taskConfig = new JTaskConfig();
		taskConfig.setOwner(Cytoscape.getDesktop());
		taskConfig.displayCloseButton(true);
		taskConfig.displayCancelButton(true);
		taskConfig.displayStatus(true);
		taskConfig.setAutoDispose(true);

		TaskManager.executeTask(task, taskConfig);

		if (task.getFinishedState().equals(State.ERROR) && !task.getIsInterupted()) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), task.getErrorMessage());
			return Collections.emptyList();
		} else {

			final Collection<Motif> motifs = task.getMotifs();
            return new ArrayList<Motif>(motifs);
		}
	}

    @Override
    public List<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome(final SpeciesNomenclature speciesNomenclature) throws ServerCommunicationException {
        if (speciesNomenclature == null) throw new IllegalArgumentException();
        try {
		    final URLConnection connection = createConnection("URL_metatargetomes_query_factors");

            // Send the nomenclature code ...
            final StringBuilder builder = new StringBuilder();
            builder.append("SpeciesNomenclatureCode=");
            builder.append(speciesNomenclature.getCode());
            builder.append("\n");
            send(connection, builder.toString());

		    // Get the response ...
		    final List<GeneIdentifier> result = new ArrayList<GeneIdentifier>();
            read(connection, new LineProcessor() {
                @Override
                public void process(final String line) throws ServerCommunicationException {
                    if (DEBUG) logger.handleLog(LogLevel.LOG_ERROR, line);

                    if (line.startsWith("#") || line.trim().equals("")) return;

                    final String prefix = "ID=";
                    if (line.startsWith(prefix)) {
                        result.add(new GeneIdentifier(line.substring(prefix.length()), speciesNomenclature));
                    } else {
                        throw new ServerCommunicationException("Invalid format of message received from server: \"" + line + "\".");
                    }
                }
            });

            return result;
		} catch (IOException e) {
            logger.handleLog(LogLevel.LOG_ERROR, e.getMessage());
            throw new ServerCommunicationException("Error while trying to communicate with server: \"" + e.getMessage() + "\".", e);
		}
    }

    @Override
    public List<CandidateTargetGene> queryPredictedTargetome(final GeneIdentifier factor, List<TargetomeDatabase> databases) throws ServerCommunicationException {
        if (factor == null || databases == null) {
            throw new IllegalArgumentException();
        }
        try {
		    final URLConnection connection = createConnection("URL_metatargetomes_query_targetome");

            // Send the necessary information ...
            final StringBuilder builder = new StringBuilder();
            builder.append("SpeciesNomenclatureCode=");
            builder.append(factor.getSpeciesNomenclature().getCode());
            builder.append("&");
            builder.append("GeneIdentifier=");
            builder.append(factor.getGeneName());
            builder.append("&");
            builder.append("TargetomeDatabaseCode=");
            if (!databases.isEmpty()) {
                builder.append("\"");
                builder.append(databases.get(0).getDbCode());
                builder.append("\"");
            }
            for (TargetomeDatabase database : databases.subList(1, databases.size())) {
                builder.append(",\"");
                builder.append(database.getDbCode());
                builder.append("\"");
            }
            builder.append("\n");
            send(connection, builder.toString());

		    // Get the response ...
		    final List<CandidateTargetGene> result = new ArrayList<CandidateTargetGene>();
            read(connection, new LineProcessor() {
                @Override
                public void process(final String line) throws ServerCommunicationException {
                    if (DEBUG) logger.handleLog(LogLevel.LOG_ERROR, line);

                    if (line.startsWith("#") || line.trim().equals("")) return;

                    final String prefix = "ID_occurenceCount=";
                    if (line.startsWith(prefix)) {
                        final String[] columns = line.substring(prefix.length()).split(";");
                        if (columns.length != 2) {
                            throw new ServerCommunicationException("Invalid format of message received from server: \"" + line + "\".");
                        }
                        try {
                            int occurenceCount = Integer.parseInt(columns[1]);
                            result.add(new CandidateTargetGene(
                                new GeneIdentifier(columns[0], factor.getSpeciesNomenclature()),
                                occurenceCount));
                        } catch (NumberFormatException e) {
                            throw new ServerCommunicationException("Invalid format of message received from server: \"" + line + "\".");
                        }
                    } else {
                        throw new ServerCommunicationException("Invalid format of message received from server: \"" + line + "\".");
                    }
                }
            });

            return result;
		} catch (IOException e) {
            logger.handleLog(LogLevel.LOG_ERROR, e.getMessage());
            throw new ServerCommunicationException("Error while trying to communicate with server: \"" + e.getMessage() + "\".", e);
		}
    }

    private URLConnection createConnection(String bundleKey) throws IOException {
        final URL url = new URL(getBundle().getString(bundleKey));
		final URLConnection connection = url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
        return connection;
    }

    private void send(final URLConnection connection, String message) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(message);
        writer.newLine();
        writer.flush();
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
}
