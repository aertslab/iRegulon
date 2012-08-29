package servercommunication;

import domainmodel.*;
import servercommunication.protocols.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    public List<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome() {
        try {
			// Do the request ...
			final URL url = new URL(getBundle().getString("URL_targetomes"));
		    final URLConnection connection = url.openConnection();
		    connection.setDoInput(true);
		    connection.setDoOutput(false);
            connection.connect();

		    // Get the response ... TODO: Check for errors ...
		    final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    final List<GeneIdentifier> result = new ArrayList<GeneIdentifier>();
            String line;
            while ((line = reader.readLine()) != null) {
		        result.add(new GeneIdentifier(line.trim(), SpeciesNomenclature.HOMO_SAPIENS_HGNC));
		    }
		    reader.close();
            return result;
		} catch (Exception e) {
            //TODO: use error handling ...

			System.out.println(e.toString());
            return Collections.emptyList();
		}
    }

    @Override
    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor, List<TargetomeDatabase> databases) {
        return Collections.emptyList(); //TODO: implement this ...
    }
}
