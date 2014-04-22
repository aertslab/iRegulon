package view.resultspanel;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import domainmodel.GeneIdentifier;
import domainmodel.TranscriptionFactor;
import infrastructure.CytoscapeNetworkUtilities;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.guiwidgets.TranscriptionFactorListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public abstract class TranscriptionFactorDependentAction extends NetworkDrawAction {
    private static final String RANK_ATTRIBUTE_NAME = "Rank";

    private SelectedMotifOrTrack selectedMotifOrTrack;
    private TranscriptionFactorComboBox selectedTranscriptionFactor;

    public TranscriptionFactorDependentAction(final String actionName,
                                              final SelectedMotifOrTrack selectedMotifOrTrack,
                                              final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                              final Refreshable view,
                                              final String attributeName) {
        super(actionName, view, attributeName);
        setEnabled(false);
        this.selectedMotifOrTrack = selectedMotifOrTrack;
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;

        selectedTranscriptionFactor.addListener(new TranscriptionFactorListener() {
            @Override
            public void factorChanged() {
                setEnabled(checkEnabled());
            }
        });
        selectedMotifOrTrack.registerListener(new MotifAndTrackListener() {
            @Override
            public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
                setEnabled(checkEnabled());
            }
        });
    }

    protected boolean checkEnabled() {
        return this.selectedMotifOrTrack.getMotifOrTrack() != null
                && this.selectedTranscriptionFactor.getTranscriptionFactor() != null;
    }

    public SelectedMotifOrTrack getSelectedMotifOrTrack() {
        return this.selectedMotifOrTrack;
    }

    public TranscriptionFactor getTranscriptionFactor() {
        final GeneIdentifier factor = selectedTranscriptionFactor.getTranscriptionFactor();
        return factor == null ? null : new TranscriptionFactor(factor, Float.NaN, Float.NaN, null, null, null, null);
    }

    protected boolean addEdges(final CyNetwork network, final CyNetworkView view,
                               final TranscriptionFactor factor, final AbstractMotifAndTrack motifOrTrack,
                               final boolean createNodesIfNecessary) {
        final Map<String, List<CyNode>> name2nodes = !createNodesIfNecessary
                ? CytoscapeNetworkUtilities.getNodeMap(getAttributeName(), CytoscapeNetworkUtilities.getAllNodes())
                : Collections.<String, List<CyNode>>emptyMap();

        final List<CyNode> sourceNodes = createNodesIfNecessary
                ? Collections.singletonList(createSourceNode(network, view, factor, motifOrTrack))
                : findCyNodes(factor.getGeneID(), name2nodes);
        if (sourceNodes.isEmpty()) return false;

        for (final CyNode sourceNode : sourceNodes) {
            for (CandidateTargetGene targetGene : motifOrTrack.getCandidateTargetGenes()) {
                final GeneIdentifier geneID = targetGene.getGeneID();

                final List<CyNode> targetNodes = createNodesIfNecessary
                        ? Collections.singletonList(createTargetNode(network, view, targetGene, motifOrTrack))
                        : findCyNodes(geneID, name2nodes);

                for (final CyNode targetNode : targetNodes) {
                    final CyEdge edge = createEdge(sourceNode, targetNode, factor, motifOrTrack, geneID);
                    setEdgeAttribute(edge, RANK_ATTRIBUTE_NAME, targetGene.getRank());
                }
            }
        }

        return true;
    }

    private List<CyNode> findCyNodes(final GeneIdentifier geneIdentifier, final Map<String, List<CyNode>> name2nodes) {
        if (name2nodes.containsKey(geneIdentifier.getGeneName())) {
            return name2nodes.get(geneIdentifier.getGeneName());
        } else {
            return Collections.emptyList();
        }
    }
}
