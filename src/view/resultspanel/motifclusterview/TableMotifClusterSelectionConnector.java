package view.resultspanel.motifclusterview;

import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifTableModel;
import view.resultspanel.SelectedMotif;
import view.resultspanel.TranscriptionFactorComboBox;
import view.resultspanel.TableMotifSelectionConnector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public final class TableMotifClusterSelectionConnector extends TableMotifSelectionConnector {
    private final TranscriptionFactorComboBox selectedTranscriptionFactor;
	

    public static ListSelectionListener connect(JTable table, SelectedMotif selectedMotif, TranscriptionFactorComboBox selectedTranscriptionFactor) {
        return new TableMotifClusterSelectionConnector(table, selectedMotif, selectedTranscriptionFactor);
    }

    public static void unconnect(JTable table, ListSelectionListener selectionListener) {
        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.removeListSelectionListener(selectionListener);
    }

	private TableMotifClusterSelectionConnector(JTable table, SelectedMotif selectedMotif, TranscriptionFactorComboBox selectedTranscriptionFactor){
		super(table, selectedMotif);
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
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final MotifTableModel model = (MotifTableModel) getTable().getModel();
			final int modelRowIdx = getTable().convertRowIndexToModel(selectedRowIndices[0]);
			return model.getTranscriptionFactorAtRow(modelRowIdx);
		}
	}
}
