package networkDrawActions;

import cytoscape.*;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;


import iRegulonInput.IRegulonVisualStyle;
import iRegulonOutput.ComboboxAction;
import iRegulonOutput.SelectedMotif;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		DrawNodesAction drawNodesAction = new DrawNodesAction(this.getSelectedRegulatoryTree());
		Motif tree = this.getSelectedRegulatoryTree().getMotif();
		TranscriptionFactor tf = this.getTranscriptionFactor();
		CyNetwork network =  Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		Map<String, List<CyNode>> map = drawNodesAction.getNodeMap(this.getSelectedRegulatoryTree().getAttributeName(), network.nodesList());
		if (network != null && view != null){
			List<CyNode> tfNodeList = map.get(this.getTranscriptionFactor().getGeneID());
			if (tfNodeList == null || tfNodeList.isEmpty()){
				tfNodeList = new ArrayList<CyNode>();
				CyNode parent = DrawNodesAction.addNode(tf.getName(), network, view);
				tfNodeList.add(parent);
				DrawNodesAction.setAtribute(parent, "ID", tf.getName());
			}
			for (CyNode parent : tfNodeList){
				DrawNodesAction.setAtribute(parent, this.getSelectedRegulatoryTree().getAttributeName(), tf.getName());
				DrawNodesAction.setAtribute(parent, "Regulatory function", "Regulator");
				DrawNodesAction.addAtribute(parent, "Motif", tree.getEnrichedMotifID());
			}
			//System.out.println("Draw Parent Node");
			//System.out.println("Children " + tree.getCandidateTargetGenes().size());
			for (CandidateTargetGene geneID : tree.getCandidateTargetGenes()){
				List<CyNode> tgNodeList = map.get(geneID.getGeneName());
				if (tgNodeList == null || tgNodeList.isEmpty()){
					tgNodeList = new ArrayList<CyNode>();
					CyNode child = DrawNodesAction.addNode(geneID.getGeneName(), network, view);
					tgNodeList.add(child);
					DrawNodesAction.setAtribute(child, "ID", geneID.getGeneName());
				}
				for (CyNode tgNode : tgNodeList){
					DrawNodesAction.setAtribute(tgNode, this.getSelectedRegulatoryTree().getAttributeName(), geneID.getGeneName());
					//DrawNodesAction.addAtribute(nodeChild, "Regulatory function", "Target Gene");
					for (CyNode tfNode : tfNodeList){
						this.drawEdgeAndAttributes(tfNode, tgNode, network, view, tree, geneID, tf);
					}
				}
			}
			//if the node is a regulator and target at the same time, it must say regulator
			for (CyNode parent : tfNodeList){
				DrawNodesAction.setAtribute(parent, "Regulatory function", "Regulator");
			}
			//visual style
			VisualMappingManager manager = Cytoscape.getVisualMappingManager();
			CalculatorCatalog catalog = manager.getCalculatorCatalog();
			VisualStyle vs = catalog.getVisualStyle(getBundle().getString("vizmap_name"));
			if (vs != null){
				catalog.removeVisualStyle(getBundle().getString("vizmap_name"));
				IRegulonVisualStyle vsStyle = new IRegulonVisualStyle();
			}
			manager.setVisualStyle(manager.getVisualStyle().getName());
			view.redrawGraph(true,true);
		}
	}
	
	
	private void drawEdgeAndAttributes(CyNode parent, CyNode target, 
						CyNetwork network, CyNetworkView view, Motif motif, 
						CandidateTargetGene tg, TranscriptionFactor tf){
		CyEdge edge;
		edge = DrawEdgesAction.addEdge(parent, target, network, view, motif.getEnrichedMotifID());
		DrawEdgesAction.setAtribute(edge, "Regulator Gene", tf.getName());
		DrawEdgesAction.setAtribute(edge, "Target Gene", tg.getGeneName());
		DrawEdgesAction.setAtribute(edge, "Regulatory function", "Predicted");
		DrawEdgesAction.setAtribute(edge, "Motif", motif.getEnrichedMotifID());
		DrawEdgesAction.setAtribute(edge, "featureID", "" + motif.getFeatureID());
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		cyEdgeAttrs.setUserVisible("featureID", false);
	}
	
	
	
	
}