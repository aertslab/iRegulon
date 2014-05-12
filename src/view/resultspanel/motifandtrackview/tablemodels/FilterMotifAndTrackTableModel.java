package view.resultspanel.motifandtrackview.tablemodels;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.CandidateTargetGene;
import domainmodel.TranscriptionFactor;
import view.resultspanel.AbstractFilterMotifAndTrackTableModel;
import view.resultspanel.FilterAttribute;
import view.resultspanel.MotifAndTrackTableModel;

public class FilterMotifAndTrackTableModel extends AbstractFilterMotifAndTrackTableModel {
    public FilterMotifAndTrackTableModel(MotifAndTrackTableModel model, FilterAttribute filterOn, String pattern) {
        super(filterOn, model, pattern);
    }


    @Override
    protected boolean hasPattern(int rowIndex) {
        final AbstractMotifAndTrack motif = this.getMotifOrTrackAtRow(rowIndex);
        if (getFilterAttribute() == FilterAttribute.MOTIF_OR_TRACK) {
            return motif.getName().toLowerCase().contains(getPattern().toLowerCase());
        }

        if (getFilterAttribute() == FilterAttribute.TRANSCRIPTION_FACTOR) {
            for (TranscriptionFactor tf : motif.getTranscriptionFactors()) {
                if (tf.getName().toLowerCase().contains(getPattern().toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        if (getFilterAttribute() == FilterAttribute.TARGET_GENE) {
            for (CandidateTargetGene tg : motif.getCandidateTargetGenes()) {
                if (tg.getGeneName().toLowerCase().contains(getPattern().toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        throw new IllegalStateException();
    }
}
