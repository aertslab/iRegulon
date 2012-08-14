package parameterform;


import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.*;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;

public class NodesActions {

	
	
	public NodesActions(){
		
		
	}
	
	/**
	 * Get all the selected Nodes (as CyNode)
	 * @pre this.nodesSelected() == true
	 * @return an array filled with the selected CyNodes
	 */
	static public ArrayList<CyNode> getSelectedNodes(){
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		@SuppressWarnings("unchecked")
		Set<CyNode> selectedNodes = current_network.getSelectedNodes();	
		//create array for storing the nodes
		ArrayList<CyNode> nodesList = new ArrayList<CyNode>(selectedNodes);
		//return the array
		return nodesList;
	}
	
	
	/**
	 * 
	 * @return true if there are nodes selected, else false
	 */
	static public boolean nodesSelected(){
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		@SuppressWarnings("unchecked")
		Set<CyNode> selectedNodes = current_network.getSelectedNodes();	
		boolean selected;
		if (selectedNodes.isEmpty()) {
			selected = false;
		}else{
			selected = true;
		}
		return selected;
	}
	
	/**
	 * Get all the Nodes (as CyNode) in the network
	 * @pre this.nodesSelected() == true
	 * @return an arrayList filled with the selected CyNodes
	 */
	static public ArrayList<CyNode> getAllNodes(){
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		@SuppressWarnings("unchecked")
		Iterator<CyNode> it = current_network.nodesIterator();
		ArrayList<CyNode> cynodes = new ArrayList<CyNode>();
		while(it.hasNext()){
			cynodes.add(it.next());
		}
		return cynodes;
	}
	
	/**
	 * @ore the attribute of the attributeName may not be a list, array or set
	 * @param attributeName
	 * @param spnc
	 * @return the nodes as a collection of GeneIdentifiers, 
	 * 	with the selected SpeciesNomenclature and the selected attributeName
	 */
	static public Collection<GeneIdentifier> getGenes(String attributeName, SpeciesNomenclature spnc){
		if (NodesActions.nodesSelected()){
			ArrayList<CyNode> nodes = NodesActions.getSelectedNodes();
			Collection<GeneIdentifier> genes = new ArrayList<GeneIdentifier>();
			for(CyNode node : nodes){
				CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
				String geneName = cyNodeAttrs.getStringAttribute(node.getIdentifier(), attributeName);
				if (geneName != null){
					GeneIdentifier gene = new GeneIdentifier(geneName, spnc);
					genes.add(gene);
				}
			}
			return genes;
		}
		Collection<GeneIdentifier> emptyGene = new ArrayList<GeneIdentifier>();
		return emptyGene;
	}
	
	
	
	
	
	
	
	
	

	
	
	
	
}

