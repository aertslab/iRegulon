package view.resultspanel;

import domainmodel.AbstractMotif;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

import javax.swing.table.TableModel;
import java.util.List;


public interface MotifTableModel extends TableModel, ColumnImportances {
	public AbstractMotif getMotifAtRow(int row);

    public TranscriptionFactor getTranscriptionFactorAtRow(int row);

	public List<String> getTooltips();
}
