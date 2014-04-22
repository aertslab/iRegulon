package view.resultspanel;

import domainmodel.AbstractMotifAndTrack;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TableMotifAndTrackSelectionConnector implements ListSelectionListener {
    private JTable table;
    private SelectedMotifOrTrack selectedMotifOrTrack;

    public static ListSelectionListener connect(JTable table, SelectedMotifOrTrack selectedMotifOrTrack) {
        return new TableMotifAndTrackSelectionConnector(table, selectedMotifOrTrack);
    }

    public static void unconnect(JTable table, ListSelectionListener selectionListener) {
        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.removeListSelectionListener(selectionListener);
    }

    public TableMotifAndTrackSelectionConnector(JTable table, SelectedMotifOrTrack selectedMotifOrTrack) {
        if (table == null || selectedMotifOrTrack == null) {
            throw new IllegalArgumentException();
        }
        this.table = table;
        this.selectedMotifOrTrack = selectedMotifOrTrack;

        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
    }

    protected JTable getTable() {
        return table;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        this.selectedMotifOrTrack.setMotifOrTrack(this.getSelectedMotifOrTrack());
    }

    public AbstractMotifAndTrack getSelectedMotifOrTrack() {
        return this.getSelectedMotifOrTrack(this.table);
    }

    public AbstractMotifAndTrack getSelectedMotifOrTrack(final JTable table) {
        final int[] selectedRowIndices = table.getSelectedRows();
        if (selectedRowIndices.length == 0) {
            return null;
        } else {
            final MotifAndTrackTableModel model = (MotifAndTrackTableModel) table.getModel();
            final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
            return model.getMotifOrTrackAtRow(modelRowIdx);
        }
    }
}
