package view.actions;


import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.logger.ConsoleLogger;
import cytoscape.logger.CyLogHandler;
import cytoscape.logger.LogLevel;
import cytoscape.view.CyNetworkView;
import domainmodel.*;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.parametersform.MetatargetomeParameters;
import view.resultspanel.NetworkDrawAction;
import view.resultspanel.Refreshable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class QueryMetatargetomeAction extends NetworkDrawAction implements Refreshable {
    private static final String NAME = "action_query_metatargetome";

    private static final CyLogHandler logger = ConsoleLogger.getLogger();

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

    private static final AbstractMotif NO_MOTIF = new AbstractMotif(-1,
            Collections.<CandidateTargetGene>emptyList(),
            Collections.<TranscriptionFactor>emptyList()) {
        @Override
        public int getDatabaseID() {
            return Integer.MIN_VALUE;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public float getAUCValue() {
            return Float.NaN;
        }

        @Override
        public float getNEScore() {
            return Float.NaN;
        }

        @Override
        public Motif getBestMotif() {
            return null;
        }

        @Override
        public List<Motif> getMotifs() {
            return Collections.emptyList();
        }
    };

    private MetatargetomeParameters parameters;

    public QueryMetatargetomeAction(final MetatargetomeParameters parameters, final Refreshable view) {
        super(NAME, view, null);
        this.parameters = parameters;
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

    @Override
    public String getAttributeName() {
        if (getParameters() != null) {
            return getParameters().getAttributeName();
        } else {
            return super.getAttributeName();
        }
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
                    getParameters().getDatabases());
        } catch (ServerCommunicationException e) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final CyNetwork network;
        final CyNetworkView view;
        boolean useCurrentNetwork = Cytoscape.getCurrentNetworkView() != null
                && Cytoscape.getCurrentNetworkView().getNetwork() != null
                && !Cytoscape.getCurrentNetworkView().getNetwork().equals(Cytoscape.getNullNetwork());
        if (useCurrentNetwork) {
            network = Cytoscape.getCurrentNetwork();
		    view = Cytoscape.getCurrentNetworkView();
        } else {
            network = Cytoscape.createNetwork(createTitle(getParameters()));
             view = Cytoscape.createNetworkView(network, createTitle(getParameters()));
         }

        final CyNode sourceNode = createSourceNode(network, view, parameters.getTranscriptionFactor(), NO_MOTIF);
        for (CandidateTargetGene targetGene : targetome) {
            final CyNode targetNode = createTargetNode(network, view, targetGene, NO_MOTIF);
            addEdge(sourceNode, targetNode, network, view, "");
        }

        Cytoscape.getEdgeAttributes().setUserVisible(FEATURE_ID_ATTRIBUTE_NAME, false);

        if (!useCurrentNetwork) {
            view.applyLayout(CyLayouts.getDefaultLayout());
            applyVisualStyle();
        }

        view.redrawGraph(true, true);

        if (getView() != null) getView().refresh();

        activeSidePanel();
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
