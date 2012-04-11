package domainModel;


public class CandidateTargetGene {

	private GeneIdentifier geneID;
	private int rank;
	
	public CandidateTargetGene(GeneIdentifier geneID, int rank){
		this.geneID = geneID;
		this.rank = rank;
	}
	
	public String getGeneName(){
		return this.geneID.getGeneName();
	}
	
	public SpeciesNomenclature getSpeciesNomenclature(){
		return this.geneID.getSpeciesNomenclature();
	}
	
	public int getRank(){
		return this.rank;
	}
	
	public GeneIdentifier getGeneID(){
		return this.geneID;
	}
	
	
}
