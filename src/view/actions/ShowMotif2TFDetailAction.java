package view.actions;

import infrastructure.CytoscapeEnvironment;
import view.ResourceAction;
import view.resultspanel.motifview.Motif2TFDetailFrame;
import view.resultspanel.motifview.detailpanel.TFandMotifSelected;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;


public final class ShowMotif2TFDetailAction extends ResourceAction implements ListSelectionListener {
    private static final String NAME = "action_detail_frame";

    private TFandMotifSelected tfMotif;

    public ShowMotif2TFDetailAction(final TFandMotifSelected tfMotif) {
        super(NAME);
        setEnabled(false);
        this.tfMotif = tfMotif;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JDialog frame = new Motif2TFDetailFrame(tfMotif);
        frame.setLocationRelativeTo(CytoscapeEnvironment.getInstance().getJFrame());
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        final ListSelectionModel model = (ListSelectionModel) e.getSource();
        setEnabled(!model.isSelectionEmpty());
    }
}
