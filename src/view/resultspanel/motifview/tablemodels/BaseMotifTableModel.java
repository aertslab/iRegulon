package view.resultspanel.motifview.tablemodels;


import java.util.*;

import javax.swing.table.AbstractTableModel;

import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifTableModel;

public class BaseMotifTableModel extends AbstractTableModel implements MotifTableModel {
    private static final String[] COLUMN_NAMES = {"Rank", "Enriched Motif ID",
            "NES", "AUC", "ClusterCode", "#Targets", "#TFs"};
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 1, 2, 2, 3, 2, 2);
    private static final List<String> COLUMN_TOOLTIPS = Arrays.asList(
            "<html>Rank of the motif.<br/>The motif is ranked using the NEScore.</html>",
            "<html>ID of the motif.</html>",
            "<html>Normalized Enrichment Score of the motif.<br/>The higher the score, the better.</html>",
            "<html>Area Under the Curve.<br/>AUC is used for the calculation of the enrichment score and represents the area under the ROC curve.</html>",
            "<html>The enriched motifs are clustered by similarity and similar motifs that belong to<br/>" +
                  "the same cluster will have the same cluster code and the same background color.<br/>" +
                  "The cluster code is given by order of maximal NES per motif cluster.</html>",
            "<html>Number of unique target genes selected for the given motif.</html>",
            "<html>Number of transcription factors that can be associated by the motif2TF algorithm to the given motif.</html>");

    private final List<Motif> motifs;

    public BaseMotifTableModel(final List<Motif> motifs) {
        this.motifs = motifs;
    }

    public BaseMotifTableModel() {
        this(null);
    }

    public Motif getMotifAtRow(int row) {
        return (this.motifs == null) ? null : this.motifs.get(row);
    }

    @Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int row) {
        final Motif curMotif = getMotifAtRow(row);
        return (curMotif != null) ? curMotif.getBestTranscriptionFactor() : null;
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
                return Integer.class;
            case 5:
                return Integer.class;
            case 6:
                return Integer.class;
        }
        return Object.class;
    }

    public int getRowCount() {
        return (this.motifs == null) ? 0 : this.motifs.size();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public Object getValueAt(int row, int column) {
        final Motif curMotif = this.motifs.get(row);
        switch (column) {
            case 0:
                return curMotif.getRank();
            case 1:
                return curMotif.getName();
            case 2:
                return curMotif.getNEScore();
            case 3:
                return curMotif.getAUCValue();
            case 4:
                return curMotif.getClusterCode();
            case 5:
                final Set<String> set = new HashSet<String>();
                for (CandidateTargetGene tg : curMotif.getCandidateTargetGenes()) {
                    set.add(tg.getGeneName());
                }
                return set.size();
            case 6:
                return curMotif.getTranscriptionFactors().size();
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
