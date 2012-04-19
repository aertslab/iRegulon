package cisTargetOutput.DetailPanel;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cisTargetOutput.TFComboBox;

import domainModel.TranscriptionFactor;

public class TFTableSelectionListen implements ListSelectionListener{

	private JTable table;
	private TFComboBox tfcombobox;
	
	public TFTableSelectionListen(JTable table, TFComboBox tfcombobox){
		this.table = table;
		this.tfcombobox = tfcombobox;
	}
	
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		//final TFTableModel tftModel = (TFTableModel) table.getModel();
		TranscriptionFactor tf = this.getSelectedTranscriptionFactor();
		this.tfcombobox.setSelectedItem(tf);
	}
	
	public TranscriptionFactor getSelectedTranscriptionFactor(){
		final TFTableModel tftModel = (TFTableModel) table.getModel();
	
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
