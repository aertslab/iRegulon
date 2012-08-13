package iRegulonAnalysis;

import httpConnection.ComputationalService;
import httpConnection.ComputationalServiceHTTP;
import iRegulonInput.IRegulonType;
import iRegulonInput.Parameters;
import iRegulonOutput.ResultsView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

import java.util.*;

import domainModel.Input;
import domainModel.Motif;
import domainModel.Results;


public class SubmitAction extends AbstractAction {
	private final Parameters parameters;
	
	public SubmitAction(final Parameters parameters) {
		super();
		this.parameters = parameters;
		//this.putValue(SHORT_DESCRIPTION, "Boe");
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
			//Stub aanroepen
			ComputationalService analyse = new ComputationalServiceHTTP();
			//type oproepen
			
			List<Motif> motifList = analyse.findPredictedRegulators(input);
		
			if (! motifList.isEmpty()){
				ResultsView outputView = new ResultsView(input.getName());
				Results result = new Results(motifList, input);
				outputView.drawPanel(result);
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
