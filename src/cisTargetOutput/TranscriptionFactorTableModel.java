package cisTargetOutput;

import java.text.DecimalFormat;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import domainModel.CandidateTargetGene;
import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class TranscriptionFactorTableModel extends AbstractTableModel{

	private final Motif motif;
	private static final int NR_OF_COLUMNS = 3;
	private final String[] m_colNames = {"Transcription Factor Name", "Orthologous Identifier", "Motif Similarity FDR"};
	
	public TranscriptionFactorTableModel(Motif motif){
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
		return this.motif.getTranscriptionFactors().size();
	}
	
	public TranscriptionFactor getTranscriptionFactorAtRow(int row) {
		Collection<TranscriptionFactor> tfCollection = this.motif.getTranscriptionFactors();
		TranscriptionFactor[] tfArray = new TranscriptionFactor[tfCollection.size()];
		this.motif.getTranscriptionFactors().toArray(tfArray);
		//System.out.println(tfArray[row]);
		return tfArray[row];
	}
	
    public Class<?> getColumnClass(int columnIndex) {
    	switch (columnIndex){
    	case 0 : return String.class;
    	case 1 : return Float.class;
    	case 2 : return Float.class;
    	}
        return Object.class;
    }
	
	
	public Object getValueAt(int row, int column) {
		  Collection<TranscriptionFactor> tfCollection = this.motif.getTranscriptionFactors();
		  TranscriptionFactor[] tfArray = new TranscriptionFactor[tfCollection.size()];
		  int index = 0;
		  for (TranscriptionFactor tf : tfCollection){
			  tfArray[index] = tf;
			  ++index;
		  }
		  switch (column){
		  	case 0 : return (String) tfArray[row].getGeneID().getGeneName();
		  	case 1 : return (Float) tfArray[row].getOrthologousIdentifier();
		  	case 2 : return (Float) tfArray[row].getMotifSimilarityFDR();
		  }
		  return null;
	}
	
	
	public String getColumnName(int col) {
		return m_colNames[col];
	}

}
