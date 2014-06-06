package view.resultspanel.motifandtrackclusterview.tablemodels;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import view.resultspanel.CandidateTargetGeneTableModelIF;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.List;


public final class ExtendedCandidateTargetGeneTableModel extends AbstractTableModel implements CandidateTargetGeneTableModelIF {
    private static final String[] COLUMN_NAMES = {"Rank", "#Motifs", "Target Name"};
    private static final String[] COLUMN_TOOLTIPS = {
            "Maximum rank of the target.",
            "Number of motifs this gene is associated with.",
            "Name of the predicted target."};
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 3, 2);

    private final AbstractMotifAndTrack motifOrTrack;

    public ExtendedCandidateTargetGeneTableModel(final AbstractMotifAndTrack motifOrTrack) {
        this.motifOrTrack = motifOrTrack;
    }

    public ExtendedCandidateTargetGeneTableModel() {
        this.motifOrTrack = null;
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    public int getRowCount() {
        return (this.motifOrTrack == null) ? 0 : this.motifOrTrack.getCandidateTargetGenes().size();
    }

    @Override
    public CandidateTargetGene getCandidateTargetGeneAtRow(final int rowIndex) {
        return this.motifOrTrack.getCandidateTargetGenes().get(rowIndex);
    }

    public Object getValueAt(int row, int column) {
        final CandidateTargetGene gene = getCandidateTargetGeneAtRow(row);
        switch (column) {
            case 0:
                return gene.getRank();
            case 1:
                return gene.getNumberOfMotifsOrTracks();
            case 2:
                return gene.getGeneName();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
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
