package view.resultspanel.transcriptionfactorview;

import domainmodel.Motif;
import domainmodel.TranscriptionFactor;
import view.resultspanel.SelectedMotif;
import view.resultspanel.TFComboBox;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TableMotifSelectionConnector implements ListSelectionListener {
	private final JTable table;
	private final SelectedMotif selectedMotif;
    private final TFComboBox selectedTranscriptionFactor;
	

    public static void connect(JTable table, SelectedMotif selectedMotif, TFComboBox selectedTranscriptionFactor) {
        new TableMotifSelectionConnector(table, selectedMotif, selectedTranscriptionFactor);
    }

	private TableMotifSelectionConnector(JTable table, SelectedMotif selectedMotif, TFComboBox selectedTranscriptionFactor){
		if (table == null || selectedMotif == null){
			throw new IllegalArgumentException();
		}
		this.table = table;
		this.selectedMotif = selectedMotif;
        this.selectedTranscriptionFactor = selectedTranscriptionFactor;

        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.selectedMotif.setMotif(this.getSelectedMotif());
		this.selectedTranscriptionFactor.setSelectedItem(getSelectedTranscriptionFactor());
	}

    public Motif getSelectedMotif(){
        return this.getSelectedMotif(this.table);
    }

	public Motif getSelectedMotif(final JTable table) {
		final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final EnrichedTranscriptionFactorTableModel model = (EnrichedTranscriptionFactorTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getTranscriptionFactorAtRow(modelRowIdx).getMotif();
		}
	}

    public TranscriptionFactor getSelectedTranscriptionFactor() {
        final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final EnrichedTranscriptionFactorTableModel model = (EnrichedTranscriptionFactorTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getTranscriptionFactorAtRow(modelRowIdx).getTranscriptionFactor();
		}
	}
}
