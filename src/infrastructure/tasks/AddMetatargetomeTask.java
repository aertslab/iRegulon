package infrastructure.tasks;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import infrastructure.NetworkUtilities;
import view.Refreshable;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public final class AddMetatargetomeTask extends MetatargetomeTask {
    public AddMetatargetomeTask(CyNetwork network, CyNetworkView view, final Refreshable resultsPanel,
                         final String attributeName, GeneIdentifier factor, List<CandidateTargetGene> targetome) {
        super(network, view, resultsPanel, attributeName, factor, targetome);
    }

    @Override
    public void run() {
        if (getMonitor() != null) getMonitor().setStatus("Adding nodes and edges");

        final List<CyNode> nodes = NetworkUtilities.getAllNodes();
        final Map<String, List<CyNode>> name2nodes = NetworkUtilities.getNodeMap(getAttributeName(), nodes);

        final List<CyNode> sourceNodes =
                name2nodes.containsKey(getTranscriptionFactor().getGeneName())
                        ? name2nodes.get(getTranscriptionFactor().getGeneName())
                        : Collections.singletonList(NetworkUtilities.createSourceNode(getNetwork(), getView(), getAttributeName(), getTranscriptionFactor(), NO_MOTIF));
        final int totalCount = sourceNodes.size() * getTargetome().size();
        int count = 0;
        for (final CyNode sourceNode : sourceNodes) {
            NetworkUtilities.adjustSourceNode(sourceNode, getAttributeName(), getTranscriptionFactor(), NO_MOTIF);

            for (final CandidateTargetGene targetGene : getTargetome()) {
                if (isInterrupted()) return;
                if (getMonitor() != null) getMonitor().setPercentCompleted((100 * count) / totalCount);

                final List<CyNode> targetNodes =
                        name2nodes.containsKey(targetGene.getGeneName())
                                ? name2nodes.get(targetGene.getGeneName())
                                : Collections.singletonList(NetworkUtilities.createTargetNode(getNetwork(), getView(), getAttributeName(), targetGene, NO_MOTIF));
                for (final CyNode targetNode : targetNodes) {
                    NetworkUtilities.adjustTargetNode(targetNode, getAttributeName(), targetGene, NO_MOTIF);

                    final CyEdge edge = NetworkUtilities.createEdge(getNetwork(), getView(), sourceNode, targetNode, getTranscriptionFactor(), null, targetGene.getGeneID(), NetworkUtilities.REGULATORY_FUNCTION_METATARGETOME);
                    NetworkUtilities.setEdgeAttribute(edge, NetworkUtilities.STRENGTH_ATTRIBUTE_NAME, targetGene.getRank());
                }
                count++;
            }

        }

        Cytoscape.getEdgeAttributes().setUserVisible(NetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);
        getView().applyLayout(CyLayouts.getDefaultLayout());
        getView().redrawGraph(true, true);
        if (getResultsPanel() != null) getResultsPanel().refresh();
        NetworkUtilities.activeSidePanel();
    }
}
