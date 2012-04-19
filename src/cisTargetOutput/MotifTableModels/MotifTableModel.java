package cisTargetOutput.MotifTableModels;


import java.util.*;

import javax.swing.table.AbstractTableModel;

import domainModel.Motif;

public class MotifTableModel extends AbstractTableModel implements GlobalMotifTableModel{
	private static final int NR_OF_COLUMNS = 7;
	private final List<Motif> motifList; 
	private final String[] m_colNames = {"Rank", "Enriched Motif ID", 
			"NES", "AUC", "ClusterCode", "#TG", "#TF"};
	private final int nrOfRows;
	private final double[] collumnWidthPercentage = {0.06, 0.5, 0.1, 0.1, 0.06, 0.08, 0.08};
	private int[] collumnImportant = {3,1,2,2,3,2,2};
	
	public MotifTableModel(List<Motif> motifList){
		this.motifList = motifList;
		this.nrOfRows = this.calculateRowNr();
	}
	
	public Motif getRegulatoryTreeAtRow(int row) {
		return this.motifList.get(row);
	}
	
	public int calculateRowNr(){
		  return this.motifList.size();
	}
	
	/**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     *  @param columnIndex  the column being queried
     *  @return the Object.class
     */
    public Class<?> getColumnClass(int columnIndex) {
    	switch (columnIndex){
    	case 0 : return Integer.class;
    	case 1 : return String.class;
    	case 2 : return Float.class;
    	case 3 : return Float.class;
    	case 4 : return Integer.class;
    	case 5 : return Integer.class;
    	case 6 : return Integer.class;
    	}
        return Object.class;
    }
	
	  public int getRowCount() {
		  return this.nrOfRows;
	  }
	  
	  public int getColumnCount() {
		 return NR_OF_COLUMNS; 
	  }
	  
	  public Object getValueAt(int row, int column) {
		  //MyFormat format = new MyFormat("0.000");
		  final Motif curMotif = this.motifList.get(row);
		  switch (column) {
		  case 0: return (Integer) curMotif.getRank();
		  case 1: return (String) curMotif.getEnrichedMotifID();
		  case 2: return (Float) curMotif.getNeScore();
		  case 3: return (Float) curMotif.getAucValue();
		  case 4: return (Integer) curMotif.getClusterCode();
		  case 5: return (Integer) curMotif.getCandidateTargetGenes().size();
		  case 6: return (Integer) curMotif.getTranscriptionFactors().size();
		  }
		  return null;
	  }
	  
	  public String getColumnName(int col) {
	      return m_colNames[col];
	   }
	  
	  public List<Motif> getRegulatoryTree(){
		  return this.motifList;
	  }
	  
	  /*
	   * This method picks good column sizes.	
	   * If all column heads are wider than the column's cells'
	   * contents, then you can just use column.sizeWidthToFit().
	   */
	 /* protected void initColumnSizes(JTable table) {
		  TableColumn column = null;
		  for (int i = 0; i < table.getColumnCount(); i++) {
			  column = table.getColumnModel().getColumn(i);
			  column.setPreferredWidth((int) Math.round(500 * this.collumnWidthPercentage[i]));
			  column.setWidth((int) Math.round(500 * this.collumnWidthPercentage[i]));
			  //System.out.println((int) Math.round(500 * this.collumnWidthPercentage[i]));
		  }
	  }*/
	  
	  public int[] getCollumnImportance(){
		  return this.collumnImportant;
	  }
	  
}
