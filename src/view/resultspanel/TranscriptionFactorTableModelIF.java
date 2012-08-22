package view.resultspanel;

import domainmodel.TranscriptionFactor;

import javax.swing.table.TableModel;


public interface TranscriptionFactorTableModelIF extends TableModel, ColumnImportances {
    TranscriptionFactor getTranscriptionFactorAtRow(int rowIndex);

    String[] getTooltips();
}
