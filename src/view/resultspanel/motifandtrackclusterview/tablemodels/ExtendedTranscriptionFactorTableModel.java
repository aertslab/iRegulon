package view.resultspanel.motifandtrackclusterview.tablemodels;

import domainmodel.*;
import view.resultspanel.TranscriptionFactorTableModelIF;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExtendedTranscriptionFactorTableModel extends AbstractTableModel implements TranscriptionFactorTableModelIF {
    private static String[] COLUMN_NAMES_MOTIF = {
            "Filter",
            "Transcription Factor Name",
            "#Motifs",
            "Orthologous Identity",
            "Motif Similarity (FDR)" };
    private static List<Integer> COLUMN_IMPORTANCES_MOTIF = Arrays.asList(3, 2, 3, 3, 3);
    private static String[] COLUMN_TOOLTIPS_MOTIF = {
            "Is the currently selected motif annotated for this factor?",
            "Gene ID of the predicted transcription factor.",
            "Number of enriched motifs annotated with this factor.",
            "Minimum identity between orthologous genes.",
            "Maximum false discovery rate (FDR) on motif similarity." };

    private static String[] COLUMN_NAMES_TRACK = new String[]{
            "Filter",
            "Transcription Factor Name",
            "#Tracks" };
    private static List<Integer> COLUMN_IMPORTANCES_TRACK = Arrays.asList(3, 2, 3);
    private static String[] COLUMN_TOOLTIPS_TRACK = {
            "Is the currently selected track annotated for this factor?",
            "Gene ID of the associated transcription factor.",
            "Number of enriched tracks annotated with this factor." };

    private String[] COLUMN_NAMES;
    private List<Integer> COLUMN_IMPORTANCES;
    private String[] COLUMN_TOOLTIPS;

    private final AbstractMotifAndTrack motifOrTrackCluster;
    private List<AbstractMotifAndTrack> selectedMotifsAndTracks = Collections.emptyList();

    public ExtendedTranscriptionFactorTableModel(final MotifAndTrackCluster motifOrTrackCluster) {
        this.motifOrTrackCluster = motifOrTrackCluster;

        if (motifOrTrackCluster != null) {
            setSelectedMotifsAndTracks(motifOrTrackCluster.getMotifsAndTracks());
            if (motifOrTrackCluster.getTrackType().equals(AbstractMotifAndTrack.TrackType.MOTIF_CLUSTER)) {
                COLUMN_NAMES = COLUMN_NAMES_MOTIF;
                COLUMN_IMPORTANCES = COLUMN_IMPORTANCES_MOTIF;
                COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_MOTIF;
            } else if (motifOrTrackCluster.getTrackType().equals(AbstractMotifAndTrack.TrackType.TRACK_CLUSTER)) {
                COLUMN_NAMES = COLUMN_NAMES_TRACK;
                COLUMN_IMPORTANCES = COLUMN_IMPORTANCES_TRACK;
                COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_TRACK;
            }
        } else {
            setSelectedMotifsAndTracks(null);
            COLUMN_NAMES = COLUMN_NAMES_MOTIF;
            COLUMN_IMPORTANCES = COLUMN_IMPORTANCES_MOTIF;
            COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_MOTIF;
        }
    }

    public ExtendedTranscriptionFactorTableModel() {
        this(null);
    }

    public List<AbstractMotifAndTrack> getSelectedMotifsAndTracks() {
        return selectedMotifsAndTracks;
    }

    public void setSelectedMotifAndTrack(final AbstractMotifAndTrack motifOrTrack) {
        List<AbstractMotifAndTrack> selectedMotifAndTrack = new ArrayList<AbstractMotifAndTrack>();
        if (motifOrTrack != null) {
            if (motifOrTrack instanceof Motif) {
                Motif motif = (Motif) motifOrTrack;
                for (Motif curMotif : motif.getMotifs()) {
                    selectedMotifAndTrack.add(curMotif);
                }
            } else if (motifOrTrack instanceof Track) {
                Track track = (Track) motifOrTrack;
                for (Track curTrack : track.getTracks()) {
                    selectedMotifAndTrack.add(curTrack);
                }
            } else if (motifOrTrack instanceof MotifAndTrackCluster) {
                MotifAndTrackCluster motifOrTrackCluster = (MotifAndTrackCluster) motifOrTrack;
                selectedMotifAndTrack = motifOrTrackCluster.getMotifsAndTracks();
            }
        }
        if (!selectedMotifAndTrack.isEmpty()) {
            setSelectedMotifsAndTracks(selectedMotifAndTrack);
        } else {
            setSelectedMotifsAndTracks(null);
        }
    }

    public void setSelectedMotifsAndTracks(final List<AbstractMotifAndTrack> motifsOrTracks) {
        if (motifsOrTracks != null) {
            this.selectedMotifsAndTracks = motifsOrTracks;
        } else {
            this.selectedMotifsAndTracks = Collections.emptyList();
        }
    }

    public boolean isAssociatedWith(final TranscriptionFactor tf) {
        for (final AbstractMotifAndTrack motifOrTrack : getSelectedMotifsAndTracks()) {
            for (TranscriptionFactor associatedFactor : motifOrTrack.getTranscriptionFactors()) {
                if (associatedFactor.getGeneID().equals(tf.getGeneID())) return true;
            }
        }
        return false;
    }

    @Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int rowIndex) {
        return (this.motifOrTrackCluster == null) ? null : this.motifOrTrackCluster.getTranscriptionFactors().get(rowIndex);
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return (this.motifOrTrackCluster == null) ? 0 : this.motifOrTrackCluster.getTranscriptionFactors().size();
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return Float.class;
            case 3:
                return Float.class;
            case 4:
                return Float.class;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Object getValueAt(int row, int column) {
        final TranscriptionFactor tf = getTranscriptionFactorAtRow(row);
        switch (column) {
            case 0:
                return isAssociatedWith(tf);
            case 1:
                return tf.getGeneID().getGeneName();
            case 2:
                return (tf.getMotifCount() >= tf.getTrackCount()) ? tf.getMotifCount() : tf.getTrackCount();
            case 3:
                return tf.getMinOrthologousIdentity();
            case 4:
                return tf.getMaxMotifSimilarityFDR();
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
