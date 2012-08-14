package view.resultspanel.motifview.detailpanel;

import javax.swing.table.AbstractTableModel;

import domainmodel.Motif;
import domainmodel.TranscriptionFactor;


public class TFTableModel extends AbstractTableModel {
	private final Motif motif;

	private final String[] columnNames = {
            "Transcription Factor Name",
            "Orthologous Identity",
            "Motif Similarity (FDR)" };
	private final String[] columnToolTips = {
            "The gene ID of the predicted transcritpion factor",
			"The orthologous identity as fraction",
			"The motif similarity FDR"};
	
	public TFTableModel(Motif motif) {
		this.motif = motif;
	}
	
	public int getColumnCount() {
		return this.columnNames.length;
	}

	public int getRowCount() {
		return this.motif == null ? 0 : this.motif.getTranscriptionFactors().size();
	}
	
	public TranscriptionFactor getTranscriptionFactorAtRow(int rowIndex) {
		return this.motif.getTranscriptionFactors().get(rowIndex);
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}
	
    public Class<?> getColumnClass(int columnIndex) {
    	switch (columnIndex){
    	case 0 : return String.class;
    	case 1 : return Float.class;
    	case 2 : return Float.class;
    	}
        return Object.class;
    }

    public Object getValueAt(int row, int column) {
        final TranscriptionFactor tf = getTranscriptionFactorAtRow(row);
        switch (column){
            case 0 : return tf.getGeneID().getGeneName();
            case 1 : return tf.getMinOrthologousIdentity();
            case 2 : return tf.getMaxMotifSimilarityFDR();
        }
        throw new IndexOutOfBoundsException();
    }
	
	public String[] getTooltips() {
		return this.columnToolTips;
	}
}
