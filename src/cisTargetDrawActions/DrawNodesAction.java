package cisTargetDrawActions;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cisTargetExeptions.CreationException;
import cisTargetOutput.ComboboxAction;
import cisTargetOutput.SelectedRegulatoryTree;
import cisTargetX.CisTargetXNodes;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import domainModel.CandidateTargetGene;
import domainModel.GeneIdentifier;
import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class DrawNodesAction extends ComboboxAction implements ListSelectionListener{
		
		public DrawNodesAction(SelectedRegulatoryTree selectedRegulatoryTree) throws CreationException{
			super(selectedRegulatoryTree);
			if (selectedRegulatoryTree == null){
				throw new CreationException("Couldn't create DrawNodes");
			}
			putValue(Action.NAME, getBundle().getString("action_draw_nodes_name"));
			putValue(Action.SHORT_DESCRIPTION, getBundle().getString("action_draw_nodes_name"));
			setEnabled(false);
		}
		
		public void valueChanged(ListSelectionEvent e) {
			final ListSelectionModel model = (ListSelectionModel) e.getSource();
			setEnabled(! model.isSelectionEmpty());
		}
		
		/**
		 * 
		 * @return a list of all selected Transcription factor regulons
		 */
		public Motif getListSelectedRegulatoryTree(){
			return this.getSelectedRegulatoryTree().getMotifs();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String NodeID = "New Node";
			CyNetwork current_network = Cytoscape.getCurrentNetwork();
			CyNode node0 = Cytoscape.getCyNode(NodeID, true);
			current_network.addNode(node0);
			
			//System.out.println(this.getSelectedRegulatoryTree());
			Motif regulatoryTree = this.getListSelectedRegulatoryTree();
			TranscriptionFactor tf = this.getTranscriptionFactor();
			CyNode node1 = null;
			CyNode node2 = null;
			ArrayList<CyNode> nodes = CisTargetXNodes.getAllNodes();
			//System.out.println(tf.getName());
			node1 = this.getCyNode(tf.getName(), nodes);
			if (node1 == null){
				node1 = this.addNode(tf.getName());
			}
			for (CandidateTargetGene link : regulatoryTree.getCandidateTargetGenes()){
				node2 = this.getCyNode(link.getGeneName(), nodes);
				if (node2 == null){
					node2 = this.addNode(link.getGeneName());
				}
			}
		}
		
		
		/**
		 * 
		 * @param geneIdentifier the gene where the node is needed from
		 * @param nodes all the nodes where there must been shearched in
		 * @return the node that is gene
		 */
		public CyNode getCyNode(String geneName, ArrayList<CyNode> nodes){
			CyNode node1 = null;
			for(CyNode node : nodes){
				if (node.getIdentifier().equals(geneName)){
					node1 = node;
				}
			}
			return node1;
		}
		
		/**
		 * 
		 * @param geneIdentifier
		 * @param nodes
		 * @return true if there exists already a note that would have the same geneName
		 */
		public boolean existsNode(GeneIdentifier geneIdentifier, ArrayList<CyNode> nodes){
			return this.getCyNode(geneIdentifier.getGeneName(), nodes) != null;
		}
		
		
		/**
		 * 
		 * @param nodeID the name for the node
		 * @return the new created node
		 */
		private CyNode addNode(String nodeID){
			CyNetwork current_network = Cytoscape.getCurrentNetwork();
			CyNode node = Cytoscape.getCyNode(nodeID, true);
			current_network.addNode(node);
			//System.out.println("Node drawn");
			
			//view eigenschappen veranderen
			CyNetworkView current_view = Cytoscape.getCurrentNetworkView();
			NodeView nodeview = current_view.getNodeView(node);
			current_view.addNodeView(node.getRootGraphIndex());
			current_view.updateView();
			return node;
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
		 * @param node where a attribute must be added to
		 * @param attributeName the name of the attribute that must be added
		 * @param AttributeValue the value that that attribute has for this node
		 */
		static public void addAtribute(CyNode node, String attributeName, List attributeValueList){
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			List<Object> nodeAtr = cyNodeAttrs.getListAttribute(node.getIdentifier(), attributeName);
			if (nodeAtr == null){
				nodeAtr = new ArrayList<Object>();
			}
			String atrString;
			for (Object attributeValue : attributeValueList){
				String attributeValueString = (String) attributeValue;
				for (Object atr : nodeAtr){
					atrString = (String) atr;
					if (atrString.equals(attributeValueString)){
						nodeAtr.add(attributeValueString);
					}
				}
			}
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
		 * @param node where a attribute must be set to
		 * @param attributeName the name of the attribute that must be added
		 * @param AttributeValue the value that that attribute has for this node
		 */
		static public void setAtribute(CyNode node, String attributeName, List attributeValueList){
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			cyNodeAttrs.setListAttribute(node.getIdentifier(), attributeName, attributeValueList);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		}
		
}
