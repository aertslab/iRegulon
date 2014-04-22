package view.resultspanel.motifandtrackview.tablemodels;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.TranscriptionFactor;
import view.resultspanel.TranscriptionFactorTableModelIF;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.List;

public class TranscriptionFactorTableModel extends AbstractTableModel implements TranscriptionFactorTableModelIF {
    private static String[] COLUMN_NAMES_MOTIF = {
            "Transcription Factor Name",
            "Orthologous Identity",
            "Motif Similarity (FDR)" };
    private static List<Integer> COLUMN_IMPORTANCES_MOTIF = Arrays.asList(2, 3, 3);
    private static String[] COLUMN_TOOLTIPS_MOTIF = {
            "Gene ID of the predicted transcription factor.",
            "Minimum identity between orthologous genes.",
            "Maximum false discovery rate (FDR) on motif similarity." };

    private static String[] COLUMN_NAMES_TRACK = {
            "Transcription Factor Name" };
    private static List<Integer> COLUMN_IMPORTANCES_TRACK = Arrays.asList(2);
    private static String[] COLUMN_TOOLTIPS_TRACK = {
            "Gene ID of the predicted transcription factor." };

    private String[] COLUMN_NAMES;
    private List<Integer> COLUMN_IMPORTANCES;
    private String[] COLUMN_TOOLTIPS;


    private final AbstractMotifAndTrack motifOrTrack;

    public TranscriptionFactorTableModel(final AbstractMotifAndTrack motifOrTrack, final AbstractMotifAndTrack.TrackType trackType) {
        this.motifOrTrack = motifOrTrack;

        if (trackType.equals(AbstractMotifAndTrack.TrackType.TRACK)) {
            COLUMN_NAMES = COLUMN_NAMES_TRACK;
            COLUMN_IMPORTANCES = COLUMN_IMPORTANCES_TRACK;
            COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_TRACK;
        } else {
            COLUMN_NAMES = COLUMN_NAMES_MOTIF;
            COLUMN_IMPORTANCES = COLUMN_IMPORTANCES_MOTIF;
            COLUMN_TOOLTIPS = COLUMN_TOOLTIPS_MOTIF;
        }
    }

    public TranscriptionFactorTableModel() {
        this(null, AbstractMotifAndTrack.TrackType.MOTIF);
    }

    @Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int rowIndex) {
        return (this.motifOrTrack == null) ? null : this.motifOrTrack.getTranscriptionFactors().get(rowIndex);
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public int getRowCount() {
        return (this.motifOrTrack == null) ? 0 : this.motifOrTrack.getTranscriptionFactors().size();
    }

    public String getColumnName(int col) {
        return COLUMN_NAMES[col];
    }

    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return Float.class;
            case 2:
                return Float.class;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public Object getValueAt(int row, int column) {
        final TranscriptionFactor tf = getTranscriptionFactorAtRow(row);
        switch (column) {
            case 0:
                return tf.getGeneID().getGeneName();
            case 1:
                return tf.getMinOrthologousIdentity();
            case 2:
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
