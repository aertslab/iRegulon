package view.resultspanel.motifclusterview.detailpanel;


import domainmodel.Motif;
import view.resultspanel.LogoThumbnail;
import view.resultspanel.MotifTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



class UpdateLogoListener implements ListSelectionListener {
    private final JTable table;
    private final LogoThumbnail thumbnail;

    UpdateLogoListener(JTable table, LogoThumbnail thumbnail) {
        this.table = table;
        this.thumbnail = thumbnail;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        final int rowIdx = table.getSelectedRow();
        if (rowIdx < 0) {
            thumbnail.setMotif(null);
        } else {
            final MotifTableModel model = (MotifTableModel) table.getModel();
            thumbnail.setMotif((Motif) model.getMotifAtRow(table.convertRowIndexToModel(rowIdx)));
        }
    }
}
