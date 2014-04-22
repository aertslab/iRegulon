package view.resultspanel.guiwidgets;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifAndTrackListener;
import view.resultspanel.SelectedMotifOrTrack;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.util.*;


public final class TranscriptionFactorComboBox extends JComboBox {
    private final SpeciesNomenclature speciesNomenclature;

    private final List<TranscriptionFactorListener> listeners = new ArrayList<TranscriptionFactorListener>();

    public TranscriptionFactorComboBox(final SelectedMotifOrTrack selectedMotifOrTrack, final SpeciesNomenclature speciesNomenclature) {
        super();
        if (selectedMotifOrTrack == null || speciesNomenclature == null) throw new IllegalArgumentException();

        this.speciesNomenclature = speciesNomenclature;

        selectedMotifOrTrack.registerListener(new SuggestionsListener());
        selectedMotifOrTrack.registerListener(new EnablenessListener());

        final JTextComponent textComponent = (JTextComponent) getEditor().getEditorComponent();
        textComponent.getDocument().addDocumentListener(new UpdateListenersListener());

        setEnabled(false);
    }

    public void addListener(final TranscriptionFactorListener listener) {
        listeners.add(listener);
    }

    private void fireFactorChanged() {
        for (final TranscriptionFactorListener listener : new ArrayList<TranscriptionFactorListener>(listeners)) {
            listener.factorChanged();
        }
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

    private void reset(List<TranscriptionFactor> tfs) {
        removeAllItems();
        for (TranscriptionFactor tf : tfs) {
            addItem(tf);
        }
    }

    private class SuggestionsListener implements MotifAndTrackListener {
        @Override
        public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
            if (currentSelection != null) {
                final Set<GeneIdentifier> ids = new HashSet<GeneIdentifier>();
                final List<TranscriptionFactor> tfs = new ArrayList<TranscriptionFactor>();
                for (final TranscriptionFactor factor : currentSelection.getTranscriptionFactors()) {
                    if (!ids.contains(factor.getGeneID())) {
                        ids.add(factor.getGeneID());
                        tfs.add(factor);
                    }
                }
                Collections.sort(tfs);
                reset(tfs);
            } else {
                reset(Collections.<TranscriptionFactor>emptyList());
            }
        }
    }

    private class EnablenessListener implements MotifAndTrackListener {
        @Override
        public void newMotifOrTrackSelected(AbstractMotifAndTrack currentSelection) {
            setEnabled(currentSelection != null);
        }
    }

    private class UpdateListenersListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            fireFactorChanged();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fireFactorChanged();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fireFactorChanged();
        }
    }
}
