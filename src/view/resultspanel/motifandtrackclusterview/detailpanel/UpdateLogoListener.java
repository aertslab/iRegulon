package view.resultspanel.motifandtrackclusterview.detailpanel;


import domainmodel.Motif;
import view.resultspanel.MotifAndTrackTableModel;
import view.resultspanel.guiwidgets.LogoThumbnail;

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
            final MotifAndTrackTableModel model = (MotifAndTrackTableModel) table.getModel();
            if (model.getMotifOrTrackAtRow(table.convertRowIndexToModel(rowIdx)).isMotif()) {
                thumbnail.setMotif((Motif) model.getMotifOrTrackAtRow(table.convertRowIndexToModel(rowIdx)));
            } else {
                thumbnail.setMotif(null);
            }
        }
    }
}
