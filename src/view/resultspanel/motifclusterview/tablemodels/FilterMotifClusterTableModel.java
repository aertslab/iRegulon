package view.resultspanel.motifclusterview.tablemodels;

import domainmodel.AbstractMotif;
import domainmodel.CandidateTargetGene;
import domainmodel.MotifCluster;
import view.resultspanel.AbstractFilterMotifTableModel;
import view.resultspanel.FilterAttribute;


public final class FilterMotifClusterTableModel extends AbstractFilterMotifTableModel {
    public FilterMotifClusterTableModel(final BaseMotifClusterTableModel model,
                                        final FilterAttribute filterOn,
                                        final String pattern){
        super(filterOn, model, pattern);
    }

    @Override
    protected boolean hasPattern(final int rowIndex) {
        if (getFilterAttribute() == FilterAttribute.MOTIF) {
            final MotifCluster cluster = (MotifCluster) getMotifAtRow(rowIndex);
            for (final AbstractMotif motif : cluster.getMotifs()) {
                if (motif.getName().toLowerCase().contains(this.pattern.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        if (getFilterAttribute() == FilterAttribute.TRANSCRIPTION_FACTOR) {
            return getTranscriptionFactorAtRow(rowIndex).getName().toLowerCase().contains(getPattern().toLowerCase());
        }

        if (getFilterAttribute() == FilterAttribute.TARGET_GENE) {
            for (final CandidateTargetGene tg : getMotifAtRow(rowIndex).getCandidateTargetGenes()) {
                if (tg.getGeneName().toLowerCase().contains(getPattern().toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        throw new IllegalStateException();
	}
}
