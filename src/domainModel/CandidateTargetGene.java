package domainmodel;


public class CandidateTargetGene implements Comparable<CandidateTargetGene>{

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

	@Override
	/**
	 * @param CandidateTargetGene tg
	 * @return -1 if this targetGene is better than tg
	 * 			0 if both targetGenes are equal
	 * 			1 if tg is better than this targetGene
	 */
	public int compareTo(CandidateTargetGene tg) {
		if (tg.getRank() < this.getRank()){
			//the rank of this is lower.
			return 1;
		}
		if (this.getRank() > tg.getRank()){
			//the rank of this is higher
			return -1;
		}
		if (this.getGeneName().compareToIgnoreCase(tg.getGeneName()) > 0){
			//this is aphabeticaly afther tg (may have a longer name)
			return 1;
		}
		if (this.getGeneName().compareToIgnoreCase(tg.getGeneName()) < 0){
			// this is aphabeticaly before tg (may have a shorter name)
			return -1;
		}
		//the targetgenes are the same
		return 0;
	}
	
	
	
	
}
