package view.resultspanel.actions;

import domainmodel.AbstractMotif;
import view.parametersform.IRegulonVisualStyle;
import view.resultspanel.Refreshable;
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
import domainmodel.TranscriptionFactor;


public class CreateNewRegulatoryNetworkAction extends TranscriptionFactorDependentAction {
    private static final String NAME = "action_create_new_network";
	
	public CreateNewRegulatoryNetworkAction(SelectedMotif selectedRegulatoryTree,
                                            final TFComboBox selectedTranscriptionFactor,
                                            final Refreshable view){
		super(NAME, selectedRegulatoryTree, selectedTranscriptionFactor,view);
		if (selectedRegulatoryTree == null)	throw new IllegalArgumentException();
		setEnabled(false);
	}
	
	public CyNetwork createNetwork(AbstractMotif mtf, TranscriptionFactor tf){
        return Cytoscape.createNetwork(tf.getName() +
                " with motif " + mtf.getName());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AbstractMotif tree = this.getSelectedMotif().getMotif();
		TranscriptionFactor tf = this.getTranscriptionFactor();
		CyNetwork network = this.createNetwork(tree, tf);
		CyNetworkView view = Cytoscape.createNetworkView(network, "MyNetwork");
		CyNode nodeParent = addNode(tf.getName(), network, view);
		setAtribute(nodeParent, "ID", tf.getName());
		setAtribute(nodeParent, "Regulatory function", "Regulator");
		//System.out.println("Draw Parent Node");
		//System.out.println("Children " + tree.getCandidateTargetGenes().size());
		for (CandidateTargetGene geneID : tree.getCandidateTargetGenes()){
			CyNode nodeChild = addNode(geneID.getGeneName(), network, view);
			setAtribute(nodeChild, "ID", tf.getName());
			//DrawNodesAction.addAtribute(nodeChild, "Regulatory function", "Target Gene");
			CyEdge edge;
			edge = AddRegulatoryInteractionsAction.addEdge(nodeParent, nodeChild, network, view, tree.getName());
			AddRegulatoryInteractionsAction.setAtribute(edge, "Regulator Gene", tf.getName());
			AddRegulatoryInteractionsAction.setAtribute(edge, "Target Gene", geneID.getGeneName());
			AddRegulatoryInteractionsAction.setAtribute(edge, "Regulatory function", "Predicted");
			AddRegulatoryInteractionsAction.setAtribute(edge, "Motif", tree.getName());
			AddRegulatoryInteractionsAction.setAtribute(edge, "featureID", "" + tree.getDatabaseID());
			CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
			cyEdgeAttrs.setUserVisible("featureID", false);
		}
		//if the node is a regulator and target at the same time, it must say regulator
		setAtribute(nodeParent, "Regulatory function", "Regulator");
		addAtribute(nodeParent, "Motif", tree.getName());
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
        getView().refresh();
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
		}
    /**
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
}
