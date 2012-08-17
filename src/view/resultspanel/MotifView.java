package view.resultspanel;


import domainmodel.Motif;
import javax.swing.*;


public interface MotifView {
    public JComponent createPanel(final SelectedMotif selectedMotif, final TFComboBox transcriptionFactorCB,
                                  final JComboBox filterAttributeTF, final JTextField filterValueTF);

    public Motif getSelectedMotif();
}
