package view.resultspanel;

import domainmodel.AbstractMotif;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class TableMotifSelectionConnector implements ListSelectionListener {
	private JTable table;
	private SelectedMotif selectedMotif;

	public static ListSelectionListener connect(JTable table, SelectedMotif selectedMotif) {
        return new TableMotifSelectionConnector(table, selectedMotif);
    }

    public static void unconnect(JTable table, ListSelectionListener selectionListener) {
        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.removeListSelectionListener(selectionListener);
    }
	
	public TableMotifSelectionConnector(JTable table, SelectedMotif selectedMotif){
		if (table == null || selectedMotif == null){
			throw new IllegalArgumentException();
		}
		this.table = table;
		this.selectedMotif = selectedMotif;

        final ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
	}

    protected JTable getTable() {
        return table;
    }

	@Override
	public void valueChanged(ListSelectionEvent e) {
		this.selectedMotif.setMotif(this.getSelectedMotif());
	}

    public AbstractMotif getSelectedMotif(){
        return this.getSelectedMotif(this.table);
    }

	public AbstractMotif getSelectedMotif(final JTable table) {
		final int[] selectedRowIndices = table.getSelectedRows();
		if (selectedRowIndices.length == 0){
			return null;
		} else {
            final MotifTableModel model = (MotifTableModel) table.getModel();
			final int modelRowIdx = table.convertRowIndexToModel(selectedRowIndices[0]);
			return model.getMotifAtRow(modelRowIdx);
		}
	}
}
