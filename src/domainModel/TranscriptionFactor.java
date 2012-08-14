package domainmodel;


public final class TranscriptionFactor implements Comparable<TranscriptionFactor> {
	private final GeneIdentifier geneID;
	private final float minOrthologousIdentity;
	private final float maxMotifSimilarityFDR;
	private final String similarMotifName;
	private final String similarMotifDescription;
	private final String orthologousGeneName;
	private final String orthologousSpecies;
	
	public TranscriptionFactor(GeneIdentifier geneID, float minOrthologousIdentity,
			float maxMotifSimilarityFDR, String similarMotifName, String similarMotifDescription,
			String orthologousGeneName, String orthologousSpecies){
		this.geneID = geneID;

		this.minOrthologousIdentity = orthologousGeneName == null ? Float.NaN : minOrthologousIdentity;
        this.orthologousGeneName = orthologousGeneName;
        this.orthologousSpecies = orthologousSpecies;

		this.maxMotifSimilarityFDR = similarMotifName == null ? Float.NaN : maxMotifSimilarityFDR;
		this.similarMotifName = similarMotifName;
		this.similarMotifDescription = similarMotifDescription;
	}
	
	public String getName(){
		return this.geneID.getGeneName();
	}
	
	public SpeciesNomenclature getSpeciesNomeclature(){
		return this.geneID.getSpeciesNomenclature();
	}
	
	public GeneIdentifier getGeneID() {
		return this.geneID;
	}
	
	public float getMinOrthologousIdentity(){
		return this.minOrthologousIdentity;
	}
	
	public float getMaxMotifSimilarityFDR(){
		return this.maxMotifSimilarityFDR;
	}
	
	public String getSimilarMotifName(){
		return this.similarMotifName;
	}
	
	public String getSimilarMotifDescription(){
		return this.similarMotifDescription;
	}
	
	public String getOrthologousGeneName(){
		return this.orthologousGeneName;
	}
	
	public String getOrthologousSpecies(){
		return this.orthologousSpecies;
	}

    private static int compareFloat(final float f1, final float f2, final boolean reverse) {
        if (Double.isNaN(f1) && Double.isNaN(f2)) {
            return 0;
        } else if (Double.isNaN(f1)) {
            return -1;
        } else {
            return reverse ? Float.compare(f2, f1) : Float.compare(f1, f2);
        }
    }

    @Override
	public int compareTo(TranscriptionFactor other) {
        int r = compareFloat(this.getMaxMotifSimilarityFDR(), other.getMaxMotifSimilarityFDR(), false);
        if (r != 0) return r;
        r = compareFloat(this.getMinOrthologousIdentity(), other.getMinOrthologousIdentity(), true);
        if (r != 0) return r;
        return this.getName().toLowerCase().compareToIgnoreCase(other.getName().toLowerCase());
	}

    @Override
	public String toString() {
		return this.getName();
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranscriptionFactor that = (TranscriptionFactor) o;

        if (Float.compare(that.maxMotifSimilarityFDR, maxMotifSimilarityFDR) != 0) return false;
        if (Float.compare(that.minOrthologousIdentity, minOrthologousIdentity) != 0) return false;
        if (!geneID.equals(that.geneID)) return false;
        if (orthologousGeneName != null ? !orthologousGeneName.equals(that.orthologousGeneName) : that.orthologousGeneName != null)
            return false;
        if (orthologousSpecies != null ? !orthologousSpecies.equals(that.orthologousSpecies) : that.orthologousSpecies != null)
            return false;
        if (similarMotifDescription != null ? !similarMotifDescription.equals(that.similarMotifDescription) : that.similarMotifDescription != null)
            return false;
        if (similarMotifName != null ? !similarMotifName.equals(that.similarMotifName) : that.similarMotifName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = geneID.hashCode();
        result = 31 * result + (minOrthologousIdentity != +0.0f ? Float.floatToIntBits(minOrthologousIdentity) : 0);
        result = 31 * result + (maxMotifSimilarityFDR != +0.0f ? Float.floatToIntBits(maxMotifSimilarityFDR) : 0);
        result = 31 * result + (similarMotifName != null ? similarMotifName.hashCode() : 0);
        result = 31 * result + (similarMotifDescription != null ? similarMotifDescription.hashCode() : 0);
        result = 31 * result + (orthologousGeneName != null ? orthologousGeneName.hashCode() : 0);
        result = 31 * result + (orthologousSpecies != null ? orthologousSpecies.hashCode() : 0);
        return result;
    }
}
