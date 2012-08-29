package view.actions;


import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import domainmodel.AbstractMotif;
import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;
import servercommunication.ComputationalService;
import servercommunication.ComputationalServiceHTTP;
import servercommunication.ServerCommunicationException;
import view.parametersform.MetatargetomeParameters;
import view.resultspanel.NetworkDrawAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;

public class QueryMetatargetomeAction extends NetworkDrawAction {
    private static final String NAME = "action_query_metatargetome";

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

    private final MetatargetomeParameters parameters;

    public QueryMetatargetomeAction(final MetatargetomeParameters parameters) {
        super(NAME);
        this.parameters = parameters;
    }

    public MetatargetomeParameters getParameters() {
        return parameters;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
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

        final CyNetwork network =  Cytoscape.getCurrentNetwork();
		final CyNetworkView view = Cytoscape.getCurrentNetworkView();

        final CyNode sourceNode = createSourceNode(network, view, parameters.getTranscriptionFactor(), NO_MOTIF);
        for (CandidateTargetGene targetGene : targetome) {
            final CyNode targetNode = createTargetNode(network, view, targetGene, NO_MOTIF);
            addEdge(sourceNode, targetNode, network, view, "");
        }

        Cytoscape.getEdgeAttributes().setUserVisible(FEATURE_ID_ATTRIBUTE_NAME, false);

        view.redrawGraph(true, true);

        activeSidePanel();
    }
}
