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
    private final CyLogHandler logger = ConsoleLogger.getLogger();

    private Service service = new HTTPService();

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
		    final URLConnection connection = createConnection("URL_targetomes");

            // Send the nomenclature code ...
            final StringBuilder builder = new StringBuilder();
            builder.append("SpeciesNomenclatureCode=");
            builder.append(speciesNomenclature.getCode());
            send(connection, builder.toString());

		    // Get the response ...
		    final List<GeneIdentifier> result = new ArrayList<GeneIdentifier>();
            read(connection, new LineProcessor() {
                @Override
                public void process(final String line) {
                    if (line.startsWith("ID=")) {
                        result.add(new GeneIdentifier(line.trim().substring(3), speciesNomenclature));
                    }
                }
            });

            return result;
		} catch (IOException e) {
            logger.handleLog(LogLevel.LOG_ERROR, e.getMessage());
            throw new ServerCommunicationException("Error while trying to communicate with server.", e);
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

    private void read(final URLConnection connection, final LineProcessor processor) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		final List<GeneIdentifier> result = new ArrayList<GeneIdentifier>();
        String line;
        while ((line = reader.readLine()) != null) {
		     processor.process(line);
		}
		reader.close();
    }

    @Override
    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor, List<TargetomeDatabase> databases) throws ServerCommunicationException {
        return Collections.emptyList(); //TODO: implement this ...
    }

    private static interface LineProcessor {
        public void process(String line);
    }
}
