package iRegulonOutput.MotifTableModels;

import iRegulonOutput.FilteringOn;

import javax.swing.table.AbstractTableModel;


import domainModel.CandidateTargetGene;
import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class FilteredMotifModel extends AbstractTableModel implements GlobalMotifTableModel{

	
	private final MotifTableModel model;
	private final String[] m_colNames;
	private String pattern;
	private FilteringOn filter;
	
	
	public FilteredMotifModel(MotifTableModel model, FilteringOn filterOn, String pattern){
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
		Motif motif = this.getRegulatoryTreeAtRow(rowIndex);
		if (this.filter == FilteringOn.MOTIF){
			if (motif.getEnrichedMotifID().toLowerCase().contains(this.pattern.toLowerCase())){
				return true;
			}
		}
		if (this.filter == FilteringOn.TRANSCRIPTION_FACTOR){
			for (TranscriptionFactor tf : motif.getTranscriptionFactors()){
				if (tf.getName().toLowerCase().contains(this.pattern.toLowerCase())){
					return true;
				}
			}
		}
		if (this.filter == FilteringOn.TARGET_GENE){
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
		return this.filter == FilteringOn.TRANSCRIPTION_FACTOR;
	}
	
	/**
	 * 
	 * @return true if this model filters on the motif
	 */
	public boolean isFilteringOnMotif(){
		return this.filter == FilteringOn.MOTIF;
	}
	
	/**
	 * 
	 * @return true if this model filters on the target gene
	 */
	public boolean isFilteringOnTG(){
		return this.filter == FilteringOn.TARGET_GENE;
	}
	
	/**
	 * 
	 * @return the filter that is used in this model
	 */
	public FilteringOn getFilter(){
		return this.filter;
	}
	
	/**
	 * @post sets the filtering on the motif
	 */
	public void setFilteringOnMotif(){
		this.filter = FilteringOn.MOTIF;
		this.fireTableDataChanged();
	}
	
	/**
	 * @post sets the filtering on the transcription factor
	 */
	public void setFilteringOnTF(){
		this.filter = FilteringOn.TRANSCRIPTION_FACTOR;
		this.fireTableDataChanged();
	}
	
	/**
	 * @post sets the filtering on the target gene
	 */
	public void setFilteringOnTG(){
		this.filter = FilteringOn.TARGET_GENE;
		this.fireTableDataChanged();
	}
	
	/**
	 * 
	 * @param filter
	 * @post the model will be filtered on the given filter
	 */
	public void setFilteringOn(FilteringOn filter){
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
	public Motif getRegulatoryTreeAtRow(int row) {
		return this.model.getRegulatoryTreeAtRow(row);
	}
	
	public Class<?> getColumnClass(int columnIndex){
		switch(columnIndex){
		case 0: return Boolean.class;
		default: return this.model.getColumnClass(columnIndex - 1);
		}
	}

	@Override
	public int[] getCollumnImportance() {
		int[] imp = this.model.getCollumnImportance();
		int[] newImp = new int[imp.length +1];
		newImp[0] = 3;
		for (int i=0; i < imp.length; i++){
			newImp[i+1] = imp[i];
		}
		return newImp;
	}
	
	

}
