package view.resultspanel.motifview.detailpanel;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import domainmodel.TranscriptionFactor;

public class SelectedMotifListener implements ListSelectionListener{

	private JTable table;
	private TFandMotifSelected tfMotif;
	
	public SelectedMotifListener(JTable table, TFandMotifSelected tfMotif){
		this.table = table;
		this.tfMotif = tfMotif;
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		TFTableModel model = (TFTableModel) this.table.getModel();
		int[] rowsSelected = table.getSelectedRows();
		if (rowsSelected.length == 0){
		}
		else{
			TranscriptionFactor tf = model.getTranscriptionFactorAtRow(rowsSelected[0]);
			this.tfMotif.setTranscriptionFactor(tf);
		}
	}

}