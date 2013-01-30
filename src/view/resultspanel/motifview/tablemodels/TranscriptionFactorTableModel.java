package view.resultspanel.motifview.tablemodels;

import domainmodel.AbstractMotif;
import domainmodel.TranscriptionFactor;
import view.resultspanel.TranscriptionFactorTableModelIF;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.List;

public class TranscriptionFactorTableModel extends AbstractTableModel implements TranscriptionFactorTableModelIF {
	private static final String[] COLUMN_NAMES = {
            "Transcription Factor Name",
            "Orthologous Identity",
            "Motif Similarity (FDR)" };
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(2, 3, 3);
	private static final String[] COLUMN_TOOLTIPS = {
            "Gene ID of the predicted transcription factor.",
            "Orthologous identity as fraction.",
            "Motif similarity (false discovery rate)."};


private final AbstractMotif motif;

	public TranscriptionFactorTableModel(final AbstractMotif motif) {
		this.motif = motif;
	}

    public TranscriptionFactorTableModel() {
		this(null);
	}

	@Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int rowIndex) {
		return (this.motif == null) ? null : this.motif.getTranscriptionFactors().get(rowIndex);
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public int getRowCount() {
		return (this.motif == null) ? 0 : this.motif.getTranscriptionFactors().size();
	}

	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
	}

    public Class<?> getColumnClass(int columnIndex) {
    	switch (columnIndex){
    	case 0 : return String.class;
    	case 1 : return Float.class;
    	case 2 : return Float.class;
        default: throw new IndexOutOfBoundsException();
    	}
    }

    public Object getValueAt(int row, int column) {
        final TranscriptionFactor tf = getTranscriptionFactorAtRow(row);
        switch (column){
            case 0 : return tf.getGeneID().getGeneName();
            case 1 : return tf.getMinOrthologousIdentity();
            case 2 : return tf.getMaxMotifSimilarityFDR();
            default: throw new IndexOutOfBoundsException();
        }
    }

	@Override
    public String[] getTooltips() {
		return COLUMN_TOOLTIPS;
    }

    public List<Integer> getColumnImportances() {
        return COLUMN_IMPORTANCES;
    }
}
