package view.resultspanel.motifclusterview.tablemodels;

import domainmodel.AbstractMotif;
import domainmodel.Motif;
import domainmodel.MotifCluster;
import domainmodel.TranscriptionFactor;
import view.resultspanel.TranscriptionFactorTableModelIF;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExtendedTranscriptionFactorTableModel extends AbstractTableModel implements TranscriptionFactorTableModelIF {
	private static final String[] COLUMN_NAMES = {
            "Filter",
            "Transcription Factor Name",
            "#Motifs",
            "Orthologous Identity",
            "Motif Similarity (FDR)" };
    private static final List<Integer> COLUMN_IMPORTANCES = Arrays.asList(3, 2, 3, 3, 3);
	private static final String[] COLUMN_TOOLTIPS = {
            "Is the currently selected motif annotated for this factor?",
            "Gene ID of the predicted transcription factor.",
            "Number of enriched motifs annotated with this factor.",
			"Minimum identity between orthologous genes.",
			"Maximum false discovery rate (FDR) on motif similarity."};

    private final AbstractMotif motif;
    private List<Motif> selectedMotifs = Collections.emptyList();

	public ExtendedTranscriptionFactorTableModel(final MotifCluster motif) {
		this.motif = motif;
        setSelectedMotifs(motif != null ? motif.getMotifs() : null);
	}

    public ExtendedTranscriptionFactorTableModel() {
		this(null);
	}

    public List<Motif> getSelectedMotifs() {
        return selectedMotifs;
    }

    public void setSelectedMotif(final AbstractMotif motif) {
        setSelectedMotifs(motif == null ? null : motif.getMotifs());
    }

    public void setSelectedMotifs(final List<Motif> motifs) {
         this.selectedMotifs = (motifs != null) ? motifs : Collections.<Motif>emptyList();
    }

    public boolean isAssociatedWith(final TranscriptionFactor tf) {
        for (final Motif motif: getSelectedMotifs()) {
            for (TranscriptionFactor associatedFactor : motif.getTranscriptionFactors()) {
                if (associatedFactor.getGeneID().equals(tf.getGeneID())) return true;
            }
        }
        return false;
    }

	@Override
    public TranscriptionFactor getTranscriptionFactorAtRow(int rowIndex) {
		return (this.motif == null) ? null : this.motif.getTranscriptionFactors().get(rowIndex);
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public int getRowCount() {
		return (this.motif == null) ? 0 : this.motif.getTranscriptionFactors().size();
	}

	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
	}

    public Class<?> getColumnClass(int columnIndex) {
    	switch (columnIndex){
    	case 0 : return Boolean.class;
    	case 1 : return String.class;
        case 2 : return Float.class;
    	case 3 : return Float.class;
    	case 4 : return Float.class;
        default: throw new IndexOutOfBoundsException();
    	}
    }

    public Object getValueAt(int row, int column) {
        final TranscriptionFactor tf = getTranscriptionFactorAtRow(row);
        switch (column){
            case 0 : return isAssociatedWith(tf);
            case 1 : return tf.getGeneID().getGeneName();
            case 2 : return tf.getMotifCount();
            case 3 : return tf.getMinOrthologousIdentity();
            case 4 : return tf.getMaxMotifSimilarityFDR();
            default: throw new IndexOutOfBoundsException();
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
