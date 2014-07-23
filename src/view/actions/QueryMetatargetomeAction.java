package view.actions;

import domainmodel.*;
import infrastructure.CytoscapeEnvironment;
import infrastructure.NetworkUtilities;
import infrastructure.tasks.*;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.swing.DialogTaskManager;
import servercommunication.MetaTargetomes;
import servercommunication.tasks.QueryMetaTargetomeTask;
import view.Refreshable;
import view.ResourceAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class QueryMetatargetomeAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_query_metatargetome";

    public static final MetaTargetomeParameters DEFAULT_PARAMETERS = new MetaTargetomeParameters() {
        @Override
        public String getAttributeName() {
            return NetworkUtilities.ID_ATTRIBUTE_NAME;
        }

        @Override
        public GeneIdentifier getTranscriptionFactor() {
            return getSelectedFactor();
        }

        @Override
        public List<TargetomeDatabase> getTargetomeDatabases() {
            return TargetomeDatabase.getAllTargetomeDatabases();
        }

        @Override
        public int getOccurrenceCountThreshold() {
            return MetaTargetomeParameters.DEFAULT_THRESHOLD;
        }

        @Override
        public int getMaxNumberOfNodes() {
            return MetaTargetomeParameters.DEFAULT_MAX_NODE_COUNT;
        }

        @Override
        public boolean createNewNetwork() {
            return true;
        }

        private GeneIdentifier getSelectedFactor() {
            final CyNetwork network = NetworkUtilities.getInstance().getCurrentNetwork();
            if (network == null) return null;
            final List<GeneIdentifier> ids = NetworkUtilities.getInstance().getSelectedNodesAsGeneIDs(
                    network, getAttributeName(), SpeciesNomenclature.HOMO_SAPIENS_HGNC);
            if (ids == null || ids.isEmpty()) return null;
            else return ids.get(0);
        }
    };

    private MetaTargetomeParameters parameters;
    private final Refreshable resultsPanel;

    public QueryMetatargetomeAction(final MetaTargetomeParameters parameters, final Refreshable view) {
        super(NAME);
        this.parameters = parameters;
        this.resultsPanel = view;
        refresh();
    }

    public QueryMetatargetomeAction(final Refreshable view) {
        this(null, view);
    }

    public MetaTargetomeParameters getParameters() {
        return parameters;
    }

    public void setParameters(final MetaTargetomeParameters parameters) {
        this.parameters = parameters;
        refresh();
    }

    public Refreshable getResultsPanel() {
        return resultsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (!MetaTargetomes.hasAvailableFactors()) {
            JOptionPane.showMessageDialog(CytoscapeEnvironment.getInstance().getJFrame(),
                    "Problem while communicating with server.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final MetaTargetomeParameters parameters = new DefaultMetaTargetomeParameters(getParameters());

        final TaskIterator tasks = new TaskIterator();

        tasks.append(new AbstractTask() {
            @Override
            public void run(TaskMonitor taskMonitor) throws Exception {
                taskMonitor.setTitle(createTitle(parameters));
            }
        });

        final NetworkResult network;
        final boolean useCurrentNetwork = useCurrentNetwork();
        final boolean oldNetworkHasEdges;

        if (! useCurrentNetwork) {
            final CreateNewNetworkTask createNetworkTask = new CreateNewNetworkTask(createTitle(parameters));
            tasks.append(createNetworkTask);
            network = createNetworkTask;
            oldNetworkHasEdges = false;
        } else {
            network = NetworkResult.CURRENT;
            oldNetworkHasEdges = network.getNetwork().getEdgeCount() > 0 ? true : false;
        }

        final QueryMetaTargetomeTask queryMetaTargetomeTask = new QueryMetaTargetomeTask(parameters);
        tasks.append(queryMetaTargetomeTask);

        final AddMetatargetomeNetworkTask addMetaTargetomeNetworkTask = new AddMetatargetomeNetworkTask(
                network,
                true,
                parameters.getAttributeName(),
                parameters.getTranscriptionFactor(),
                queryMetaTargetomeTask);
        tasks.append(addMetaTargetomeNetworkTask);

        if (! oldNetworkHasEdges) {
            /* Apply layout when the old network didn't have any edges yet (or when we have created a new network). */
            tasks.append(new ApplyDefaultLayoutTask(network));
        }

        tasks.append(new FinalizeTask(network, resultsPanel));

        final TaskManager taskManager = CytoscapeEnvironment.getInstance().getServiceRegistrar().getService(DialogTaskManager.class);
        taskManager.execute(tasks);
    }

    private boolean useCurrentNetwork() {
        return !getParameters().createNewNetwork()
                && NetworkUtilities.getInstance().getCurrentNetworkView() != null
                && NetworkUtilities.getInstance().getCurrentNetwork() != null;
    }

    private String createTitle(final MetaTargetomeParameters parameters) {
        return "Metatargetome for " + parameters.getTranscriptionFactor().getGeneName();
    }

    @Override
    public void refresh() {
        setEnabled(checkEnabled());
    }

    private boolean checkEnabled() {
        if (getParameters() == null) return false;
        if (getParameters().getTargetomeDatabases().isEmpty()) return false;
        final GeneIdentifier factor = getParameters().getTranscriptionFactor();
        if (factor == null) return false;
        if (getParameters().getOccurrenceCountThreshold() < 0) return false;
        if (getParameters().getMaxNumberOfNodes() < 0) return false;
        return (MetaTargetomes.hasAvailableFactors(factor.getSpeciesNomenclature()) &&
                MetaTargetomes.getAvailableFactors(factor.getSpeciesNomenclature()).contains(factor));
    }
}
