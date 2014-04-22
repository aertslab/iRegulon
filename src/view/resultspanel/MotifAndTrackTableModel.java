package view.resultspanel;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;

import javax.swing.table.TableModel;
import java.util.List;


public interface MotifAndTrackTableModel extends TableModel, ColumnImportances {
    public AbstractMotifAndTrack getMotifOrTrackAtRow(int row);

    public TranscriptionFactor getTranscriptionFactorAtRow(int row);

    public List<String> getTooltips();
}
