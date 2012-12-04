package domainmodel;


public class GeneIdentifier implements Comparable<GeneIdentifier>{
	private final String geneName;
	private final SpeciesNomenclature speciesNomenclature;
	
	public GeneIdentifier(String geneName, SpeciesNomenclature speciesNomenclature) {
        if (geneName == null || speciesNomenclature == null) {
            throw new IllegalArgumentException();
        }
		this.geneName = geneName;
		this.speciesNomenclature = speciesNomenclature;
	}
	
	public String getGeneName(){
		return this.geneName;
	}
	
	public SpeciesNomenclature getSpeciesNomenclature(){
		return this.speciesNomenclature;
	}
	
	public int compareTo(GeneIdentifier o) {
        final int r = speciesNomenclature.getName().compareTo(o.getSpeciesNomenclature().getName());
        if (r != 0) return r;
		return geneName.compareTo(o.getGeneName());
	}

    public String toString() {
        return geneName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneIdentifier that = (GeneIdentifier) o;

        if (!geneName.equals(that.geneName)) return false;
        if (!speciesNomenclature.equals(that.speciesNomenclature)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = geneName.hashCode();
        result = 31 * result + speciesNomenclature.hashCode();
        return result;
    }
}
