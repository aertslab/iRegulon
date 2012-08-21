package view.resultspanel;


import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;


public interface DetailPanelIF extends MotifListener {
    public AbstractMotif getSelectedMotif();

    public TranscriptionFactor getSelectedTranscriptionFactor();

    public void registerSelectionComponents(final TFComboBox tfcombobox);

    public void unregisterSelectionComponents();
}
