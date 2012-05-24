package summaryTFs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class PredictedTFTableModel extends AbstractTableModel{

	private List<PredictedTF> predictedTFs;
	private String[] colNames = {"TF rank", "TF", "#predicted", "Mean Score", "#perfect", "perfect Score", "No orthologous", "Similar motif", "Recalculate Score"};
	private static final int NR_OF_COLUMNS = 9;
	
	public PredictedTFTableModel(Collection<PredictedTF> predictedTFs){
		this.predictedTFs = new ArrayList<PredictedTF>(predictedTFs);
	}
	
	@Override
	public int getRowCount() {
		return this.predictedTFs.size();
	}

	@Override
	public int getColumnCount() {
		return this.NR_OF_COLUMNS;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		final PredictedTF tf = this.predictedTFs.get(rowIndex);
		switch(columnIndex){
		case 0 : return (Integer) rowIndex + 1;
		case 1 : return (String) tf.getName();
		case 2 : return (Integer) tf.getTimesPredicted();
		case 3 : return (Float) tf.calculateMeanScore();
		case 4 : return (Integer) tf.getTimesPerfectMatch();
		case 5 : return (Float) tf.calculateMeanPerfectScore();
		case 6 : return (Integer) tf.getTimesOrthologousMatch();
		case 7 : return (Integer) tf.getTimesExactMotifMatch();
		case 8 : return (Float) tf.getTotalScore();
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
		case 7: return Integer.class;
		case 8: return Integer.class;
		case 9: return Float.class;
		}
		return Object.class;
	}
	
	public String getColumnName(int col){
		return this.colNames[col];
	}

}
