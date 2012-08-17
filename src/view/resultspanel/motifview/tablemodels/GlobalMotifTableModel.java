package view.resultspanel.motifview.tablemodels;

import domainmodel.Motif;


public interface GlobalMotifTableModel {

	public Motif getMotifAtRow(int row);
	
	public Class<?> getColumnClass(int columnIndex);
	
	public int[] getCollumnImportance();
	
	public String[] getTooltips();
	
}
