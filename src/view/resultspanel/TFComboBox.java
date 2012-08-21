package view.resultspanel;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import domainmodel.AbstractMotif;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;


public class TFComboBox extends JComboBox implements MotifListener {
	public TFComboBox(SelectedMotif selectedMotif) {
		super();
		selectedMotif.registerListener(this);
		setEnabled(false);
	}

    public void registerAction(final TranscriptionFactorDependentAction action) {
        final JTextComponent textComponent = (JTextComponent) getEditor().getEditorComponent();
        textComponent.getDocument().addDocumentListener(action);
    }
	
	@Override
    public void newMotifSelected(AbstractMotif currentSelection) {
        if (currentSelection != null) {
            final List<TranscriptionFactor> tfs = new ArrayList<TranscriptionFactor>(currentSelection.getTranscriptionFactors());
            Collections.sort(tfs);
            reset(tfs);
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    private void reset(List<TranscriptionFactor> tfs) {
        removeAllItems();
        for (TranscriptionFactor tf : tfs) {
            addItem(tf);
        }
    }
}
