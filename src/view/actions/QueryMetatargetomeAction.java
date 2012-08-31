package view.actions;


import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;
import domainmodel.*;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.ResourceAction;
import view.parametersform.MetatargetomeParameters;
import view.resultspanel.Refreshable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;


public class QueryMetatargetomeAction extends ResourceAction implements Refreshable {
    private static final String NAME = "action_query_metatargetome";
    private static final int MAX_NODE_COUNT = 100;

    private static final CyLogHandler logger = ConsoleLogger.getLogger();

    public static final int DEFAULT_THRESHOLD;
    static {
        DEFAULT_THRESHOLD = Integer.parseInt(ResourceBundle.getBundle("iRegulon").getString("occurence_count_threshold"));
    }

    private static Map<SpeciesNomenclature,Set<GeneIdentifier>> SPECIES_NOMENCLATURE2FACTORS;
    static {
        try {
            SPECIES_NOMENCLATURE2FACTORS = queryForFactors();
        } catch (ServerCommunicationException e) {
            logger.handleLog(LogLevel.LOG_ERROR, e.getMessage());
            SPECIES_NOMENCLATURE2FACTORS = Collections.emptyMap();
        }
    }

    private static Map<SpeciesNomenclature,Set<GeneIdentifier>> queryForFactors() throws ServerCommunicationException {
        final ComputationalService service = new ComputationalServiceHTTP();
        final Map<SpeciesNomenclature,Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature,Set<GeneIdentifier>>();
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

        final ComputationalService service = new ComputationalServiceHTTP();
        final List<CandidateTargetGene> targetome;
        try {
            targetome = service.queryPredictedTargetome(
                    getParameters().getTranscriptionFactor(),
                    getParameters().getDatabases(),
                    getParameters().getOccurenceCountThreshold());
        } catch (ServerCommunicationException e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final CyNetworkView view;
        final MetatargetomeTask task;
        boolean useCurrentNetwork = Cytoscape.getCurrentNetworkView() != null
                && Cytoscape.getCurrentNetworkView().getNetwork() != null
                && !Cytoscape.getCurrentNetworkView().getNetwork().equals(Cytoscape.getNullNetwork());
        if (useCurrentNetwork) {
            final CyNetwork network = Cytoscape.getCurrentNetwork();
		    view = Cytoscape.getCurrentNetworkView();
            task = new AddMetatargetomeTask(network, view,
                    getResultsPanel(), getParameters().getAttributeName(),
                    getParameters().getTranscriptionFactor(), targetome);
        } else {
            final CyNetwork network = Cytoscape.createNetwork(createTitle(getParameters()));
            view = Cytoscape.createNetworkView(network, createTitle(getParameters()));
            task = new CreateMetatargetomeTask(network, view,
                    getResultsPanel(), getParameters().getAttributeName(),
                    getParameters().getTranscriptionFactor(), targetome);
        }

        if (targetome.size() < MAX_NODE_COUNT) {
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
        if (!SPECIES_NOMENCLATURE2FACTORS.containsKey(factor.getSpeciesNomenclature())) return false;
        return SPECIES_NOMENCLATURE2FACTORS.get(factor.getSpeciesNomenclature()).contains(factor);
    }
}
