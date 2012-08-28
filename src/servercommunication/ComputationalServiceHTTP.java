package servercommunication;

import domainmodel.*;
import servercommunication.protocols.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;


import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;


public class ComputationalServiceHTTP implements ComputationalService {
	@Override
	public List<Motif> findPredictedRegulators(InputParameters input) {
		final Service service = new HTTPService();
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor, List<TargetomeDatabase> databases) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
