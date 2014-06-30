package view.actions;


import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import domainmodel.*;
import infrastructure.CytoscapeEnvironment;
import infrastructure.NetworkUtilities;
import infrastructure.tasks.AddMetatargetomeTask;
import infrastructure.tasks.CreateMetatargetomeTask;
import infrastructure.tasks.MetatargetomeTask;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.MetaTargetomes;
import servercommunication.ServerCommunicationException;
import view.Refreshable;
import view.ResourceAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class QueryMetatargetomeAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_query_metatargetome";
    private static final int NODE_COUNT_LIMIT_FOR_TASK = 50;

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

        final ComputationalService service = new ComputationalServiceHTTP();
        final List<CandidateTargetGene> targetome;
        try {
            targetome = service.queryPredictedTargetome(
                    parameters.getTranscriptionFactor(),
                    parameters.getTargetomeDatabases(),
                    parameters.getOccurrenceCountThreshold(),
                    parameters.getMaxNumberOfNodes());
        } catch (ServerCommunicationException e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final CyNetworkView view;
        final MetatargetomeTask task;
        boolean useCurrentNetwork = !getParameters().createNewNetwork()
                && Cytoscape.getCurrentNetworkView() != null
                && Cytoscape.getCurrentNetworkView().getNetwork() != null
                && !Cytoscape.getCurrentNetworkView().getNetwork().equals(Cytoscape.getNullNetwork());
        if (useCurrentNetwork) {
            final CyNetwork network = Cytoscape.getCurrentNetwork();
            view = Cytoscape.getCurrentNetworkView();
            task = new AddMetatargetomeTask(network, view,
                    getResultsPanel(), parameters.getAttributeName(),
                    parameters.getTranscriptionFactor(), targetome);
        } else {
            final CyNetwork network = Cytoscape.createNetwork(createTitle(parameters));
            view = Cytoscape.createNetworkView(network, createTitle(parameters));
            task = new CreateMetatargetomeTask(network, view,
                    getResultsPanel(), parameters.getAttributeName(),
                    parameters.getTranscriptionFactor(), targetome);
        }

        if (targetome.size() < NODE_COUNT_LIMIT_FOR_TASK) {
            task.run();
        } else {
            final JTaskConfig taskConfig = new JTaskConfig();
            taskConfig.setOwner(Cytoscape.getDesktop());
            taskConfig.displayCloseButton(true);
            taskConfig.displayCancelButton(true);
            taskConfig.displayStatus(true);
            taskConfig.setAutoDispose(true);
            TaskManager.executeTask(task, taskConfig);
        }
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
