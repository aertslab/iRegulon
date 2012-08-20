package view.resultspanel;

import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

import javax.swing.table.TableModel;
import java.util.List;


public interface MotifTableModel extends TableModel {

	public Motif getMotifAtRow(int row);

    public TranscriptionFactor getTranscriptionFactorAtRow(int row);

	public List<Integer> getColumnImportances();
	
	public List<String> getTooltips();
}
