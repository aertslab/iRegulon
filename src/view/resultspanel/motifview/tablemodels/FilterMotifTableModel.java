package view.resultspanel.motifview.tablemodels;

import view.resultspanel.FilterAttribute;

import javax.swing.table.AbstractTableModel;


import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;
import view.resultspanel.FilterMotifTableModelIF;
import view.resultspanel.MotifTableModel;

import java.util.ArrayList;
import java.util.List;

public class FilterMotifTableModel extends AbstractTableModel implements FilterMotifTableModelIF {
    private static final String FILTER_COLUMN_NAME = "Filtered";
    private static final String FILTER_COLUMN_TOOLTIP = "Check if the filter expression is found in this motif (the motif, target or transcription factor), cross otherwise";
    private static final int FILTER_COLUMN_IMPORTANCE = 3;

	private final MotifTableModel model;
	private String pattern;
	private FilterAttribute filterAttribute;
	
	
	public FilterMotifTableModel(MotifTableModel model, FilterAttribute filterOn, String pattern){
		this.model = model;
		this.filterAttribute = filterOn;
		this.pattern = pattern;
	}
	
	@Override
    public FilterAttribute getFilterAttribute(){
		return this.filterAttribute;
	}
	
	@Override
    public void setFilterAttribute(FilterAttribute filter){
		this.filterAttribute = filter;
		this.fireTableDataChanged();
	}
	
	
	@Override
    public String getPattern(){
		return this.pattern;
	}
	
	@Override
    public void setPattern(String pattern){
		this.pattern = (pattern == null) ? "": pattern;
		this.fireTableDataChanged();
	}

    @Override
	public String getColumnName(int col){
		if (col == 0) return FILTER_COLUMN_NAME;
        else return model.getColumnName(col-1);
	}

	@Override
	public int getRowCount() {
		return this.model.getRowCount();
	}

	@Override
	public int getColumnCount() {
		return this.model.getColumnCount() + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex){
		case 0 : return this.hasPattern(rowIndex);
		default : return this.model.getValueAt(rowIndex, columnIndex-1);
		}
	}

	private boolean hasPattern(int rowIndex){
		Motif motif = this.getMotifAtRow(rowIndex);
		if (this.filterAttribute == FilterAttribute.MOTIF){
			if (motif.getEnrichedMotifID().toLowerCase().contains(this.pattern.toLowerCase())){
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

	@Override
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex){
		case 0: return Boolean.class;
		default: return this.model.getColumnClass(columnIndex - 1);
		}
	}

	@Override
	public Motif getMotifAtRow(int row) {
		return this.model.getMotifAtRow(row);
	}

	@Override
	public List<Integer> getColumnImportances() {
		final List<Integer> importances = new ArrayList<Integer>(model.getColumnImportances());
		importances.add(0, FILTER_COLUMN_IMPORTANCE);
		return importances;
	}

	@Override
	public List<String> getTooltips() {
        final List<String> tooltips = new ArrayList<String>(model.getTooltips());
		tooltips.add(0, FILTER_COLUMN_TOOLTIP);
		return tooltips;
	}
}
