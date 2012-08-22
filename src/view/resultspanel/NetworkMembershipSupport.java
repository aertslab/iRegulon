package view.resultspanel;

import view.CytoscapeNetworkUtilities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;


public class NetworkMembershipSupport {
	private CyNetwork currentNetwork;
    private Set<String> currentIDs = Collections.emptySet();
	
	public NetworkMembershipSupport() {
		refresh();
	}

    public CyNetwork getCurrentNetwork() {
        return currentNetwork;
    }

    public boolean refresh() {
        if (Cytoscape.getCurrentNetwork().equals(this.currentNetwork)) {
            return false;
        } else {
            this.currentNetwork = Cytoscape.getCurrentNetwork();
            return true;
        }
    }
	
	public Set<String> getCurrentIDs() {
        if (refresh()) this.currentIDs = retrieveIDs();
        return currentIDs;
    }

    private Set<String> retrieveIDs() {
        final Set<String> IDs = new HashSet<String>();

        final CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
        for (CyNode node : CytoscapeNetworkUtilities.getAllNodes()) {
            for (String attributeName : cyNodeAttrs.getAttributeNames()) {
                if (cyNodeAttrs.getType(attributeName) == CyAttributes.TYPE_STRING) {
                    final String possibleGeneName = cyNodeAttrs.getStringAttribute(node.getIdentifier(), attributeName);
                    if (possibleGeneName != null) {
                        IDs.add(possibleGeneName);
                    }
                }
            }
        }

        return IDs;
    }
}
