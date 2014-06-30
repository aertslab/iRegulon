package infrastructure.tasks;


import infrastructure.NetworkUtilities;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;


public interface NetworkResult {
    static final NetworkResult CURRENT = new NetworkResult() {
        @Override
        public CyNetwork getNetwork() {
            return NetworkUtilities.getInstance().getCurrentNetwork();
        }

        @Override
        public CyNetworkView getView() {
            return NetworkUtilities.getInstance().getCurrentNetworkView();
        }
    };

    CyNetwork getNetwork();

    CyNetworkView getView();
}
