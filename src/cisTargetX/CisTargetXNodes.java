package cisTargetX;

import java.awt.*;
import javax.swing.*;

import com.lowagie.text.List;

import java.util.*;
//import com.lowagie.text.List;



import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.generated.*;
import cytoscape.*;
import domainModel.GeneIdentifier;
import domainModel.SpeciesNomenclature;
import giny.model.*;
import giny.model.Node;



public class CisTargetXNodes {

	
	
	public CisTargetXNodes(){
		
		
	}
	
	/**
	 * Get all the selected Nodes (as CyNode)
	 * @pre this.nodesSelected() == true
	 * @return an array filled with the selected CyNodes
	 */
	static public ArrayList<CyNode> getSelectedNodes(){
		CyNetwork current_network = Cytoscape.getCurrentNetwork();
		Set selectedNodes = current_network.getSelectedNodes();	
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
		Set selectedNodes = current_network.getSelectedNodes();	
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
		Iterator it = current_network.nodesIterator();
		ArrayList<CyNode> cynodes = new ArrayList<CyNode>();
		while(it.hasNext()){
			cynodes.add((CyNode) it.next());
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
		if (CisTargetXNodes.nodesSelected()){
			ArrayList<CyNode> nodes = CisTargetXNodes.getSelectedNodes();
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
		return null;
	}
	
	
	
	
	
	
	
	
	

	
	
	
	
}

