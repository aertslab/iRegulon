package view.parametersform.actions;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import view.ResourceAction;
import view.parametersform.IRegulonType;
import view.parametersform.Parameters;
import view.resultspanel.ResultsView;

import java.awt.event.ActionEvent;

import javax.swing.*;

import cytoscape.Cytoscape;

import java.util.*;

import domainmodel.Input;
import domainmodel.Motif;
import domainmodel.Results;


public class SubmitAnalysisAction extends ResourceAction {
    private static final String NAME = "action_submit_analysis";

	private final Parameters parameters;
	
	public SubmitAnalysisAction(final Parameters parameters) {
		super(NAME);
		this.parameters = parameters;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        this.parameters.generateInput();
        Input input = this.parameters.getInput();
        if (input.getIRegulonType().equals(IRegulonType.PREDICTED_REGULATORS)) {
            final ComputationalService analyse = new ComputationalServiceHTTP();
            final List<Motif> motifList = analyse.findPredictedRegulators(input);

            if (!motifList.isEmpty()) {
                final ResultsView outputView = new ResultsView(input.getName(), new Results(motifList, input));
                final CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
                panel.setState(CytoPanelState.DOCK);
                outputView.addToPanel(panel);
            } else {
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Not a single motif is enriched for your input gene signature.");
            }
        } else {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "This option is not yet implemented.");
        }
    }
}
