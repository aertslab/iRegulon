package view.resultspanel.motifandtrackview.tablemodels;


import domainmodel.*;
import view.resultspanel.MotifAndTrackTableModel;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseMotifAndTrackTableModel extends AbstractTableModel implements MotifAndTrackTableModel {
    private final String[] COLUMN_NAMES_MOTIF = {"Rank", "Enriched Motif ID", "NES", "AUC", "ClusterCode", "#Targets", "#TFs", "ClusterNumber"};
    private final List<String> COLUMN_TOOLTIPS_MOTIF = Arrays.asList(
            "<html>Rank of the motif.<br/>The motif is ranked using the NEScore.</html>",
            "<html>ID of the motif.</html>",
            "<html>Normalized Enrichment Score of the motif.<br/>The higher the score, the better.</html>",
            "<html>Area Under the Curve.<br/>AUC is used for the calculation of the enrichment score and represents the area under the ROC curve.</html>",
            "<html>The enriched motifs are clustered by similarity and similar motifs that belong to<br/>" +
                    "the same cluster will have the same cluster code and the same background color.<br/>" +
                    "The cluster code is given by order of maximal NES per motif cluster.</html>",
            "<html>Number of unique target genes selected for the given motif.</html>",
            "<html>Number of transcription factors that can be associated by the motif2TF algorithm to the given motif.</html>",
            "<html>Cluster number for coloring the clusters.</html>");

    private final String[] COLUMN_NAMES_TRACK = {"Rank", "Enriched Track ID", "NES", "AUC", "ClusterCode", "#Targets", "#TFs", "ClusterNumber"};
    private final List<String> COLUMN_TOOLTIPS_TRACK = Arrays.asList(
            "<html>Rank of the track.<br/>The track is ranked using the NEScore.</html>",
            "<html>ID of the track.</html>",
            "<html>Normalized Enrichment Score of the track.<br/>The higher the score, the better.</html>",
            "<html>Area Under the Curve.<br/>AUC is used for the calculation of the enrichment score and represents the area under the ROC curve.</html>",
            "<html>The enriched track are clustered by transcription factor name.</html>",
            "<html>Number of unique target genes selected for the given track.</html>",
            "<html>Number of transcription factors that can be assigned to the current track.</html>",
            "<html>Cluster number for coloring the clusters.</html>");

    private String[] COLUMN_NAMES;
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 1, 2, 2, 3, 2, 2, 0);
    private List<String> COLUMN_TOOLTIPS;

    private final List<AbstractMotifAndTrack> motifsOrTracks;

    public BaseMotifAndTrackTableModel(final List<AbstractMotifAndTrack> motifsOrTracks, AbstractMotifAndTrack.TrackType trackType) {
        this.motifsOrTracks = motifsOrTracks;
        if (trackType.equals(AbstractMotifAndTrack.TrackType.MOTIF) || trackType.equals(AbstractMotifAndTrack.TrackType.MOTIF_CLUSTER)) {
            COLUMN_NAMES = COLUMN_NAMES_MOTIF;
            COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_MOTIF;
        } else if (trackType.equals(AbstractMotifAndTrack.TrackType.TRACK) || trackType.equals(AbstractMotifAndTrack.TrackType.TRACK_CLUSTER)) {
            COLUMN_NAMES = COLUMN_NAMES_TRACK;
            COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_TRACK;
        }
    }

    public BaseMotifAndTrackTableModel() {
        this(null, AbstractMotifAndTrack.TrackType.MOTIF);
    }

    public AbstractMotifAndTrack getMotifOrTrackAtRow(int row) {
        return (this.motifsOrTracks == null) ? null : this.motifsOrTracks.get(row);
    }

    @Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int row) {
        final AbstractMotifAndTrack curMotifOrTrack = getMotifOrTrackAtRow(row);
        return (curMotifOrTrack != null) ? curMotifOrTrack.getBestTranscriptionFactor() : null;
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
                return Float.class;
            case 3:
                return Float.class;
            case 4:
                return String.class;
            case 5:
                return Integer.class;
            case 6:
                return Integer.class;
            case 7:
                return Integer.class;
        }
        return Object.class;
    }

    public int getRowCount() {
        return (this.motifsOrTracks == null) ? 0 : this.motifsOrTracks.size();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public Object getValueAt(int row, int column) {
        final AbstractMotifAndTrack curMotifOrTrack = this.motifsOrTracks.get(row);
        int rank = 65535;
        if (curMotifOrTrack.isMotif()) {
            Motif motif = (Motif) curMotifOrTrack;
            rank = motif.getRank();
        } else if (curMotifOrTrack.isTrack()) {
            Track track = (Track) curMotifOrTrack;
            rank = track.getRank();
        }
        switch (column) {
            case 0:
                return rank;
            case 1:
                return curMotifOrTrack.getName();
            case 2:
                return curMotifOrTrack.getNEScore();
            case 3:
                return curMotifOrTrack.getAUCValue();
            case 4:
                return curMotifOrTrack.getClusterCode();
            case 5:
                final Set<String> set = new HashSet<String>();
                for (CandidateTargetGene tg : curMotifOrTrack.getCandidateTargetGenes()) {
                    set.add(tg.getGeneName());
                }
                return set.size();
            case 6:
                return curMotifOrTrack.getTranscriptionFactors().size();
            case 7:
                return curMotifOrTrack.getClusterNumber();
        }
        return null;
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    public List<Integer> getColumnImportances() {
        return COLUMN_IMPORTANCES;
    }

    @Override
    public List<String> getTooltips() {
        return COLUMN_TOOLTIPS;
    }
}
