package view.resultspanel;

import infrastructure.NetworkUtilities;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class NetworkMembershipSupport {
    private static Long CURRENT_NETWORK_ID = null;
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

    private static Long getCurrentNetworkID() {
        if (NetworkUtilities.getInstance().getCurrentNetwork() == null) {
            return null;
        } else {
            return NetworkUtilities.getInstance().getCurrentNetwork().getSUID();
        }
    }

    private static boolean isRefreshNecessary() {
        final Long curID = getCurrentNetworkID();
        return curID == null || !curID.equals(CURRENT_NETWORK_ID);
    }

    private static Set<String> retrieveIDs() {
        final Set<String> IDs = new HashSet<String>();

        final CyNetwork network = NetworkUtilities.getInstance().getCurrentNetwork();
        if (network == null) return IDs;
        final CyTable table = network.getDefaultNodeTable();
        for (CyNode node : network.getNodeList()) {
            for (CyColumn column : table.getColumns()) {
                if (column.getType().equals(String.class)) {
                    final String possibleGeneName = table.getRow(node.getSUID()).get(column.getName(), String.class);
                    if (possibleGeneName != null) {
                        IDs.add(possibleGeneName);
                    }
                }
            }
        }
        return IDs;
    }
}
