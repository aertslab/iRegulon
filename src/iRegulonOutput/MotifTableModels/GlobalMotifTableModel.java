package iRegulonOutput.MotifTableModels;

import domainModel.Motif;

public interface GlobalMotifTableModel {

	public Motif getRegulatoryTreeAtRow(int row);
	
	public Class<?> getColumnClass(int columnIndex);
	
	public int[] getCollumnImportance();
	
	public String[] getTooltips();
	
}
