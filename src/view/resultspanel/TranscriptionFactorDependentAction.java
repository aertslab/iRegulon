package view.resultspanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanel;
import domainmodel.*;
import view.CytoscapeNetworkUtilities;
import view.ResourceAction;

import java.util.*;


public abstract class TranscriptionFactorDependentAction extends NetworkDrawAction
        implements ListSelectionListener, DocumentListener {


    private SelectedMotif selectedMotif;
	private TranscriptionFactor transcriptionFactor;
    private Refreshable view;
	
	public TranscriptionFactorDependentAction(final String actionName,
                                              final SelectedMotif selectedMotif,
                                              final TFComboBox selectedTranscriptionFactor,
                                              final Refreshable view) {
        super(actionName);
		setEnabled(false);
		this.selectedMotif = selectedMotif;
		transcriptionFactor = null;
        this.view = view;

        selectedTranscriptionFactor.registerAction(this);

        //TODO: Implementation of undo functionality can be done via cytoscape.util.undo.CyUndo: a tiny class
        // for supporting undo in the Cytoscape context. If you want to post an edit, use
        // CyUndo.getUndoableEditSupport().postEdit(yourEdit) where yourEdit implements the UndoableEdit.
        // Every implementing action needs to use basic operations like addNode, addEdge which
        // are implemented in this abstract class. Adding undo functionality is just adding
        // the creation of UndoableEdit objects, combining them in a composite UndoableEdit and
        // registering this compound object with Cytoscape.
	}

    protected Refreshable getView() {
        return view;
    }

    public SelectedMotif getSelectedMotif() {
        return this.selectedMotif;
    }

	public TranscriptionFactor getTranscriptionFactor() {
		return this.transcriptionFactor;
	}

	private void setTranscriptionFactorByName(String geneName){
		GeneIdentifier geneID = new GeneIdentifier(geneName, SpeciesNomenclature.UNKNOWN);
		this.transcriptionFactor = new TranscriptionFactor(geneID, Float.NaN, Float.NaN, null, null, null, null);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		final ListSelectionModel model = (ListSelectionModel) e.getSource();
		setEnabled(!model.isSelectionEmpty());
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		} else {
			this.setEnabled(true);
			try {
				this.setTranscriptionFactorByName(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			try {
				this.setTranscriptionFactorByName(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		if (e.getDocument().getLength() <= 0){
			this.setEnabled(false);
		}else{
			this.setEnabled(true);
			try {
				this.setTranscriptionFactorByName(e.getDocument().getText(0, e.getDocument().getLength()));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

    protected boolean addEdges(final CyNetwork network, final CyNetworkView view,
                               final TranscriptionFactor factor, final AbstractMotif motif,
                               final boolean createNodesIfNecessary) {
        final Map<String,List<CyNode>> name2nodes = getNodeMap(selectedMotif.getAttributeName(), CytoscapeNetworkUtilities.getAllNodes());

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
