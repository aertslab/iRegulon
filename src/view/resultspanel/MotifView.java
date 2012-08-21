package view.resultspanel;


import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;
import javax.swing.*;


public interface MotifView {
    public AbstractFilterMotifTableModel getModel();

    public FilterAttributeActionListener getFilterAttributeListener();

    public void setFilterAttributeListener(FilterAttributeActionListener listener);

    public FilterPatternDocumentListener getFilterPatternListener();

    public void setFilterPatternListener(FilterPatternDocumentListener listener);

    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB);

    public AbstractMotif getSelectedMotif();

    public TranscriptionFactor getSelectedTranscriptionFactor();

    public void registerSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB);

    public void unregisterSelectionComponents(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB);

    public void registerFilterComponents(final JComboBox filterAttributeTF, final JTextField filterValueTF);

    public void unregisterFilterComponents(JComboBox filterAttributeCB, JTextField filterValueTF);
}
