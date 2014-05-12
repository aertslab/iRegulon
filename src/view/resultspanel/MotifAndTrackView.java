package view.resultspanel;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import javax.swing.*;


public interface MotifAndTrackView extends Refreshable {
    public JTable getMasterTable();

    public AbstractFilterMotifAndTrackTableModel getModel();

    public DetailPanelIF getDetailPanel();

    public FilterAttributeActionListener getFilterAttributeListener();

    public void setFilterAttributeListener(FilterAttributeActionListener listener);

    public FilterPatternDocumentListener getFilterPatternListener();

    public void setFilterPatternListener(FilterPatternDocumentListener listener);

    public AbstractMotifAndTrack getSelectedMotifOrTrack();

    public void setSelectedMotifOrTrack(AbstractMotifAndTrack motifOrTrack);

    public TranscriptionFactor getSelectedTranscriptionFactor();

    public void registerSelectionComponents(final SelectedMotifOrTrack selectedMotifOrTrack,
                                            final TranscriptionFactorComboBox transcriptionFactorCB);

    public void unregisterSelectionComponents(final SelectedMotifOrTrack selectedMotifOrTrack,
                                              final TranscriptionFactorComboBox transcriptionFactorCB);

    public void registerFilterComponents(final JComboBox filterAttributeTF, final JTextField filterValueTF);

    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF);
}
