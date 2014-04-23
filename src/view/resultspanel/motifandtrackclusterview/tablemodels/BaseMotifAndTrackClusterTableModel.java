package view.resultspanel.motifandtrackclusterview.tablemodels;

import domainmodel.MotifAndTrackCluster;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifAndTrackTableModel;

import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public final class BaseMotifAndTrackClusterTableModel extends AbstractTableModel implements MotifAndTrackTableModel {
    private static final String[] COLUMN_NAMES = { "ClusterCode", "TF", "NES", "#Targets", "#Motifs/Tracks", "ClusterNumber" };
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 1, 1, 1, 1, 0);
    private static final List<String> COLUMN_TOOLTIPS = Arrays.asList(
            "Each cluster code contains enriched motifs that are clustered by similarity or contains enriched tracks that are clustered by transcription factor name.",
            "ID of the transcription factor.",
            "The highest enrichment score for a motif/track that is part of the current clustercode.",
            "Number of unique target genes detected by the enriched motifs/tracks associated to the given transcription factor (UNION).",
            "Number of motifs/tracks that can be associated to the given transcription factor.",
            "Cluster number for coloring the clusters.");

    private final List<MotifAndTrackCluster> clusters;

    public BaseMotifAndTrackClusterTableModel(final List<MotifAndTrackCluster> clusters) {
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
        final MotifAndTrackCluster cluster = getMotifOrTrackAtRow(rowIndex);
        switch (columnIndex) {
            case 0:
                return cluster.getClusterCode();
            case 1:
                return cluster.getBestTranscriptionFactor().getName();
            case 2:
                return cluster.getNEScore();
            case 3:
                return cluster.getCandidateTargetGenes().size();
            case 4:
                return cluster.getMotifsAndTracks().size();
            case 5:
                return cluster.getClusterNumber();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Float.class;
            case 3:
                return Integer.class;
            case 4:
                return Integer.class;
            case 5:
                return Integer.class;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public String getColumnName(int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public MotifAndTrackCluster getMotifOrTrackAtRow(int rowIndex) {
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
