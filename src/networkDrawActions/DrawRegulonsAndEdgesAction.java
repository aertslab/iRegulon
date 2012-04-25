package networkDrawActions;

import cytoscape.*;
import cytoscape.view.CyNetworkView;


import iRegulonInput.IRegulonVisualStyle;
import iRegulonOutput.ComboboxAction;
import iRegulonOutput.SelectedMotif;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import domainModel.CandidateTargetGene;
import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class DrawRegulonsAndEdgesAction extends ComboboxAction implements ListSelectionListener{

	
	public DrawRegulonsAndEdgesAction(SelectedMotif selectedRegulatoryTree){
		super(selectedRegulatoryTree);
		if (selectedRegulatoryTree == null){
			throw new IllegalArgumentException();
		}
		putValue(Action.NAME, getBundle().getString("action_draw_edges_and_nodes_name"));
		putValue(Action.SHORT_DESCRIPTION, getBundle().getString("action_draw_edges_and_nodes_name"));
		String pathArrow = "/icons/arrow.png";
		java.net.URL imgURLArrow = getClass().getResource(pathArrow);
		ImageIcon iconArrow = new ImageIcon(imgURLArrow, "Add edges");
		putValue(Action.LARGE_ICON_KEY, iconArrow);
		putValue(Action.SMALL_ICON, iconArrow);
		setEnabled(false);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		setEnabled(! model.isSelectionEmpty());
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		Motif tree = this.getSelectedRegulatoryTree().getMotif();
		TranscriptionFactor tf = this.getTranscriptionFactor();
		CyNetwork network =  Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		if (network != null && view != null){
			CyNode nodeParent = DrawNodesAction.addNode(tf.getName(), network, view);
			DrawNodesAction.setAtribute(nodeParent, "ID", tf.getName());
			DrawNodesAction.setAtribute(nodeParent, "Regulatory function", "Regulator");
			//System.out.println("Draw Parent Node");
			//System.out.println("Children " + tree.getCandidateTargetGenes().size());
			for (CandidateTargetGene geneID : tree.getCandidateTargetGenes()){
				CyNode nodeChild = DrawNodesAction.addNode(geneID.getGeneName(), network, view);
				DrawNodesAction.setAtribute(nodeChild, "ID", tf.getName());
				//DrawNodesAction.addAtribute(nodeChild, "Regulatory function", "Target Gene");
				CyEdge edge;
				edge = DrawEdgesAction.addEdge(nodeParent, nodeChild, network, view, tree.getEnrichedMotifID());
				DrawEdgesAction.setAtribute(edge, "Regulator Gene", tf.getName());
				DrawEdgesAction.setAtribute(edge, "Target Gene", geneID.getGeneName());
				DrawEdgesAction.setAtribute(edge, "Regulatory function", "Predicted");
				DrawEdgesAction.setAtribute(edge, "Motif", tree.getEnrichedMotifID());
			}
			//if the node is a regulator and target at the same time, it must say regulator
			DrawNodesAction.setAtribute(nodeParent, "Regulatory function", "Regulator");
			DrawNodesAction.addAtribute(nodeParent, "Motif", tree.getEnrichedMotifID());
			
			//visual style
			IRegulonVisualStyle vsStyle = new IRegulonVisualStyle();
			view.redrawGraph(true,true);
		}
	}
	
	
	
	
	
}
