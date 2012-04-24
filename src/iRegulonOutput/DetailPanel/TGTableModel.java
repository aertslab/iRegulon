package iRegulonOutput.DetailPanel;

import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import domainModel.CandidateTargetGene;
import domainModel.Motif;

public class TGTableModel extends AbstractTableModel{

	private final Motif motif;
	private static final int NR_OF_COLUMNS = 2;
	private final String[] m_colNames = {"Rank", "Target Name"};
	
	public TGTableModel(Motif motif){
		this.motif = motif;
	}
	
	public String[] getColumnNames(){
		return this.m_colNames;
	}
	
	  public int getColumnCount() {
		 return NR_OF_COLUMNS; 
	  }
	  
	  public int getRowCount() {
		  if (this.motif == null){
			  return 0;
		  }
		 return this.motif.getCandidateTargetGenes().size();
	  }
	  
	  
	  public Object getValueAt(int row, int column) {
		  //final RegulatoryTree curRegulon = this.regulatoryTreeList.get(row);
		 /* final RegulatoryTree curRegulon = this.getRegulatoryTreeAtRow(row);
		  switch (column) {
		  case 0: if (curRegulon.isEdgeDirectedFromParentToChildren()){
			  			return curRegulon.getParentName();
		  			}else{
		  				return this.getRegulatoryLinkAtRow(row).getGeneName();
		  			}
		  case 1: if (curRegulon.isEdgeDirectedFromParentToChildren()){
			  			return this.getRegulatoryLinkAtRow(row).getGeneName();
					}else{
						return curRegulon.getParentName();
					}
		  case 2: return this.getRegulatoryLinkAtRow(row).getNEScore();
		  case 3: return this.getRegulatoryLinkAtRow(row).getEnrichedMotif();
		  case 4: return this.getRegulatoryLinkAtRow(row).getOrthologousIdentity();
		  case 5: return this.getRegulatoryLinkAtRow(row).getMotifSimilarityFDR();
		  }
		  return null;
		  */
		  /*if (this.regulatoryTree == null){
			  return "?";
		  }*/
		  Collection<CandidateTargetGene> geneIDs = this.motif.getCandidateTargetGenes();
		  CandidateTargetGene[] geneArray = new CandidateTargetGene[geneIDs.size()];
		  int index = 0;
		  for (CandidateTargetGene gene : geneIDs){
			  geneArray[index] = gene;
			  ++index;
		  }
		  switch (column){
		  case 0 : return geneArray[row].getRank();
		  case 1 : return geneArray[row].getGeneName();
		  }
		  return null;
	  }
	  
	  
	  public String getColumnName(int col) {
	      return m_colNames[col];
	   }
	  
}
