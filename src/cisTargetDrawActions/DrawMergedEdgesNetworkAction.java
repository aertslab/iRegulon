package cisTargetDrawActions;

import giny.model.GraphPerspective;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.View;

import cisTargetExeptions.CreationException;
import cisTargetOutput.ComboboxAction;
import cisTargetOutput.SelectedRegulatoryTree;
import cisTargetX.CisTargetVisualStyle;
import cisTargetX.CisTargetXNodes;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import domainModel.CandidateTargetGene;
import domainModel.GeneIdentifier;
import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class DrawMergedEdgesNetworkAction extends ComboboxAction implements ListSelectionListener{

private static final String HIERARCHICAL_LAYOUT = "hierarchical";
	
	public DrawMergedEdgesNetworkAction(SelectedRegulatoryTree selectedRegulatoryTree) throws CreationException{
		super(selectedRegulatoryTree);
		if (selectedRegulatoryTree == null){
			throw new CreationException("Couldn't create DrawMergedEdgesNetworkAction");
		}
		//putValue(Action.NAME, "NetworkCreate");
		//putValue(Action.NAME, getBundle().getString("action_draw_edges_name"));
		putValue(Action.NAME, getBundle().getString("action_create_new_network_name"));
		putValue(Action.SHORT_DESCRIPTION, getBundle().getString("action_create_new_network_name"));
		//String pathNetwork = "/icons/node-select.png";
		//java.net.URL imgURLNetwork = getClass().getResource(pathNetwork);
		//ImageIcon iconNetwork = new ImageIcon(imgURLNetwork, "Draw new network");
		//putValue(Action.LARGE_ICON_KEY, iconNetwork);
		//putValue(Action.SMALL_ICON, iconNetwork);
		setEnabled(false);
	}
	
	
	
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		setEnabled(! model.isSelectionEmpty());
	}
	
	public CyNetwork createNetwork(Motif mtf, TranscriptionFactor tf){
		CyNetwork network = Cytoscape.createNetwork(tf.getName() + 
				" with motif " + mtf.getEnrichedMotifID());
		return network;
	}
	
	public CyNetwork createNetwork(){
		CyNetwork network = Cytoscape.createNetwork("New Network");	
		return network;
	}

	

	//inner class data edge
	private class NewEdgeAttr{
			
		private String TF;
		private String TG;
		private HashSet<String> motifs;
		
		public NewEdgeAttr(String TF, String TG, List<String> motif){
			this.TF = TF;
			this.TG = TG;
			this.motifs = new HashSet<String>();
			this.motifs.addAll(motif);
		}
		
		public void addMotif(List<String> motif){
			this.motifs.addAll(motifs);
		}
		
		public String getTF(){
			return this.TF;
		}
		
		public String getTG(){
			return this.TG;
		}
		
		public Collection<String> getMotifs(){
			return this.motifs;
		}
			
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		CyNetwork oldNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView oldView = Cytoscape.getCurrentNetworkView();
		
		
		//CyNetwork network = Cytoscape.createNetwork(CisTargetXNodes.getAllNodes(), this.getAllEdges(), "test");
		CyNetwork network2 = Cytoscape.createNetwork(CisTargetXNodes.getAllNodes(), this.getAllEdges(), "Merged iRegulon network", Cytoscape.getCurrentNetwork());
		Cytoscape.setCurrentNetwork(network2.getIdentifier());
		CyNetworkView cyView = Cytoscape.createNetworkView(network2, "merged iRegulon network view");
		
		
		Iterator it = oldNetwork.nodesIterator();
		while(it.hasNext()){
			CyNode node = (CyNode) it.next();
			NodeView nodev = oldView.getNodeView(node);
			if (nodev != null){
				double xpos = nodev.getXPosition();
				double ypos = nodev.getYPosition();
				NodeView newnodev = cyView.getNodeView(node);
				newnodev.setXPosition(xpos);
				newnodev.setYPosition(ypos);
				network2.addNode(node);
			}
		}
		it = network2.edgesIterator();
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		HashMap<String, NewEdgeAttr> edgesAttr = new HashMap<String, DrawMergedEdgesNetworkAction.NewEdgeAttr>();
		while(it.hasNext()){
			CyEdge edge = (CyEdge) it.next();
			if (cyEdgeAttrs.getAttribute(edge.getIdentifier(), "interaction").toString().contains("regulates")){
				String TF = (String) cyEdgeAttrs.getListAttribute(edge.getIdentifier(), "Regulator Gene").get(0);
				String TG = (String) cyEdgeAttrs.getListAttribute(edge.getIdentifier(), "Target Gene").get(0);
				List<String> motifs = (List<String>) cyEdgeAttrs.getListAttribute(edge.getIdentifier(), "Motif");
				String name = TF + " regulates " + TG;
				if (edgesAttr.containsKey(name)){
					NewEdgeAttr edgeattr = edgesAttr.get(name);
					edgeattr.addMotif(motifs);
				}else{
					NewEdgeAttr edgeattr = new NewEdgeAttr(TF, TG, motifs);
					edgesAttr.put(name, edgeattr);
				}
				network2.removeEdge(edge.getRootGraphIndex(), true);
			}
		}
		for (String key : edgesAttr.keySet()){
			NewEdgeAttr edgeattr = edgesAttr.get(key);
			CyNode node1 = null;
			CyNode node2 = null;
			it = network2.nodesIterator();
			while(it.hasNext()){
				CyNode node = (CyNode) it.next();
				if (node.getIdentifier().equals(edgeattr.getTF())){
					node1 = node;
				}
				if (node.getIdentifier().equals(edgeattr.getTG())){
					node2 = node;
				}
			}
			if (node1 != null && node2 != null){
				CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, 
					"regulates", true);
				network2.addEdge(edge);
				System.out.println("Edge identifier=" + edge.getIdentifier());
				System.out.println("TF=" + edgeattr.getTF());
				DrawEdgesAction.setAtribute(edge, "Transcription_Factor", edgeattr.getTF());
				System.out.println("TG=" + edgeattr.getTG());
				DrawEdgesAction.setAtribute(edge, "Target_Gene", edgeattr.getTG());
				List<String> motifs = new ArrayList<String>();
				Iterator<String> motifit = edgeattr.getMotifs().iterator();
				while(motifit.hasNext()){
					motifs.add(motifit.next());
				}
				DrawEdgesAction.setAtribute(edge, "Motifs", motifs);
				cyView.addEdgeView(edge.getRootGraphIndex());
				cyView.updateView();
			}
			
		}
		cyView.updateView();
		
		
		
		
		/*
		Motif tree = this.getSelectedRegulatoryTree().getMotifs();
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
			DrawEdgesAction.addAtribute(edge, "Regulator Gene", tf.getName());
			DrawEdgesAction.addAtribute(edge, "Target Gene", geneID.getGeneName());
			DrawEdgesAction.addAtribute(edge, "Regulatory function", "Predicted");
			DrawEdgesAction.addAtribute(edge, "Motif", tree.getEnrichedMotifID());
		}
		//if the node is a regulator and target at the same time, it must say regulator
		DrawNodesAction.setAtribute(nodeParent, "Regulatory function", "Regulator");
		DrawNodesAction.addAtribute(nodeParent, "Motif", tree.getEnrichedMotifID());
		*/
		
		//
		/*final CyLayoutAlgorithm hierarchicalLayout = CyLayouts.getLayout(HIERARCHICAL_LAYOUT);
		if (hierarchicalLayout != null){
			System.out.println("Hierarchical");
		    view.applyLayout(hierarchicalLayout);
		}else{
			System.out.println("default");
			view.applyLayout(CyLayouts.getDefaultLayout());
		}*/
		//this.addAtribute(network, "Enriched Motif", tree.getEnrichedMotifID());
		
		/*
		CisTargetVisualStyle vsStyle = new CisTargetVisualStyle();
		view.applyLayout(CyLayouts.getDefaultLayout());
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		VisualStyle vs = catalog.getVisualStyle(ResourceBundle.getBundle("cistargetx").getString("vizmap_name"));
		if (vs != null){
			manager.setVisualStyle(vs);
		}
		view.redrawGraph(true,true);
		*/
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
	
	
	/**
	 * Get all the Nodes (as CyNode) in the network
	 * @pre this.nodesSelected() == true
	 * @return an arrayList filled with the selected CyNodes
	 */
	static public ArrayList<CyEdge> getAllEdges(){
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		Iterator it = current_network.edgesIterator();
		ArrayList<CyEdge> cyedges = new ArrayList<CyEdge>();
		while(it.hasNext()){
			cyedges.add((CyEdge) it.next());
		}
		return cyedges;
	}
	
	
	
	
}


