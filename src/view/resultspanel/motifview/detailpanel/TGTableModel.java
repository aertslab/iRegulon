package view.resultspanel.motifview.detailpanel;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import domainmodel.AbstractMotif;
import domainmodel.CandidateTargetGene;
import domainmodel.Motif;

public class TGTableModel extends AbstractTableModel{

	private final AbstractMotif motif;
	private static final int NR_OF_COLUMNS = 2;
	private final String[] m_colNames = {"Rank", "Target Name"};
	private final String[] toolTipsCol = {"The rank of the target.",
					"The name of the predicted target."};
	
	public TGTableModel(AbstractMotif motif){
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
		  case 5: return this.getRegulatoryLinkAtRow(row).getMaxMotifSimilarityFDR();
		  }
		  return null;
		  */
		  /*if (this.regulatoryTree == null){
			  return "?";
		  }*/
		  List<CandidateTargetGene> geneIDs = this.motif.getCandidateTargetGenes();
		  switch (column){
		  case 0 : return geneIDs.get(row).getRank();
		  case 1 : return geneIDs.get(row).getGeneName();
		  }
		  return null;
	  }
	  
	  
	  public String getColumnName(int col) {
	      return m_colNames[col];
	   }
	  
	  public String[] getTooltips(){
		  return this.toolTipsCol;
	  }
	  
}
