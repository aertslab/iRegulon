package view.resultspanel;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import domainmodel.*;
import infrastructure.CytoscapeNetworkUtilities;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;
import view.resultspanel.guiwidgets.TranscriptionFactorListener;

import java.util.*;


public abstract class TranscriptionFactorDependentAction extends NetworkDrawAction {
    private static final String RANK_ATTRIBUTE_NAME = "Rank";

    private SelectedMotif selectedMotif;
	private TranscriptionFactorComboBox selectedTranscriptionFactor;

	public TranscriptionFactorDependentAction(final String actionName,
                                              final SelectedMotif selectedMotif,
                                              final TranscriptionFactorComboBox selectedTranscriptionFactor,
                                              final Refreshable view,
                                              final String attributeName) {
        super(actionName, view,attributeName);
		setEnabled(false);
		this.selectedMotif = selectedMotif;
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;

        selectedTranscriptionFactor.addListener(new TranscriptionFactorListener() {
            @Override
            public void factorChanged() {
                setEnabled(checkEnabled());
            }
        });
        selectedMotif.registerListener(new MotifListener() {
            @Override
            public void newMotifSelected(AbstractMotif currentSelection) {
                setEnabled(checkEnabled());
            }
        });
	}

    protected boolean checkEnabled() {
        return this.selectedMotif.getMotif() != null
                && this.selectedTranscriptionFactor.getTranscriptionFactor() != null;
    }

    public SelectedMotif getSelectedMotif() {
        return this.selectedMotif;
    }

	public TranscriptionFactor getTranscriptionFactor() {
        final GeneIdentifier factor = selectedTranscriptionFactor.getTranscriptionFactor();
        return factor == null ? null : new TranscriptionFactor(factor, Float.NaN, Float.NaN, null, null, null, null);
	}

    protected boolean addEdges(final CyNetwork network, final CyNetworkView view,
                               final TranscriptionFactor factor, final AbstractMotif motif,
                               final boolean createNodesIfNecessary) {
        final Map<String,List<CyNode>> name2nodes = !createNodesIfNecessary
                ? CytoscapeNetworkUtilities.getNodeMap(getAttributeName(), CytoscapeNetworkUtilities.getAllNodes())
                : Collections.<String, List<CyNode>>emptyMap();

        final List<CyNode> sourceNodes = createNodesIfNecessary
                ? Collections.singletonList(createSourceNode(network, view, factor, motif))
                : findCyNodes(factor.getGeneID(), name2nodes);
        if (sourceNodes.isEmpty()) return false;

        for (final CyNode sourceNode : sourceNodes) {
            for (CandidateTargetGene targetGene : motif.getCandidateTargetGenes()) {
                final GeneIdentifier geneID = targetGene.getGeneID();

                final List<CyNode> targetNodes = createNodesIfNecessary
                    ? Collections.singletonList(createTargetNode(network, view, targetGene, motif))
                    : findCyNodes(geneID, name2nodes);

                for (final CyNode targetNode : targetNodes) {
                    final CyEdge edge = createEdge(sourceNode, targetNode, factor, motif, geneID);
                    setEdgeAttribute(edge, RANK_ATTRIBUTE_NAME, targetGene.getRank());
                }
		    }
        }

        return true;
    }

	private List<CyNode> findCyNodes(final GeneIdentifier geneIdentifier, final Map<String,List<CyNode>> name2nodes) {
        if (name2nodes.containsKey(geneIdentifier.getGeneName())) {
            return name2nodes.get(geneIdentifier.getGeneName());
        } else {
            return Collections.emptyList();
        }
    }
}
