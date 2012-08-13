package domainmodel;

import iRegulonInput.IRegulonType;

import java.util.Collection;


public class Results {

	private Collection<Motif> motifs;
	private Input inputParameters;
	
	public Results(Collection<Motif> motifs, Input inputParameters){
		this.motifs = motifs;
		this.inputParameters = inputParameters;
	}
	
	public Collection<Motif> getMotifs(){
		return this.motifs;
	}
	
	public boolean hasParameters(){
		return (this.inputParameters != null);
	}
	
	/**
	 * 
	 * @return an array of all the nodes selected as input
	 */
	public Collection<GeneIdentifier> getGenes(){
		return this.inputParameters.getGenes();
	}
	
	/**
	 * 
	 * @return the Enrichment score threshold
	 */
	public float getEScore(){
		return this.inputParameters.getEScore();
	}
	
	/**
	 * 
	 * @return the x-axis cut-off for visualisation
	 */
	public int getThresholdForVisualisation(){
		return this.inputParameters.getThresholdForVisualisation();
	}
	
	/**
	 * 
	 * @return the region rank cut-off (ROC), where to calculate the Area Under the Curve (AUC)
	 */
	public float getROCthresholdAUC(){
		return this.inputParameters.getROCthresholdAUC();
	}
	
	public SpeciesNomenclature getSpeciesNomenclature(){
		return this.inputParameters.getSpeciesNomenclature();
	}
	
	/**
	 * 
	 * @return the type of the iRegulon action
	 */
	public IRegulonType getIRegulonType(){
		return this.inputParameters.getIRegulonType();
	}
	
	/**
	 * 
	 * @return the name of the run
	 */
	public String getName(){
		return this.inputParameters.getName();
	}
	
	/**
	 * 
	 * @return the minimal othologous
	 */
	public float getMinOrthologous(){
		return this.inputParameters.getMinOrthologous();
	}
	
	/**
	 * 
	 * @return the maximal motif Similarity FDR
	 */
	public float getMaxMotifSimilarityFDR(){
		return this.inputParameters.getMaxMotifSimilarityFDR();
	}
	
	/**
	 * 
	 * @return true if region based
	 */
	public boolean isRegionBased(){
		return this.inputParameters.isRegionBased();
	}
	
	/**
	 * 
	 * @return true if gene based
	 */
	public boolean isGeneBased(){
		return ! this.inputParameters.isGeneBased();
	}
	
	/**
	 * 
	 * @return the database
	 */
	public String getDatabaseName(){
		return this.inputParameters.getDatabase().getName();
	}
	
	public String getDatabase(){
		return this.inputParameters.getDatabase().getCode();
	}
	
	
	/**
	 * 
	 * @return the percentage of overlap (only regionbased)
	 */
	public float getOverlap(){
		return this.inputParameters.getOverlap();
	}
	
	/**
	 * 
	 * @return the name of the delineation (only regionbased)
	 */
	public String getDelineationName(){
		if (this.isDelineationBased()){
			return this.inputParameters.getDelineation().getName();
		}
		return null;
	}
	
	public Delineation getDelineation(){
		return this.inputParameters.getDelineation();
	}
	
	public String getDelineationDatabase(){
		return this.inputParameters.getDelineation().getCode();
	}
	
	public boolean isDelineationBased(){
		return this.inputParameters.isDelineationBased();
	}
	
	/**
	 * 
	 * @return the bp upstream (only regionbased)
	 */
	public int getUpstream(){
		return this.inputParameters.getUpstream();
	}
	
	/**
	 * 
	 * @return the bp downstream (only regionbased)
	 */
	public int getDownstream(){
		return this.inputParameters.getDownstream();
	}
	
	public Input getInput(){
		return this.inputParameters;
	}
}
