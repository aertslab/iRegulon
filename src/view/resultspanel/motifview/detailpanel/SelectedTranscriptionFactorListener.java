package view.resultspanel.motifview.detailpanel;

import view.resultspanel.TranscriptionFactorComboBox;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import domainmodel.TranscriptionFactor;
import view.resultspanel.TranscriptionFactorTableModelIF;

class SelectedTranscriptionFactorListener implements ListSelectionListener {
	private final JTable table;
	private final TranscriptionFactorComboBox tfcombobox;
	
	public SelectedTranscriptionFactorListener(JTable table, TranscriptionFactorComboBox tfcombobox){
		this.table = table;
		this.tfcombobox = tfcombobox;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		TranscriptionFactor tf = this.getSelectedTranscriptionFactor();
		this.tfcombobox.setSelectedItem(tf);
	}
	
	public TranscriptionFactor getSelectedTranscriptionFactor(){
		final TranscriptionFactorTableModelIF tftModel = (TranscriptionFactorTableModelIF) table.getModel();
	
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
