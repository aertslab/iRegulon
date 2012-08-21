package view.resultspanel.motifclusterview.tablemodels;

import domainmodel.MotifCluster;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifTableModel;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class BaseMotifClusterTableModel extends AbstractTableModel implements MotifTableModel {
    private static final String[] COLUMN_NAMES = {"ClusterCode", "TF", "NES"};
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 1, 1);
    private static final List<String> COLUMN_TOOLTIPS = Arrays.asList("", "", "");

    private final List<MotifCluster> clusters;

    public BaseMotifClusterTableModel(final List<MotifCluster> clusters) {
        if (clusters == null) throw new IllegalArgumentException();
        this.clusters = clusters;
    }

    @Override
    public int getRowCount() {
        return this.clusters.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final MotifCluster cluster = getMotifAtRow(rowIndex);
        switch (columnIndex) {
            case 0:
                return cluster.getClusterCode();
            case 1:
                return cluster.getBestTranscriptionFactor().getName();
            case 2:
                return cluster.getNEScore();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
                return Float.class;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public MotifCluster getMotifAtRow(int rowIndex) {
        return clusters.get(rowIndex);
    }

    @Override
    public TranscriptionFactor getTranscriptionFactorAtRow(final int rowIdx) {
        return clusters.get(rowIdx).getBestTranscriptionFactor();
    }

    @Override
    public List<Integer> getColumnImportances() {
        return COLUMN_IMPORTANCES;
    }

    @Override
    public List<String> getTooltips() {
        return COLUMN_TOOLTIPS;
    }
}
