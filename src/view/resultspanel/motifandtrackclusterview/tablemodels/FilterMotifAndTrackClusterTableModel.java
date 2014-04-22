package view.resultspanel.motifandtrackclusterview.tablemodels;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import domainmodel.MotifAndTrackCluster;
import view.resultspanel.AbstractFilterMotifAndTrackTableModel;
import view.resultspanel.FilterAttribute;


public final class FilterMotifAndTrackClusterTableModel extends AbstractFilterMotifAndTrackTableModel {
    public FilterMotifAndTrackClusterTableModel(final BaseMotifAndTrackClusterTableModel model,
                                                final FilterAttribute filterOn,
                                                final String pattern){
        super(filterOn, model, pattern);
    }

    @Override
    protected boolean hasPattern(final int rowIndex) {
        if (getFilterAttribute() == FilterAttribute.MOTIF_OR_TRACK) {
            final MotifAndTrackCluster cluster = (MotifAndTrackCluster) getMotifOrTrackAtRow(rowIndex);
            for (final AbstractMotifAndTrack motifOrTrack : cluster.getMotifsAndTracks()) {
                if (motifOrTrack.getName().toLowerCase().contains(this.pattern.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        if (getFilterAttribute() == FilterAttribute.TRANSCRIPTION_FACTOR) {
            return getTranscriptionFactorAtRow(rowIndex).getName().toLowerCase().contains(getPattern().toLowerCase());
        }

        if (getFilterAttribute() == FilterAttribute.TARGET_GENE) {
            for (final CandidateTargetGene tg : getMotifOrTrackAtRow(rowIndex).getCandidateTargetGenes()) {
                if (tg.getGeneName().toLowerCase().contains(getPattern().toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        throw new IllegalStateException();
	}
}
