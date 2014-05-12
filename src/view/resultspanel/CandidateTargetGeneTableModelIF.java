package view.resultspanel;

import domainmodel.CandidateTargetGene;

import javax.swing.table.TableModel;


public interface CandidateTargetGeneTableModelIF extends TableModel, ColumnImportances {
    CandidateTargetGene getCandidateTargetGeneAtRow(int rowIndex);

    String[] getTooltips();
}
