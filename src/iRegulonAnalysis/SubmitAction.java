package iRegulonAnalysis;

import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import httpConnection.ComputationalService;
import httpConnection.ComputationalServiceHTTP;
import iRegulonInput.IRegulonType;
import iRegulonInput.Parameters;
import resultsview.ResultsView;

import java.awt.event.ActionEvent;

import javax.swing.*;

import cytoscape.Cytoscape;

import java.util.*;

import domainmodel.Input;
import domainmodel.Motif;
import domainmodel.Results;


public class SubmitAction extends AbstractAction {
	private final Parameters parameters;
	
	public SubmitAction(final Parameters parameters) {
		super();
		this.parameters = parameters;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.parameters.generateInput();
		Input input = this.parameters.getInput();
		/*
		System.out.println("Node size = " + parameters.getInput().getNodes().length);
		System.out.println("Escore = " + parameters.getInput().getEScore());
		System.out.println("ROC threshold AUC = " + parameters.getInput().getROCthresholdAUC());
		System.out.println("Threshold for Vis = " + parameters.getInput().getThresholdForVisualisation());
		*/
		/*
		//Alle geneIDs selecteren
		Collection<GeneIdentifier> geneIDs = new ArrayList<GeneIdentifier>();
		GeneIdentifier geneID = null;
		for (int i = 0; i < input.getNodes().length; i++){
			geneID = new GeneIdentifier(input.getNodes()[i].getIdentifier(), input.getSpeciesNomenclature());
			geneIDs.add(geneID);
		}*/
		
		if (input.getIRegulonType().equals(IRegulonType.PREDICTED_REGULATORS)){
			final ComputationalService analyse = new ComputationalServiceHTTP();
			final List<Motif> motifList = analyse.findPredictedRegulators(input);
		
			if (! motifList.isEmpty()){
				final ResultsView outputView = new ResultsView(input.getName(), new Results(motifList, input));
                final CytoPanel panel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		        panel.setState(CytoPanelState.DOCK);
                outputView.addToPanel(panel);
			}
		}
		else{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"<html> " +
					"<body>" +
					"This option is not yet possible." +
					"</body>" +
					"</html>");
		}
	}

}
