package domainModel;


public class TranscriptionFactor implements Comparable<TranscriptionFactor>{

	private GeneIdentifier geneID;
	private float orthologousIdentifier;
	private float motifSimilarityFDR;
	private String similarMotifName;
	private String similarMotifDescription;
	private String orthologousGeneName;
	private String orthologousSpecies;
	
	
	public TranscriptionFactor(GeneIdentifier geneID, float orthologousIdentifier, 
			float motifSimilarityFDR, String similarMotifName, String similarMotifDescription,
			String ortholgousGeneName, String ortholgousSpecies){
		this.geneID = geneID;
		this.orthologousIdentifier = orthologousIdentifier;
		this.motifSimilarityFDR = motifSimilarityFDR;
		this.similarMotifName = similarMotifName;
		this.similarMotifDescription = similarMotifDescription;
		this.orthologousGeneName = ortholgousGeneName;
		this.orthologousSpecies = ortholgousSpecies;
	}
	
	public String getName(){
		return this.geneID.getGeneName();
	}
	
	public SpeciesNomenclature getSpeciesNomeclature(){
		return this.geneID.getSpeciesNomenclature();
	}
	
	public GeneIdentifier getGeneID(){
		return this.geneID;
	}
	
	public float getOrthologousIdentifier(){
		return this.orthologousIdentifier;
	}
	
	public float getMotifSimilarityFDR(){
		return this.motifSimilarityFDR;
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
	
	/**
	 * @param TranscriptionFactor tf
	 * @return -1 if this transcriptionfactor is better than tf
	 * 			0 if both transcriptionfactors are equal
	 * 			1 if tf is better than this transcriptionfactor
	 */
	public int compareTo(TranscriptionFactor tf){
		if (Float.isNaN(tf.getOrthologousIdentifier()) && 
				Float.isNaN(this.getOrthologousIdentifier())){
			if(tf.getMotifSimilarityFDR() == this.getMotifSimilarityFDR()){
				return 0;
			}
			if (Float.isNaN(tf.getMotifSimilarityFDR())){
				return 1;
			}
			if (Float.isNaN(this.getMotifSimilarityFDR())){
				return -1;
			}
			if (tf.getMotifSimilarityFDR() < this.getMotifSimilarityFDR()){
				return -1;
			}
			return 1;
		}
		if (Float.isNaN(tf.getOrthologousIdentifier())){
			return 1;
		}
		if (Float.isNaN(this.getOrthologousIdentifier())){
			return -1;
		}
		if (tf.getOrthologousIdentifier() > this.getOrthologousIdentifier()){
			return 1;
		}
		if (tf.getOrthologousIdentifier() < this.getOrthologousIdentifier()){
			return -1;
		}
		if (tf.getOrthologousIdentifier() == this.getOrthologousIdentifier()){
			if(tf.getMotifSimilarityFDR() == this.getMotifSimilarityFDR()){
				return 0;
			}
			if (Float.isNaN(tf.getMotifSimilarityFDR())){
				return -1;
			}
			if (Float.isNaN(this.getMotifSimilarityFDR())){
				return 1;
			}
			if (tf.getMotifSimilarityFDR() < this.getMotifSimilarityFDR()){
				return 1;
			}
		}
		return -1;
	}
	
	public String toString(){
		return this.getName();
	}
	
}
