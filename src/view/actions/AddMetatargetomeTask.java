package view.actions;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import infrastructure.CytoscapeNetworkUtilities;
import view.resultspanel.Refreshable;

import java.util.Collections;
import java.util.List;
import java.util.Map;


final class AddMetatargetomeTask extends MetatargetomeTask {
    AddMetatargetomeTask(CyNetwork network, CyNetworkView view, final Refreshable resultsPanel,
                         final String attributeName, GeneIdentifier factor, List<CandidateTargetGene> targetome) {
        super(network, view, resultsPanel, attributeName, factor, targetome);
    }

    @Override
    public void run() {
        if (getMonitor() != null) getMonitor().setStatus("Adding nodes and edges");

        final List<CyNode> nodes = CytoscapeNetworkUtilities.getAllNodes();
        final Map<String, List<CyNode>> name2nodes = CytoscapeNetworkUtilities.getNodeMap(getAttributeName(), nodes);

        final List<CyNode> sourceNodes =
                name2nodes.containsKey(getTranscriptionFactor().getGeneName())
                        ? name2nodes.get(getTranscriptionFactor().getGeneName())
                        : Collections.singletonList(CytoscapeNetworkUtilities.createSourceNode(getNetwork(), getView(), getAttributeName(), getTranscriptionFactor(), NO_MOTIF));
        final int totalCount = sourceNodes.size() * getTargetome().size();
        int count = 0;
        for (final CyNode sourceNode : sourceNodes) {
            CytoscapeNetworkUtilities.adjustSourceNode(sourceNode, getAttributeName(), getTranscriptionFactor(), NO_MOTIF);

            for (final CandidateTargetGene targetGene : getTargetome()) {
                if (isInterrupted()) return;
                if (getMonitor() != null) getMonitor().setPercentCompleted((100 * count) / totalCount);

                final List<CyNode> targetNodes =
                        name2nodes.containsKey(targetGene.getGeneName())
                                ? name2nodes.get(targetGene.getGeneName())
                                : Collections.singletonList(CytoscapeNetworkUtilities.createTargetNode(getNetwork(), getView(), getAttributeName(), targetGene, NO_MOTIF));
                for (final CyNode targetNode : targetNodes) {
                    CytoscapeNetworkUtilities.adjustTargetNode(targetNode, getAttributeName(), targetGene, NO_MOTIF);

                    final CyEdge edge = CytoscapeNetworkUtilities.createEdge(getNetwork(), getView(), sourceNode, targetNode, getTranscriptionFactor(), null, targetGene.getGeneID(), CytoscapeNetworkUtilities.REGULATORY_FUNCTION_METATARGETOME);
                    CytoscapeNetworkUtilities.setEdgeAttribute(edge, CytoscapeNetworkUtilities.STRENGTH_ATTRIBUTE_NAME, targetGene.getRank());
                }
                count++;
            }

        }

        Cytoscape.getEdgeAttributes().setUserVisible(CytoscapeNetworkUtilities.FEATURE_ID_ATTRIBUTE_NAME, false);
        getView().applyLayout(CyLayouts.getDefaultLayout());
        getView().redrawGraph(true, true);
        if (getResultsPanel() != null) getResultsPanel().refresh();
        CytoscapeNetworkUtilities.activeSidePanel();
    }
}
