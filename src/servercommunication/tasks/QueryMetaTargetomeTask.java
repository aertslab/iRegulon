package servercommunication.tasks;

import domainmodel.CandidateTargetGene;
import domainmodel.MetaTargetomeParameters;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceFactory;
import servercommunication.ServerCommunicationException;

import java.util.Collections;
import java.util.List;


public final class QueryMetaTargetomeTask extends AbstractTask implements TargetomeResult {
    private final MetaTargetomeParameters parameters;

    private List<CandidateTargetGene> targetome;

    public QueryMetaTargetomeTask(MetaTargetomeParameters parameters) {
        this.parameters = parameters;
        this.targetome = Collections.emptyList();
    }

    public List<CandidateTargetGene> getTargetome() {
        return targetome;
    }

    public void setTargetome(List<CandidateTargetGene> targetome) {
        this.targetome = targetome;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws ServerCommunicationException {
        taskMonitor.setStatusMessage("Query metatargetome ...");
        taskMonitor.setProgress(0.0);
        final ComputationalService service = ComputationalServiceFactory.getInstance().getService();
        setTargetome(service.queryPredictedTargetome(
                parameters.getTranscriptionFactor(),
                parameters.getTargetomeDatabases(),
                parameters.getOccurrenceCountThreshold(),
                parameters.getMaxNumberOfNodes()));
        taskMonitor.setProgress(1.0);
    }
}
