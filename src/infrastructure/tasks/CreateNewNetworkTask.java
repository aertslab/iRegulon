package infrastructure.tasks;


import infrastructure.NetworkUtilities;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public final class CreateNewNetworkTask extends AbstractTask implements NetworkResult {
    private final String title;

    private CyNetwork network;
    private CyNetworkView view;

    public CreateNewNetworkTask(String title) {
        this.title = title;
    }

    @Override
    public CyNetwork getNetwork() {
        return network;
    }

    @Override
    public CyNetworkView getView() {
        return view;
    }

    private void setNetwork(CyNetwork network) {
        this.network = network;
    }

    private void setView(CyNetworkView view) {
        this.view = view;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setStatusMessage("Create new network");

        final CyNetwork network = NetworkUtilities.getInstance().createNetwork(title);
        setNetwork(network);

        setView(NetworkUtilities.getInstance().createNetworkView(network));

        NetworkUtilities.getInstance().setCurrentNetwork(network);
    }
}
