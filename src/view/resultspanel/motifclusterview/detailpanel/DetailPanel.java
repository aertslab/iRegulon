package view.resultspanel.motifclusterview.detailpanel;

import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;
import view.resultspanel.DetailPanelIF;
import view.resultspanel.TFComboBox;

import javax.swing.*;


public class DetailPanel extends JPanel implements DetailPanelIF {
    @Override
    public AbstractMotif getSelectedMotif() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TranscriptionFactor getSelectedTranscriptionFactor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerSelectionComponents(TFComboBox tfcombobox) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void unregisterSelectionComponents() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void newMotifSelected(AbstractMotif currentSelection) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
