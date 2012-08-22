package view.resultspanel;

import domainmodel.CandidateTargetGene;

import javax.swing.table.TableModel;

/**
 * Created by IntelliJ IDEA.
 * User: u0043358
 * Date: 22/08/12
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public interface CandidateTargetGeneTableModelIF extends TableModel, ColumnImportances {
    CandidateTargetGene getCandidateTargetGeneAtRow(int rowIndex);

    String[] getTooltips();
}
