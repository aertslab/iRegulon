package networkDrawActions;

import giny.view.NodeView;

import iRegulonInput.NodesActions;
import iRegulonOutput.ComboboxAction;
import iRegulonOutput.SelectedMotif;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class DrawMergedEdgesNetworkAction extends ComboboxAction implements ListSelectionListener{

private static final String HIERARCHICAL_LAYOUT = "hierarchical";
	
	public DrawMergedEdgesNetworkAction(SelectedMotif selectedRegulatoryTree){
		super(selectedRegulatoryTree);
		if (selectedRegulatoryTree == null){
			throw new IllegalArgumentException();
		}
		//putValue(Action.NAME, "NetworkCreate");
		//putValue(Action.NAME, getBundle().getString("action_draw_edges_name"));
		putValue(Action.NAME, getBundle().getString("action_draw_merged_edges_network_name"));
		putValue(Action.SHORT_DESCRIPTION, getBundle().getString("action_draw_merged_edges_network_name"));
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
			motifs.addAll(motif);
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
		// get all the information of the old network
		CyNetwork oldNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView oldView = Cytoscape.getCurrentNetworkView();
		
		//Create a new network
		//CyNetwork network = Cytoscape.createNetwork(CisTargetXNodes.getAllNodes(), this.getAllEdges(), "test");
		CyNetwork network2 = Cytoscape.createNetwork(NodesActions.getAllNodes(), this.getAllEdges(), "Merged iRegulon network", Cytoscape.getCurrentNetwork());
		Cytoscape.setCurrentNetwork(network2.getIdentifier());
		CyNetworkView cyView = Cytoscape.createNetworkView(network2, "merged iRegulon network view");
		
		//copy the place of all nodes on the old network to the new network
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
		/*
		 * run over all edges
		 */
		it = network2.edgesIterator();
		//get the edge attributes
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		//hashmap, first argument the transcription factor, the second all the motifs
		HashMap<String, NewEdgeAttr> edgesAttr = new HashMap<String, DrawMergedEdgesNetworkAction.NewEdgeAttr>();
		//list of all unmapped edges
		ArrayList<CyEdge> unmappedEdges = new ArrayList<CyEdge>();
		//iterate over the edges
		while(it.hasNext()){
			CyEdge edge = (CyEdge) it.next();
			//check if edge is a regulates interaction, from a valid regulator gene to a target gene
			if (cyEdgeAttrs.getAttribute(edge.getIdentifier(), "interaction").toString().contains("regulates")
					&& cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), "Regulator Gene") != null
					&& cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), "Target Gene") != null){
				//take the atributes
				String TF = (String) cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), "Regulator Gene");
				String TG = (String) cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), "Target Gene");
				List<String> motifs = new ArrayList<String>();
				motifs.add(cyEdgeAttrs.getStringAttribute(edge.getIdentifier(), "Motif"));
				if (motifs.size() == 0){
					motifs = (List<String>) cyEdgeAttrs.getListAttribute(edge.getIdentifier(), "Motifs");
				}
				String name = TF + " regulates " + TG;
				if (edgesAttr.containsKey(name)){
					//if there is already an interaction between the 2
					NewEdgeAttr edgeattr = edgesAttr.get(name);
					// add the motifs to the interaction
					edgeattr.addMotif(motifs);
				}else{
					//create a new interaction between the 2
					NewEdgeAttr edgeattr = new NewEdgeAttr(TF, TG, motifs);
					edgesAttr.put(name, edgeattr);
				}
				network2.removeEdge(edge.getRootGraphIndex(), true);
			}else{
				//unmapped edge
				unmappedEdges.add(edge);
			}
		}
		/*
		 * draw all edges
		 */
		for (String key : edgesAttr.keySet()){
			//every new edge
			NewEdgeAttr edgeattr = edgesAttr.get(key);
			CyNode node1 = null;
			CyNode node2 = null;
			it = network2.nodesIterator();
			while(it.hasNext()){
				//get the nodes for the TF and the TG
				CyNode node = (CyNode) it.next();
				if (node.getIdentifier().equals(edgeattr.getTF())){
					node1 = node;
				}
				if (node.getIdentifier().equals(edgeattr.getTG())){
					node2 = node;
				}
			}
			//if the nodes are matched, the edge can been drawn
			if (node1 != null && node2 != null){
				CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, 
					"regulates", true);
				network2.addEdge(edge);
				//System.out.println("Edge identifier=" + edge.getIdentifier());
				//System.out.println("TF=" + edgeattr.getTF());
				DrawEdgesAction.setAtribute(edge, "Regulator Gene", edgeattr.getTF());
				//System.out.println("TG=" + edgeattr.getTG());
				DrawEdgesAction.setAtribute(edge, "Target Gene", edgeattr.getTG());
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


