package view.parametersform.actions;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.ResourceAction;
import view.parametersform.IRegulonType;
import view.parametersform.PredictedRegulatorsParameters;
import view.resultspanel.ResultsView;

import java.awt.event.ActionEvent;

import javax.swing.*;

import cytoscape.Cytoscape;

import java.util.*;

import domainmodel.InputParameters;
import domainmodel.Motif;
import domainmodel.Results;


public class PredictRegulatorsAction extends ResourceAction {
    private static final String NAME = "action_submit_analysis";

    private final ComputationalService service = new ComputationalServiceHTTP();
	private final PredictedRegulatorsParameters parameters;
	
	public PredictRegulatorsAction(final PredictedRegulatorsParameters parameters) {
		super(NAME);
		this.parameters = parameters;
	}

    public PredictedRegulatorsParameters getParameters() {
        return parameters;
    }

    @Override
	public void actionPerformed(ActionEvent event) {
        this.parameters.generateInput();
        final InputParameters input = this.parameters.getInput();
        if (!input.getIRegulonType().equals(IRegulonType.PREDICTED_REGULATORS)) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "This option is not yet implemented.");
            return;
        }

        final List<Motif> motifList;
        try {
            motifList = service.findPredictedRegulators(input);
        } catch (ServerCommunicationException e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!motifList.isEmpty()) {
            final ResultsView outputView = new ResultsView(input.getName(), new Results(motifList, input));
            final CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
            panel.setState(CytoPanelState.DOCK);
            outputView.addToPanel(panel);
        } else {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "Not a single motif is enriched for your input gene signature.");
        }
    }
}
