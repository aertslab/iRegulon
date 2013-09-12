package view.actions;


import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TargetomeDatabase;
import infrastructure.CytoscapeNetworkUtilities;
import infrastructure.Logger;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.ResourceAction;
import view.parametersform.DefaultMetatargetomeParameters;
import view.parametersform.MetatargetomeParameters;
import view.resultspanel.Refreshable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;


public class QueryMetatargetomeAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_query_metatargetome";
    private static final int NODE_COUNT_LIMIT_FOR_TASK = 50;

    public static final int DEFAULT_THRESHOLD;
    public static final int DEFAULT_MAX_NODE_COUNT;

    static {
        DEFAULT_THRESHOLD = Integer.parseInt(ResourceBundle.getBundle("iRegulon").getString("occurrence_count_threshold"));
        DEFAULT_MAX_NODE_COUNT = Integer.parseInt(ResourceBundle.getBundle("iRegulon").getString("max_node_number"));
    }

    public static final MetatargetomeParameters DEFAULT_PARAMETERS = new MetatargetomeParameters() {
        @Override
        public String getAttributeName() {
            return null;
        }

        @Override
        public GeneIdentifier getTranscriptionFactor() {
            return getSelectedFactor();
        }

        @Override
        public List<TargetomeDatabase> getDatabases() {
            return TargetomeDatabase.getAllDatabases();
        }

        @Override
        public int getOccurrenceCountThreshold() {
            return QueryMetatargetomeAction.DEFAULT_THRESHOLD;
        }

        @Override
        public int getMaxNumberOfNodes() {
            return QueryMetatargetomeAction.DEFAULT_MAX_NODE_COUNT;
        }

        @Override
        public boolean createNewNetwork() {
            return true;
        }

        private GeneIdentifier getSelectedFactor() {
            final List<CyNode> nodes = CytoscapeNetworkUtilities.getSelectedNodes();
            if (nodes == null || nodes.isEmpty()) return null;
            final CyNode node = nodes.iterator().next();
            return new GeneIdentifier(node.getIdentifier(), SpeciesNomenclature.HOMO_SAPIENS_HGNC);
        }
    };

    private static Map<SpeciesNomenclature, Set<GeneIdentifier>> SPECIES_NOMENCLATURE2FACTORS;

    static {
        try {
            SPECIES_NOMENCLATURE2FACTORS = queryForFactors();
        } catch (ServerCommunicationException e) {
            Logger.getInstance().error(e);
            SPECIES_NOMENCLATURE2FACTORS = Collections.emptyMap();
        }
    }

    private static Map<SpeciesNomenclature, Set<GeneIdentifier>> queryForFactors() throws ServerCommunicationException {
        final ComputationalService service = new ComputationalServiceHTTP();
        final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature, Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, service.queryTranscriptionFactorsWithPredictedTargetome(speciesNomenclature));
        }
        return speciesNomenclature2factors;
    }

    public static Set<GeneIdentifier> getAvailableFactors(final SpeciesNomenclature speciesNomenclature) {
        if (SPECIES_NOMENCLATURE2FACTORS.containsKey(speciesNomenclature)) {
            return SPECIES_NOMENCLATURE2FACTORS.get(speciesNomenclature);
        } else {
            return Collections.emptySet();
        }
    }

    private MetatargetomeParameters parameters;
    private final Refreshable resultsPanel;

    public QueryMetatargetomeAction(final MetatargetomeParameters parameters, final Refreshable view) {
        super(NAME);
        this.parameters = parameters;
        this.resultsPanel = view;
        refresh();
    }

    public QueryMetatargetomeAction(final Refreshable view) {
        this(null, view);
    }

    public MetatargetomeParameters getParameters() {
        return parameters;
    }

    public void setParameters(final MetatargetomeParameters parameters) {
        this.parameters = parameters;
        refresh();
    }

    public Refreshable getResultsPanel() {
        return resultsPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (SPECIES_NOMENCLATURE2FACTORS.isEmpty()) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Problem while communicating with server.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final MetatargetomeParameters parameters = new DefaultMetatargetomeParameters(getParameters());

        final ComputationalService service = new ComputationalServiceHTTP();
        final List<CandidateTargetGene> targetome;
        try {
            targetome = service.queryPredictedTargetome(
                    parameters.getTranscriptionFactor(),
                    parameters.getDatabases(),
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

    private String createTitle(final MetatargetomeParameters parameters) {
        return "Metatargetome for " + parameters.getTranscriptionFactor().getGeneName();
    }

    @Override
    public void refresh() {
        setEnabled(checkEnabled());
    }

    private boolean checkEnabled() {
        if (getParameters() == null) return false;
        if (getParameters().getDatabases().isEmpty()) return false;
        final GeneIdentifier factor = getParameters().getTranscriptionFactor();
        if (factor == null) return false;
        if (getParameters().getOccurrenceCountThreshold() < 0) return false;
        if (getParameters().getMaxNumberOfNodes() < 0) return false;
        if (!SPECIES_NOMENCLATURE2FACTORS.containsKey(factor.getSpeciesNomenclature())) return false;
        return SPECIES_NOMENCLATURE2FACTORS.get(factor.getSpeciesNomenclature()).contains(factor);
    }
}
