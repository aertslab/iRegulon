package view.resultspanel.motifandtrackclusterview;

import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifAndTrackTableModel;
import view.resultspanel.SelectedMotifOrTrack;
import view.resultspanel.TableMotifAndTrackSelectionConnector;
import view.resultspanel.guiwidgets.TranscriptionFactorComboBox;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public final class TableMotifAndTrackClusterSelectionConnector extends TableMotifAndTrackSelectionConnector {
    private final TranscriptionFactorComboBox selectedTranscriptionFactor;


    public static ListSelectionListener connect(JTable table, SelectedMotifOrTrack selectedMotifOrTrack, TranscriptionFactorComboBox selectedTranscriptionFactor) {
        return new TableMotifAndTrackClusterSelectionConnector(table, selectedMotifOrTrack, selectedTranscriptionFactor);
    }

    public static void unconnect(JTable table, ListSelectionListener selectionListener) {
        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.removeListSelectionListener(selectionListener);
    }

    private TableMotifAndTrackClusterSelectionConnector(JTable table, SelectedMotifOrTrack selectedMotifOrTrack, TranscriptionFactorComboBox selectedTranscriptionFactor) {
        super(table, selectedMotifOrTrack);
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;

        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        this.selectedTranscriptionFactor.setSelectedItem(getSelectedTranscriptionFactor());
    }

    public TranscriptionFactor getSelectedTranscriptionFactor() {
        final int[] selectedRowIndices = getTable().getSelectedRows();
        if (selectedRowIndices.length == 0) {
            return null;
        } else {
            final MotifAndTrackTableModel model = (MotifAndTrackTableModel) getTable().getModel();
            final int modelRowIdx = getTable().convertRowIndexToModel(selectedRowIndices[0]);
            return model.getTranscriptionFactorAtRow(modelRowIdx);
        }
    }
}
