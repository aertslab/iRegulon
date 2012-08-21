package view.resultspanel.motifview.detailpanel;

import view.resultspanel.TFComboBox;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import domainmodel.TranscriptionFactor;
import view.resultspanel.TranscriptionFactorTableModel;

public class SelectedTranscriptionFactorListener implements ListSelectionListener {
	private final JTable table;
	private final TFComboBox tfcombobox;
	
	public SelectedTranscriptionFactorListener(JTable table, TFComboBox tfcombobox){
		this.table = table;
		this.tfcombobox = tfcombobox;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		TranscriptionFactor tf = this.getSelectedTranscriptionFactor();
		this.tfcombobox.setSelectedItem(tf);
	}
	
	public TranscriptionFactor getSelectedTranscriptionFactor(){
		final TranscriptionFactorTableModel tftModel = (TranscriptionFactorTableModel) table.getModel();
	
		int[] rowsSelected = table.getSelectedRows();
		if (rowsSelected.length == 0){
			return null;
		} else {
			int regTreeSelected = (table.convertRowIndexToModel(rowsSelected[0]));
			TranscriptionFactor tf = tftModel.getTranscriptionFactorAtRow(regTreeSelected);
			return tf;
		}
	}
}
