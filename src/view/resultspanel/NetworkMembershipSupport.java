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
	private static String CURRENT_NETWORK_ID = null;
    private static Set<String> CURRENT_IDS = Collections.emptySet();
	
	public NetworkMembershipSupport() {
		isRefreshNecessary();
	}

	public Set<String> getCurrentIDs() {
        if (isRefreshNecessary()) {
            CURRENT_IDS = retrieveIDs();
            CURRENT_NETWORK_ID = Cytoscape.getCurrentNetwork().getIdentifier();
        }
        return CURRENT_IDS;
    }

    private static boolean isRefreshNecessary() {
        return !Cytoscape.getCurrentNetwork().getIdentifier().equals(CURRENT_NETWORK_ID);
    }

    private static Set<String> retrieveIDs() {
        final Set<String> IDs = new HashSet<String>();

        final CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
        //TODO: this is wrong ..
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
