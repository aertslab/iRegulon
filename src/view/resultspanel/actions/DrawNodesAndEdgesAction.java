package view.resultspanel.actions;

import cytoscape.*;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;


import view.parametersform.IRegulonVisualStyle;
import view.resultspanel.TFComboBox;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class DrawNodesAndEdgesAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_draw_nodes_and_edges";

    private final TFComboBox selectedTranscriptionFactor;

	
	public DrawNodesAndEdgesAction(SelectedMotif selectedMotif, final TFComboBox selectedTranscriptionFactor) {
		super(NAME, selectedMotif, selectedTranscriptionFactor);
		if (selectedMotif == null) throw new IllegalArgumentException();
		setEnabled(false);
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DrawNodesAction drawNodesAction = new DrawNodesAction(this.getSelectedMotif(), selectedTranscriptionFactor);
		Motif tree = this.getSelectedMotif().getMotif();
		TranscriptionFactor tf = this.getTranscriptionFactor();
		CyNetwork network =  Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		Map<String, List<CyNode>> map = drawNodesAction.getNodeMap(this.getSelectedMotif().getAttributeName(), network.nodesList());
		if (network != null && view != null){
			List<CyNode> tfNodeList = map.get(this.getTranscriptionFactor().getGeneID());
			if (tfNodeList == null || tfNodeList.isEmpty()){
				tfNodeList = new ArrayList<CyNode>();
				CyNode parent = DrawNodesAction.addNode(tf.getName(), network, view);
				tfNodeList.add(parent);
				DrawNodesAction.setAtribute(parent, "ID", tf.getName());
			}

			for (CyNode parent : tfNodeList){
				DrawNodesAction.setAtribute(parent, this.getSelectedMotif().getAttributeName(), tf.getName());
				DrawNodesAction.setAtribute(parent, "Regulatory function", "Regulator");
				DrawNodesAction.addAtribute(parent, "Motif", tree.getID());
			}

			for (CandidateTargetGene geneID : tree.getCandidateTargetGenes()){
				List<CyNode> tgNodeList = map.get(geneID.getGeneName());
				if (tgNodeList == null || tgNodeList.isEmpty()){
					tgNodeList = new ArrayList<CyNode>();
					CyNode child = DrawNodesAction.addNode(geneID.getGeneName(), network, view);
					tgNodeList.add(child);
					DrawNodesAction.setAtribute(child, "ID", geneID.getGeneName());
				}
				for (CyNode tgNode : tgNodeList){
					DrawNodesAction.setAtribute(tgNode, this.getSelectedMotif().getAttributeName(), geneID.getGeneName());
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
		edge = DrawEdgesAction.addEdge(parent, target, network, view, motif.getID());
		DrawEdgesAction.setAtribute(edge, "Regulator Gene", tf.getName());
		DrawEdgesAction.setAtribute(edge, "Target Gene", tg.getGeneName());
		DrawEdgesAction.setAtribute(edge, "Regulatory function", "Predicted");
		DrawEdgesAction.setAtribute(edge, "Motif", motif.getID());
		DrawEdgesAction.setAtribute(edge, "featureID", "" + motif.getFeatureID());
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		cyEdgeAttrs.setUserVisible("featureID", false);
	}
}
