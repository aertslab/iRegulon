package view.resultspanel.motifview.tablemodels;

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
		final Motif motif = this.getMotifAtRow(rowIndex);
		if (this.filterAttribute == FilterAttribute.MOTIF){
			if (motif.getName().toLowerCase().contains(this.pattern.toLowerCase())){
				return true;
			}
		}
		if (this.filterAttribute == FilterAttribute.TRANSCRIPTION_FACTOR){
			for (TranscriptionFactor tf : motif.getTranscriptionFactors()){
				if (tf.getName().toLowerCase().contains(this.pattern.toLowerCase())){
					return true;
				}
			}
		}
		if (this.filterAttribute == FilterAttribute.TARGET_GENE){
			for (CandidateTargetGene tg : motif.getCandidateTargetGenes()){
				if (tg.getGeneName().toLowerCase().contains(this.pattern.toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}
}
