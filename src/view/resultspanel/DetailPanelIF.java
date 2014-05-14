package view.resultspanel;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;
import view.Refreshable;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;


public interface DetailPanelIF extends MotifAndTrackListener, Refreshable {
    public AbstractMotifAndTrack getSelectedMotifOrTrack();

    public TranscriptionFactor getSelectedTranscriptionFactor();

    public void registerSelectionComponents(final TranscriptionFactorComboBox tfcombobox);

    public void unregisterSelectionComponents();
}
