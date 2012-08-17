package view.resultspanel.actions;

import view.parametersform.IRegulonVisualStyle;
import view.resultspanel.TFComboBox;
import view.resultspanel.TranscriptionFactorDependentAction;
import view.resultspanel.SelectedMotif;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;


public class CreateNewNetworkAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_create_new_network";
	
	public CreateNewNetworkAction(SelectedMotif selectedRegulatoryTree,
                                              final TFComboBox selectedTranscriptionFactor){
		super(NAME, selectedRegulatoryTree, selectedTranscriptionFactor);
		if (selectedRegulatoryTree == null)	throw new IllegalArgumentException();
		setEnabled(false);
	}
	
	public CyNetwork createNetwork(Motif mtf, TranscriptionFactor tf){
        return Cytoscape.createNetwork(tf.getName() +
                " with motif " + mtf.getEnrichedMotifID());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Motif tree = this.getSelectedMotif().getMotif();
		TranscriptionFactor tf = this.getTranscriptionFactor();
		CyNetwork network = this.createNetwork(tree, tf);
		CyNetworkView view = Cytoscape.createNetworkView(network, "MyNetwork");
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
			DrawEdgesAction.setAtribute(edge, "featureID", "" + tree.getFeatureID());
			CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
			cyEdgeAttrs.setUserVisible("featureID", false);
		}
		//if the node is a regulator and target at the same time, it must say regulator
		DrawNodesAction.setAtribute(nodeParent, "Regulatory function", "Regulator");
		DrawNodesAction.addAtribute(nodeParent, "Motif", tree.getEnrichedMotifID());
		/*final CyLayoutAlgorithm hierarchicalLayout = CyLayouts.getLayout(HIERARCHICAL_LAYOUT);
		if (hierarchicalLayout != null){
			System.out.println("Hierarchical");
		    view.applyLayout(hierarchicalLayout);
		}else{
			System.out.println("default");
			view.applyLayout(CyLayouts.getDefaultLayout());
		}*/
		//this.addAtribute(network, "Enriched Motif", tree.getEnrichedMotifID());
		IRegulonVisualStyle vsStyle = new IRegulonVisualStyle();
		view.applyLayout(CyLayouts.getDefaultLayout());
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		VisualStyle vs = catalog.getVisualStyle(getBundle().getString("vizmap_name"));
		if (vs != null){
			manager.setVisualStyle(vs);
		}
		view.redrawGraph(true,true);
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel (SwingConstants.WEST);
		if (cytoPanel.indexOfComponent(getBundle().getString("plugin_visual_name")) == -1){
			cytoPanel.setSelectedIndex(0);
		}else{
			cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(getBundle().getString("plugin_visual_name")));
		}
	}
	
	/**
	 * 
	 * @param network where a attribute must be added to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void addAtribute(CyNetwork network, String attributeName, String attributeValue){
		CyAttributes cyNetworkAttrs = Cytoscape.getEdgeAttributes();
		List<Object> networkAtr = cyNetworkAttrs.getListAttribute(network.getIdentifier(), attributeName);
		if (networkAtr == null){
			networkAtr = new ArrayList<Object>();
		}
		networkAtr.add(attributeValue);
		cyNetworkAttrs.setListAttribute(network.getIdentifier(), attributeName, networkAtr);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	
	/**
	 * 
	 * @param network where a attribute must be added to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void addAtribute(CyNetwork network, String attributeName, List attributeValueList){
		CyAttributes cyNetworkAttrs = Cytoscape.getNetworkAttributes();
		List<Object> networkAtr = cyNetworkAttrs.getListAttribute(network.getIdentifier(), attributeName);
		if (networkAtr == null){
			networkAtr = new ArrayList<Object>();
		}
		networkAtr.addAll(attributeValueList);
		cyNetworkAttrs.setListAttribute(network.getIdentifier(), attributeName, networkAtr);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	
	/**
	 * 
	 * @param network where a attribute must be set to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void setAtribute(CyNetwork network, String attributeName, String attributeValue){
		CyAttributes cyNetworkAttrs = Cytoscape.getEdgeAttributes();
		cyNetworkAttrs.setAttribute(network.getIdentifier(), attributeName, attributeValue);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	
	/**
	 * 
	 * @param network where a attribute must be set to
	 * @param attributeName the name of the attribute that must be added
	 * @param AttributeValue the value that that attribute has for this edge
	 */
	static public void setAtribute(CyNetwork network, String attributeName, List attributeValueList){
		CyAttributes cyNetworkAttrs = Cytoscape.getNetworkAttributes();
		cyNetworkAttrs.setListAttribute(network.getIdentifier(), attributeName, attributeValueList);
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
}
