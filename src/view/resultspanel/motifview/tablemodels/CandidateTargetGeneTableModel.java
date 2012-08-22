package view.resultspanel.motifview.tablemodels;


import javax.swing.table.AbstractTableModel;

import domainmodel.AbstractMotif;
import domainmodel.CandidateTargetGene;
import view.resultspanel.CandidateTargetGeneTableModelIF;

import java.util.Arrays;
import java.util.List;


public final class CandidateTargetGeneTableModel extends AbstractTableModel implements CandidateTargetGeneTableModelIF {
    private static final String[] COLUMN_NAMES = {"Rank", "Target Name"};
    private static final String[] COLUMN_TOOLTIPS = {"The rank of the target.",
            "The name of the predicted target."};
        private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 2);

    private final AbstractMotif motif;

    public CandidateTargetGeneTableModel(final AbstractMotif motif) {
        this.motif = motif;
    }

    public CandidateTargetGeneTableModel() {
        this.motif = null;
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    public int getRowCount() {
        return (this.motif == null) ? 0 : this.motif.getCandidateTargetGenes().size();
    }

    @Override
    public CandidateTargetGene getCandidateTargetGeneAtRow(final int rowIndex) {
        return this.motif.getCandidateTargetGenes().get(rowIndex);
    }

    public Object getValueAt(int row, int column) {
        final CandidateTargetGene gene = getCandidateTargetGeneAtRow(row);
        switch (column) {
            case 0:
                return gene.getRank();
            case 1:
                return gene.getGeneName();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public String[] getTooltips() {
        return COLUMN_TOOLTIPS;
    }

    public List<Integer> getColumnImportances() {
        return COLUMN_IMPORTANCES;
    }
}
