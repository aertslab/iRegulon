package infrastructure.tasks;


import infrastructure.NetworkUtilities;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public final class ApplyDefaultLayoutTask extends AbstractTask {
    private final NetworkResult network;

    public ApplyDefaultLayoutTask(NetworkResult network) {
        this.network = network;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setStatusMessage("Applying layout ...");
        taskMonitor.setProgress(0.0);
        insertTasksAfterCurrentTask(NetworkUtilities.getInstance().applyLayout(network.getView()));
        taskMonitor.setProgress(1.0);
    }
}
