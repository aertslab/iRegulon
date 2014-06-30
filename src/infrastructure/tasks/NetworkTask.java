package infrastructure.tasks;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public abstract class NetworkTask extends AbstractTask {
    protected final NetworkResult network;
    protected final String attributeName;
    protected final boolean createNodesIfNecessary;

    public NetworkTask(final NetworkResult network, boolean createNodesIfNecessary, String attributeName) {
        this.network = network;
        this.createNodesIfNecessary = createNodesIfNecessary;
        this.attributeName = attributeName;
    }

    public CyNetwork getNetwork() {
        return network.getNetwork();
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isCreateNodesIfNecessary() {
        return createNodesIfNecessary;
    }

    @Override
    public void run(TaskMonitor taskMonitor) {
        //Nop ...
    }
}
