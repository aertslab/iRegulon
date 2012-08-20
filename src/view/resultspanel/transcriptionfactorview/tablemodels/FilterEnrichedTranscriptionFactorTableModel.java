package view.resultspanel.transcriptionfactorview.tablemodels;

import domainmodel.CandidateTargetGene;
import view.resultspanel.AbstractFilterMotifTableModel;
import view.resultspanel.FilterAttribute;


public class FilterEnrichedTranscriptionFactorTableModel extends AbstractFilterMotifTableModel {
    public FilterEnrichedTranscriptionFactorTableModel(BaseEnrichedTranscriptionFactorTableModel model, FilterAttribute filterOn, String pattern){
        super(filterOn, model, pattern);
    }

    @Override
    protected boolean hasPattern(int rowIndex) {
        if (this.filterAttribute == FilterAttribute.MOTIF){
			if (getMotifAtRow(rowIndex).getName().toLowerCase().contains(this.pattern.toLowerCase())){
				return true;
			}
		}
        if (this.filterAttribute == FilterAttribute.TRANSCRIPTION_FACTOR) {
            if (getTranscriptionFactorAtRow(rowIndex).getGeneID().getGeneName().toLowerCase().contains(this.pattern.toLowerCase())) {
                return true;
            }
        }
		if (this.filterAttribute == FilterAttribute.TARGET_GENE){
			for (CandidateTargetGene tg : getMotifAtRow(rowIndex).getCandidateTargetGenes()){
				if (tg.getGeneName().toLowerCase().contains(this.pattern.toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}

}
