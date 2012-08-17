package view.resultspanel.actions;

import view.parametersform.IRegulonVisualStyle;
import view.parametersform.NodesActions;
import view.resultspanel.TFComboBox;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.*;
import giny.view.*;

import domainmodel.GeneIdentifier;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class DrawEdgesAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_draw_edges";

	
	public DrawEdgesAction(SelectedMotif selectedMotif, final TFComboBox selectedTranscriptionFactor) {
		super(NAME, selectedMotif, selectedTranscriptionFactor);
		if (selectedMotif == null) throw new IllegalArgumentException();
		setEnabled(false);
	}

	public Motif getListSelectedRegulatoryTree(){
		return this.getSelectedMotif().getMotif();
	}
	
	public TranscriptionFactor getSelectedTranscriptionFactor() {
		return this.getTranscriptionFactor();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		/*List<Object[]> TFRegulons = this.getListSelectedRegulatoryTree();
		System.out.println("Regulons to Draw: " + TFRegulons.size());
		CyNode node1 = null;
		CyNode node2 = null;
		ArrayList<CyNode> nodes = CisTargetXNodes.getAlldNodes();
		for (Object[] regulonTarget : TFRegulons){
			RegulatoryTree regulon = (RegulatoryTree) regulonTarget[0];
			System.out.println(regulon.getParentName());
			node1 = this.getCyNode(regulon.getParent(), nodes);
			if (node1 != null){
				RegulatoryLink link = (RegulatoryLink) regulonTarget[1];
				node2 = this.getCyNode(link.getGeneID(), nodes);
				if (node1 != null && node2 != null){
					System.out.println("TargetGene " + link.getGeneName());
					CyEdge edge;
					if (regulon.isEdgeDirectedFromParentToChildren()){
						edge = addEdge(node1, node2);
						this.addAtribute(edge, "Regulator Gene", regulon.getParentName());
						this.addAtribute(edge, "Target Gene", link.getGeneName());
					}else {
						edge = addEdge(node2, node1);
						this.addAtribute(edge, "Regulator Gene", link.getGeneName());
						this.addAtribute(edge, "Target Gene", regulon.getParentName());
					}
					this.addAtribute(edge, "Enriched Motif", link.getEnrichedMotif());
					
				}
			}
		}*/
		
		Motif tree = this.getListSelectedRegulatoryTree();
		CyNode node1 = null;
		CyNode node2 = null;
		ArrayList<CyNode> nodes = NodesActions.getAllNodes();
		if (nodes.size() != 0){
			node1 = this.getCyNode(this.getSelectedTranscriptionFactor().getGeneID(), nodes);
			if (node1 != null){
				for (domainmodel.CandidateTargetGene child : tree.getCandidateTargetGenes()){
					GeneIdentifier childID = child.getGeneID();
					node2 = this.getCyNode(childID, nodes);
					if (node2 != null){
						CyEdge edge;
						edge = addEdge(node1, node2, tree.getEnrichedMotifID());
						this.setAtribute(edge, "Regulator Gene", this.getSelectedTranscriptionFactor().getGeneID().getGeneName());
						this.setAtribute(edge, "Target Gene", childID.getGeneName());
						this.setAtribute(edge, "Regulatory function", "Predicted");
						this.setAtribute(edge, "Motif", tree.getEnrichedMotifID());
						this.setAtribute(edge, "featureID", "" + tree.getFeatureID());
						CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
						cyEdgeAttrs.setUserVisible("featureID", false);
					}
				}	
			}
			IRegulonVisualStyle vsStyle = new IRegulonVisualStyle();
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			view.redrawGraph(true,true);
		}
	}
	
	/**
	 * 
	 * @param edge where a attribute must be added to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void addAtribute(CyEdge edge, String attributeName, String attributeValue){
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		List<Object> edgeAtr = new ArrayList<Object>();
		edgeAtr.add(cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), attributeName));
		boolean isIn = false;
		String atrString;
		for (Object atr : edgeAtr){
			atrString = (String) atr;
			if (atrString.equals(attributeValue)){
				isIn = true;
			}
		}
		if (! isIn){
			edgeAtr.add(attributeValue);
		}
		cyEdgeAttrs.setListAttribute(edge.getIdentifier(), attributeName, edgeAtr);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	/**
	 * 
	 * @param edge where a attribute must be added to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void addAtribute(CyEdge edge, String attributeName, List attributeValueList){
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		List<Object> edgeAtr = cyEdgeAttrs.getListAttribute(edge.getIdentifier(), attributeName);
		if (edgeAtr == null){
			edgeAtr = new ArrayList<Object>();
		}
		String atrString;
		for (Object attributeValue : attributeValueList){
			String attributeValueString = (String) attributeValue;
			for (Object atr : edgeAtr){
				atrString = (String) atr;
				if (atrString.equals(attributeValueString)){
					edgeAtr.add(attributeValue);
				}
			}
		}
		cyEdgeAttrs.setListAttribute(edge.getIdentifier(), attributeName, edgeAtr);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	/**
	 * 
	 * @param edge where a attribute must be set to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void setAtribute(CyEdge edge, String attributeName, String attributeValue){
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		//System.out.println(cyEdgeAttrs.getType(attributeName));
		cyEdgeAttrs.setAttribute(edge.getIdentifier(), attributeName, attributeValue);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	/**
	 * 
	 * @param edge where a attribute must be set to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void setAtribute(CyEdge edge, String attributeName, List attributeValueList){
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		cyEdgeAttrs.setListAttribute(edge.getIdentifier(), attributeName, attributeValueList);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	/**
	 * 
	 * @param geneIdentifier the gene where the node is needed from
	 * @param nodes all the nodes where there must been shearched in
	 * @return the node that is gene
	 */
	public CyNode getCyNode(GeneIdentifier geneIdentifier, ArrayList<CyNode> nodes){
		CyNode node1 = null;
		for(CyNode node : nodes){
			if (node.getIdentifier().equals(geneIdentifier.getGeneName())){
				node1 = node;
			}
		}
		return node1;
	}
	
	
	
	/**
	 * 
	 * @param node1 the node that is the regulon
	 * @param node2 the node that is the targetgene
	 * @param motif the name of the motif
	 * @post the interaction of the given edge is "regulates " + motif
	 * @return the new created edge
	 */
	private CyEdge addEdge(CyNode node1, CyNode node2, String motif){
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		CyEdge edge0 = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, 
				"regulates " + motif, true);
		current_network.addEdge(edge0);
		//System.out.println("Edge drawn");
		//view eigenschappen veranderen
		CyNetworkView current_view = Cytoscape.getCurrentNetworkView();
		EdgeView edgeview = current_view.getEdgeView(edge0);
		current_view.addEdgeView(edge0.getRootGraphIndex());
		current_view.updateView();
		return edge0;
	}
	
	/**
	 * 
	 * @param node1 the node that is the regulon
	 * @param node2 the node that is the targetgene
	 * @param network the network where the edge must be added to
	 * @param view the view where the edge must be added to
	 * @param motif the name of the motif
	 * @post the interaction of the given edge is "regulates " + motif
	 * @return the new created edge
	 */
	static public CyEdge addEdge(CyNode node1, CyNode node2, CyNetwork network, CyNetworkView view, String motif){
		CyEdge edge0 = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, 
				"regulates " + motif, true);
		network.addEdge(edge0);
		//System.out.println("Edge drawn");
		
		//view eigenschappen veranderen
		EdgeView edgeview = view.getEdgeView(edge0);
		view.addEdgeView(edge0.getRootGraphIndex());
		view.updateView();
		return edge0;
	}
	
	
}
