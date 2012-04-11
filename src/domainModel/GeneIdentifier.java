package domainModel;


public class GeneIdentifier implements Comparable<GeneIdentifier>{

	private final String geneName;
	private final SpeciesNomenclature speciesNomenclature;
	
	public GeneIdentifier(String geneName, SpeciesNomenclature speciesNomenclature){
		this.geneName = geneName;
		this.speciesNomenclature = speciesNomenclature;
	}
	
	/**
	 * 
	 * @return the gene name of this gene identifier
	 */
	public String getGeneName(){
		return this.geneName;
	}
	
	/**
	 * 
	 * @return the species and nomenclature of this geneID
	 */
	public SpeciesNomenclature getSpeciesNomenclature(){
		return this.speciesNomenclature;
	}
	
	public int compareTo(GeneIdentifier o) {
		return -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((geneName == null) ? 0 : geneName.hashCode());
		result = prime
				* result
				+ ((speciesNomenclature == null) ? 0 : speciesNomenclature
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeneIdentifier other = (GeneIdentifier) obj;
		if (geneName == null) {
			if (other.geneName != null)
				return false;
		} else if (!geneName.equals(other.geneName))
			return false;
		if (speciesNomenclature != other.speciesNomenclature)
			return false;
		return true;
	}
	
	
	
	
}
