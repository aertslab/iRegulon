package view.resultspanel.motifview.tablemodels;

import domainmodel.Motif;

import javax.swing.table.TableModel;


public interface MotifTableModel extends TableModel {

	public Motif getMotifAtRow(int row);

	public int[] getColumnImportance();
	
	public String[] getTooltips();
}
