package view.resultspanel.motifview.tablemodels;

import domainmodel.Motif;

import javax.swing.table.TableModel;
import java.util.List;


public interface MotifTableModel extends TableModel {

	public Motif getMotifAtRow(int row);

	public List<Integer> getColumnImportances();
	
	public String[] getTooltips();
}
