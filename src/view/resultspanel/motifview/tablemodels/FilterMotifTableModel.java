package view.resultspanel.motifview.tablemodels;

import view.resultspanel.FilterAttribute;

import javax.swing.table.AbstractTableModel;


import domainmodel.CandidateTargetGene;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

import java.util.ArrayList;
import java.util.List;

public class FilterMotifTableModel extends AbstractTableModel implements MotifTableModel {

	
	private final BaseMotifTableModel model;
	private final String[] m_colNames;
	private String pattern;
	private FilterAttribute filter;
	
	
	public FilterMotifTableModel(BaseMotifTableModel model, FilterAttribute filterOn, String pattern){
		this.model = model;
		String[] colnames = new String[this.model.getColumnCount() +1];
		colnames[0] = "Filtered";
		for (int index=0; index<this.model.getColumnCount(); index++){
			colnames[index + 1] = this.model.getColumnName(index);
		}
		this.m_colNames = colnames;
		this.filter = filterOn;
		this.pattern = pattern;
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
	
	public String getColumnName(int col){
		return this.m_colNames[col];
	}
	
	public boolean hasPattern(int rowIndex){
		Motif motif = this.getMotifAtRow(rowIndex);
		if (this.filter == FilterAttribute.MOTIF){
			if (motif.getEnrichedMotifID().toLowerCase().contains(this.pattern.toLowerCase())){
				return true;
			}
		}
		if (this.filter == FilterAttribute.TRANSCRIPTION_FACTOR){
			for (TranscriptionFactor tf : motif.getTranscriptionFactors()){
				if (tf.getName().toLowerCase().contains(this.pattern.toLowerCase())){
					return true;
				}
			}
		}
		if (this.filter == FilterAttribute.TARGET_GENE){
			for (CandidateTargetGene tg : motif.getCandidateTargetGenes()){
				if (tg.getGeneName().toLowerCase().contains(this.pattern.toLowerCase())){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return true if this model filters on the transcription factor
	 */
	public boolean isFilteringOnTF(){
		return this.filter == FilterAttribute.TRANSCRIPTION_FACTOR;
	}
	
	/**
	 * 
	 * @return true if this model filters on the motif
	 */
	public boolean isFilteringOnMotif(){
		return this.filter == FilterAttribute.MOTIF;
	}
	
	/**
	 * 
	 * @return true if this model filters on the target gene
	 */
	public boolean isFilteringOnTG(){
		return this.filter == FilterAttribute.TARGET_GENE;
	}
	
	/**
	 * 
	 * @return the filter that is used in this model
	 */
	public FilterAttribute getFilter(){
		return this.filter;
	}
	
	/**
	 * @post sets the filtering on the motif
	 */
	public void setFilteringOnMotif(){
		this.filter = FilterAttribute.MOTIF;
		this.fireTableDataChanged();
	}
	
	/**
	 * @post sets the filtering on the transcription factor
	 */
	public void setFilteringOnTF(){
		this.filter = FilterAttribute.TRANSCRIPTION_FACTOR;
		this.fireTableDataChanged();
	}
	
	/**
	 * @post sets the filtering on the target gene
	 */
	public void setFilteringOnTG(){
		this.filter = FilterAttribute.TARGET_GENE;
		this.fireTableDataChanged();
	}
	
	/**
	 * 
	 * @param filter
	 * @post the model will be filtered on the given filter
	 */
	public void setFilterAttribute(FilterAttribute filter){
		this.filter = filter;
		this.fireTableDataChanged();
	}
	
	
	
	/**
	 * 
	 * @return String the pattern that is used to filter the results
	 */
	public String getPattern(){
		return this.pattern;
	}
	
	/**
	 * 
	 * @param pattern
	 * @post the given pattern is the new pattern, used for filtering
	 */
	public void setPattern(String pattern){
		if (pattern == null){
			pattern = "";
		}
		this.pattern = pattern;
		this.fireTableDataChanged();
	}

	@Override
	public Motif getMotifAtRow(int row) {
		return this.model.getMotifAtRow(row);
	}
	
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex){
		case 0: return Boolean.class;
		default: return this.model.getColumnClass(columnIndex - 1);
		}
	}

	@Override
	public List<Integer> getColumnImportances() {
		final List<Integer> importances = new ArrayList<Integer>(model.getColumnImportances());
		importances.add(0, 3);
		return importances;
	}

	@Override
	public String[] getTooltips() {
		String[] tips = this.model.getTooltips();
		String[] newTips = new String[tips.length +1];
		newTips[0] = "Check if the filter expression is found in this motif (the motif, target or transcription factor), cross other wise";
		for (int i=0; i<tips.length; i++){
			newTips[i+1] = tips[i];
		}
		return newTips;
	}
}
