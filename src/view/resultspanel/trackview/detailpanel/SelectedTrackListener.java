package view.resultspanel.trackview.detailpanel;

import domainmodel.TranscriptionFactor;
import view.resultspanel.TranscriptionFactorTableModelIF;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class SelectedTrackListener implements ListSelectionListener {

    private JTable table;
    private TFandTrackSelected tfTrack;

    public SelectedTrackListener(JTable table, TFandTrackSelected tfTrack) {
        this.table = table;
        this.tfTrack = tfTrack;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        TranscriptionFactorTableModelIF model = (TranscriptionFactorTableModelIF) this.table.getModel();
        int[] rowsSelected = table.getSelectedRows();
        if (rowsSelected.length == 0) {
        } else {
            TranscriptionFactor tf = model.getTranscriptionFactorAtRow(rowsSelected[0]);
            this.tfTrack.setTranscriptionFactor(tf);
        }
    }

}
