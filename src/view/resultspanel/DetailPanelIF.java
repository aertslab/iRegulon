package view.resultspanel;


import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;


public interface DetailPanelIF extends MotifListener, Refreshable {
    public AbstractMotif getSelectedMotif();

    public TranscriptionFactor getSelectedTranscriptionFactor();

    public void registerSelectionComponents(final TranscriptionFactorComboBox tfcombobox);

    public void unregisterSelectionComponents();
}
