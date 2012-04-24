package iRegulonOutput.renderers;

import iRegulonInput.NodesActions;

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
	public Set getIDs() {
   	 final Set IDs = new HashSet();
        ArrayList<CyNode> nodes = NodesActions.getAllNodes();
        for (CyNode node : nodes){
       	 CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
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
