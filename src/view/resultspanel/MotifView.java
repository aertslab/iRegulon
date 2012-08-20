package view.resultspanel;


import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

import javax.swing.*;


public interface MotifView {
    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB);

    public Motif getSelectedMotif();

    public TranscriptionFactor getSelectedTranscriptionFactor();

    public void registerFilterComponents(final JComboBox filterAttributeTF, final JTextField filterValueTF);

    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF);
}
