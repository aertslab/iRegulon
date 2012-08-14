package view.resultspanel.renderers;

import view.parametersform.NodesActions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class UpDateHighLightRenderer {

	private CyNetwork network;
	
	
	public UpDateHighLightRenderer(){
		this.network = Cytoscape.getCurrentNetwork();
	}
	
	
	public boolean mustUpdate(){
		if (Cytoscape.getCurrentNetwork().equals(this.network)){
			return false;
		}else{
			this.network = Cytoscape.getCurrentNetwork();
			return true;
		}
	}
	
	
	/**
	 * 
	 * @return a set of the IDs that must be highlighted
	 */
	public Set<String> getIDs() {
		final Set<String> IDs = new HashSet<String>();
        ArrayList<CyNode> nodes = NodesActions.getAllNodes();
    	CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
        for (CyNode node : nodes){
        	for (String attributeName : cyNodeAttrs.getAttributeNames()){
        		if (cyNodeAttrs.getType(attributeName) == Cytoscape.getNodeAttributes().TYPE_STRING){
        			String possibleGeneName = cyNodeAttrs.getStringAttribute(node.getIdentifier(), attributeName);
        			if (possibleGeneName != null){
       			 		IDs.add(possibleGeneName);
       			 	}
       		 	}
        	}
        }
        return IDs;
    }
	
	
	
}
