package view.resultspanel.transcriptionfactorview;

import domainmodel.Motif;
import view.resultspanel.SelectedMotif;
import view.resultspanel.motifview.tablemodels.GlobalMotifTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TableMotifSelectionConnector implements ListSelectionListener {
	private JTable table;
	private SelectedMotif selectedMotif;
	

    public static void connect(JTable table, SelectedMotif selectedMotif) {
        new TableMotifSelectionConnector(table, selectedMotif);
    }

	private TableMotifSelectionConnector(JTable table, SelectedMotif selectedMotif){
		if (table == null || selectedMotif == null){
			throw new IllegalArgumentException();
		}
		this.table = table;
		this.selectedMotif = selectedMotif;

        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.selectedMotif.setMotif(this.getSelectedMotif());
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
}
