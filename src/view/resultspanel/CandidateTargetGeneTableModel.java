package view.resultspanel;


import javax.swing.table.AbstractTableModel;

import domainmodel.AbstractMotif;
import domainmodel.CandidateTargetGene;


public final class CandidateTargetGeneTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {"Rank", "Target Name"};
    private static final String[] COLUMN_TOOLTIPS = {"The rank of the target.",
            "The name of the predicted target."};

    private final AbstractMotif motif;

    public CandidateTargetGeneTableModel(final AbstractMotif motif) {
        this.motif = motif;
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

    public String[] getTooltips() {
        return COLUMN_TOOLTIPS;
    }
}
