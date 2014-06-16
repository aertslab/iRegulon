package view.resultspanel;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import infrastructure.NetworkUtilities;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class NetworkMembershipSupport {
    private static String CURRENT_NETWORK_ID = null;
    private static Set<String> CURRENT_IDS = Collections.emptySet();

    public NetworkMembershipSupport() {
        isRefreshNecessary();
    }

    public Set<String> getCurrentIDs() {
        if (isRefreshNecessary()) {
            CURRENT_IDS = retrieveIDs();
            CURRENT_NETWORK_ID = getCurrentNetworkID();
        }
        return CURRENT_IDS;
    }

    private static String getCurrentNetworkID() {
        if (Cytoscape.getCurrentNetworkView() == null
                || Cytoscape.getCurrentNetworkView().getNetwork() == null)
            return null;
        else return Cytoscape.getCurrentNetworkView().getNetwork().getIdentifier();
    }

    private static boolean isRefreshNecessary() {
        final String curID = getCurrentNetworkID();
        return curID == null || !curID.equals(CURRENT_NETWORK_ID);
    }

    private static Set<String> retrieveIDs() {
        final Set<String> IDs = new HashSet<String>();

        final CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
        for (CyNode node : NetworkUtilities.getAllNodes()) {
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
