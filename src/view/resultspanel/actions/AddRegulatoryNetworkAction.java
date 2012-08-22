package view.resultspanel.actions;

import cytoscape.*;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;


import domainmodel.AbstractMotif;
import view.parametersform.IRegulonVisualStyle;
import view.resultspanel.Refreshable;
import view.resultspanel.TFComboBox;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domainmodel.CandidateTargetGene;
import domainmodel.TranscriptionFactor;

public class AddRegulatoryNetworkAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_draw_nodes_and_edges";

    private final TFComboBox selectedTranscriptionFactor;

	
	public AddRegulatoryNetworkAction(SelectedMotif selectedMotif, final TFComboBox selectedTranscriptionFactor, final Refreshable view) {
		super(NAME, selectedMotif, selectedTranscriptionFactor, view);
		if (selectedMotif == null) throw new IllegalArgumentException();
		setEnabled(false);
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		AbstractMotif tree = this.getSelectedMotif().getMotif();
		TranscriptionFactor tf = this.getTranscriptionFactor();
		CyNetwork network =  Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		Map<String, List<CyNode>> map = getNodeMap(this.getSelectedMotif().getAttributeName(), network.nodesList());
		if (network != null && view != null){
			List<CyNode> tfNodeList = map.get(this.getTranscriptionFactor().getGeneID());
			if (tfNodeList == null || tfNodeList.isEmpty()){
				tfNodeList = new ArrayList<CyNode>();
				CyNode parent = addNode(tf.getName(), network, view);
				tfNodeList.add(parent);
				setAtribute(parent, "ID", tf.getName());
			}

			for (CyNode parent : tfNodeList){
				setAtribute(parent, this.getSelectedMotif().getAttributeName(), tf.getName());
				setAtribute(parent, "Regulatory function", "Regulator");
				addAtribute(parent, "Motif", tree.getName());
			}

			for (CandidateTargetGene geneID : tree.getCandidateTargetGenes()){
				List<CyNode> tgNodeList = map.get(geneID.getGeneName());
				if (tgNodeList == null || tgNodeList.isEmpty()){
					tgNodeList = new ArrayList<CyNode>();
					CyNode child = addNode(geneID.getGeneName(), network, view);
					tgNodeList.add(child);
					setAtribute(child, "ID", geneID.getGeneName());
				}
				for (CyNode tgNode : tgNodeList){
					setAtribute(tgNode, this.getSelectedMotif().getAttributeName(), geneID.getGeneName());

					for (CyNode tfNode : tfNodeList){
						this.drawEdgeAndAttributes(tfNode, tgNode, network, view, tree, geneID, tf);
					}
				}
			}
			//if the node is a regulator and target at the same time, it must say regulator
			for (CyNode parent : tfNodeList){
				setAtribute(parent, "Regulatory function", "Regulator");
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
            getView().refresh();
		}
	}
	
	private void drawEdgeAndAttributes(CyNode parent, CyNode target, 
						CyNetwork network, CyNetworkView view, AbstractMotif motif,
						CandidateTargetGene tg, TranscriptionFactor tf){
		CyEdge edge;
		edge = AddRegulatoryInteractionsAction.addEdge(parent, target, network, view, motif.getName());
		AddRegulatoryInteractionsAction.setAtribute(edge, "Regulator Gene", tf.getName());
		AddRegulatoryInteractionsAction.setAtribute(edge, "Target Gene", tg.getGeneName());
		AddRegulatoryInteractionsAction.setAtribute(edge, "Regulatory function", "Predicted");
		AddRegulatoryInteractionsAction.setAtribute(edge, "Motif", motif.getName());
		AddRegulatoryInteractionsAction.setAtribute(edge, "featureID", "" + motif.getDatabaseID());
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		cyEdgeAttrs.setUserVisible("featureID", false);
	}

    public Map<String, List<CyNode>> getNodeMap(String attributeName, List<CyNode> nodes){
			Map<String, List<CyNode>> map = new HashMap<String, List<CyNode>>();
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			for (CyNode node : nodes){
				String attr = (String) cyNodeAttrs.getAttribute(node.getIdentifier(), attributeName);
				if (attr != null){
					if (map.containsKey(attr)){
						map.get(attr).add(node);
					}else{
						List<CyNode> list = new ArrayList<CyNode>();
						list.add(node);
						map.put(attr, list);
					}
				}
			}
			return map;
		}

    		/**
		 *
		 * @param node where a attribute must be set to
		 * @param attributeName the name of the attribute that must be added
		 * @param AttributeValue the value that that attribute has for this node
		 */
		static public void setAtribute(CyNode node, String attributeName, String attributeValue){
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			cyNodeAttrs.setAttribute(node.getIdentifier(), attributeName, attributeValue);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		}/**
		 *
		 * @param nodeID the name for the node
		 * @param network the network where the edge must be added to
		 * @param view the view where the edge must be added to
		 * @return the new created node
		 */
		static public CyNode addNode(String nodeID, CyNetwork network, CyNetworkView view){
			CyNode node = Cytoscape.getCyNode(nodeID, true);
			network.addNode(node);
			//System.out.println("Node drawn");

			//view eigenschappen veranderen
			view.addNodeView(node.getRootGraphIndex());
			view.updateView();
			return node;
		}


		/**
		 *
		 * @param node where a attribute must be added to
		 * @param attributeName the name of the attribute that must be added
		 * @param AttributeValue the value that that attribute has for this node
		 */
		static public void addAtribute(CyNode node, String attributeName, String attributeValue){
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			List<Object> nodeAtr = cyNodeAttrs.getListAttribute(node.getIdentifier(), attributeName);
			if (nodeAtr == null){
				nodeAtr = new ArrayList<Object>();
			}
			boolean isIn = false;
			String atrString;
			for (Object atr : nodeAtr){
				atrString = (String) atr;
				if (atrString.equals(attributeValue)){
					isIn = true;
				}
			}
			if (! isIn){
				nodeAtr.add(attributeValue);
			}
			nodeAtr.add(attributeValue);
			cyNodeAttrs.setListAttribute(node.getIdentifier(), attributeName, nodeAtr);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		}
}
