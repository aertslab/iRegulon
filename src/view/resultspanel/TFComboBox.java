package view.resultspanel;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import domainmodel.*;


public class TFComboBox extends JComboBox implements MotifListener {
    private final SpeciesNomenclature speciesNomenclature;

    public TFComboBox(SelectedMotif selectedMotif, final SpeciesNomenclature speciesNomenclature) {
		super();
        this.speciesNomenclature = speciesNomenclature;
        selectedMotif.registerListener(this);
		setEnabled(false);
	}

    public void registerAction(final TranscriptionFactorDependentAction action) {
        final JTextComponent textComponent = (JTextComponent) getEditor().getEditorComponent();
        textComponent.getDocument().addDocumentListener(action);
    }

    public GeneIdentifier getTranscriptionFactor() {
        final JTextComponent textComponent = (JTextComponent) getEditor().getEditorComponent();
        final Document document = textComponent.getDocument();
        final String geneName;
        try {
            geneName = document.getText(0, document.getLength()).trim();
        } catch (BadLocationException e) {
            return null;
        }
        if (geneName.equals("")) return null;
        else return new GeneIdentifier(geneName, speciesNomenclature);
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
