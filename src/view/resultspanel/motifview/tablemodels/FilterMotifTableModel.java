package view.resultspanel.motifview.tablemodels;

import domainmodel.AbstractMotif;
import view.resultspanel.AbstractFilterMotifTableModel;
import view.resultspanel.FilterAttribute;


import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;
import view.resultspanel.MotifTableModel;

public class FilterMotifTableModel extends AbstractFilterMotifTableModel {
    public FilterMotifTableModel(MotifTableModel model, FilterAttribute filterOn, String pattern){
        super(filterOn, model, pattern);
    }


    @Override
    protected boolean hasPattern(int rowIndex){
		final AbstractMotif motif = this.getMotifAtRow(rowIndex);
		if (getFilterAttribute() == FilterAttribute.MOTIF){
			return motif.getName().toLowerCase().contains(getPattern().toLowerCase());
		}

		if (getFilterAttribute() == FilterAttribute.TRANSCRIPTION_FACTOR){
			for (TranscriptionFactor tf : motif.getTranscriptionFactors()){
				if (tf.getName().toLowerCase().contains(getPattern().toLowerCase())){
					return true;
				}
			}
            return false;
		}

		if (getFilterAttribute() == FilterAttribute.TARGET_GENE){
			for (CandidateTargetGene tg : motif.getCandidateTargetGenes()){
				if (tg.getGeneName().toLowerCase().contains(getPattern().toLowerCase())){
					return true;
				}
			}
            return false;
		}

		throw new IllegalStateException();
	}
}
