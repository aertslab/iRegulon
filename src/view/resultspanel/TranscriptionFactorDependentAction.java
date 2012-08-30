package view.resultspanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.view.CyNetworkView;
import domainmodel.*;
import view.CytoscapeNetworkUtilities;

import java.util.*;


public abstract class TranscriptionFactorDependentAction extends NetworkDrawAction {
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

        //TODO: Implementation of undo functionality can be done via cytoscape.util.undo.CyUndo: a tiny class
        // for supporting undo in the Cytoscape context. If you want to post an edit, use
        // CyUndo.getUndoableEditSupport().postEdit(yourEdit) where yourEdit implements the UndoableEdit.
        // Every implementing action needs to use basic operations like addNode, addEdge which
        // are implemented in this abstract class. Adding undo functionality is just adding
        // the creation of UndoableEdit objects, combining them in a composite UndoableEdit and
        // registering this compound object with Cytoscape.
	}

    private boolean checkEnabled() {
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
        final Map<String,List<CyNode>> name2nodes = getNodeMap(getAttributeName(), CytoscapeNetworkUtilities.getAllNodes());

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
                    createEdge(sourceNode, targetNode, factor, motif, geneID);
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
