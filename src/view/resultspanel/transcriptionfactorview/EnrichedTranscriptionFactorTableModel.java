package view.resultspanel.transcriptionfactorview;

import domainmodel.Motif;
import view.resultspanel.MotifTableModel;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class EnrichedTranscriptionFactorTableModel extends AbstractTableModel implements MotifTableModel {

	private final List<EnrichedTranscriptionFactor> transcriptionFactors;
	private final String[] COLUMN_NAMES = {
            "Rank",
            "TF",
            "Motif ID",
            "NES",
            "Orthologous Identity",
            "Orthologous GeneID",
            "Orthologous Species",
            "Motif Similarity FDR",
            "Similar Motif ID",
            "Similar Motif Description"};
    private final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(1, 3, 3, 2, 1, 1, 1, 1, 1, 1);
    private final List<String> COLUMN_TOOLTIPS = Arrays.asList("", "", "", "", "", "", "", "", "", "");


	public EnrichedTranscriptionFactorTableModel(List<EnrichedTranscriptionFactor> transcriptionFactors){
		this.transcriptionFactors = transcriptionFactors;
	}

    public EnrichedTranscriptionFactor getTranscriptionFactorAtRow(final int rowIdx) {
        return transcriptionFactors.get(rowIdx);
    }
	
	@Override
	public int getRowCount() {
		return this.transcriptionFactors.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		final EnrichedTranscriptionFactor tf = this.transcriptionFactors.get(rowIndex);
		switch(columnIndex){
		case 0 : return rowIndex + 1;
		case 1 : return tf.getTranscriptionFactorID().getGeneName();
		case 2 : return tf.getMotifID();
		case 3 : return tf.getNES();
		case 4 : return tf.getOrthologousIdentity();
		case 5 : return tf.getOrthologousGeneID();
		case 6 : return tf.getOrthologousSpecies();
		case 7 : return tf.getMotifSimilarityFDR();
		case 8 : return tf.getSimilarMotifID();
		case 9 : return tf.getSimilarMotifDescription();
		}
		return null;
	}
	
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex){
		case 0: return Integer.class;
		case 1: return String.class;
		case 2: return String.class;
		case 3: return Float.class;
		case 4: return Float.class;
		case 5: return String.class;
		case 6: return String.class;
		case 7: return Float.class;
		case 8: return String.class;
		case 9: return String.class;
		}
		return Object.class;
	}
	
	public String getColumnName(int col){
		return this.COLUMN_NAMES[col];
	}

    @Override
    public Motif getMotifAtRow(int rowIndex) {
        final EnrichedTranscriptionFactor tf = this.transcriptionFactors.get(rowIndex);
        return tf.getMotif();
    }

    @Override
    public List<Integer> getColumnImportances() {
        return COLUMN_IMPORTANCES;
    }

    @Override
    public List<String> getTooltips() {
        return COLUMN_TOOLTIPS;
    }
}
