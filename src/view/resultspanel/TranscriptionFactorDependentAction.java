package view.resultspanel;

import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import domainmodel.GeneIdentifier;
import domainmodel.Motif;
import domainmodel.SpeciesNomenclature;
import domainmodel.TranscriptionFactor;
import view.ResourceAction;


public abstract class TranscriptionFactorDependentAction extends ResourceAction
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
}
