package resultsview.transcriptionfactorview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class EnrichedTranscriptionFactorCollectionTableModel extends AbstractTableModel{

	private List<EnrichedTranscriptionFactor> predictedTFs;
	private String[] colNames = {"TF rank", "TF", "#predicted", "Mean Score", "Value", "#perfect", "perfect Mean", "perfect Value"};
	private static final int NR_OF_COLUMNS = 8;
	
	public EnrichedTranscriptionFactorCollectionTableModel(Collection<EnrichedTranscriptionFactor> predictedTFs){
		this.predictedTFs = new ArrayList<EnrichedTranscriptionFactor>(predictedTFs);
	}
	
	@Override
	public int getRowCount() {
		return this.predictedTFs.size();
	}

	@Override
	public int getColumnCount() {
		return NR_OF_COLUMNS;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		final EnrichedTranscriptionFactor tf = this.predictedTFs.get(rowIndex);
		switch(columnIndex){
		case 0 : return (Integer) rowIndex + 1;
		case 1 : return (String) tf.getName();
		case 2 : return (Integer) tf.getTimesPredicted();
		case 3 : return (Float) tf.calculateMeanScore();
		case 4 : return (Float) tf.getTotalScore();
		case 5 : return (Integer) tf.getTimesPerfectMatch();
		case 6 : return (Float) tf.calculateMeanPerfectScore();
		case 7 : return (Float) tf.calculatePerfectScore();
		}
		return null;
	}
	
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex){
		case 0: return Integer.class;
		case 1: return String.class;
		case 2: return Integer.class;
		case 3: return Float.class;
		case 4: return Float.class;
		case 5: return Integer.class;
		case 6: return Float.class;
		case 7: return Float.class;
		}
		return Object.class;
	}
	
	public String getColumnName(int col){
		return this.colNames[col];
	}

}
