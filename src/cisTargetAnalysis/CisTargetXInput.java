package cisTargetAnalysis;

import java.util.Collection;

import cisTargetX.CisTargetType;
import cytoscape.*;
import domainModel.GeneIdentifier;
import domainModel.SpeciesNomenclature;

public class CisTargetXInput {

	
	//all input variables
	private Collection<GeneIdentifier> genes;
	private float eScore;
	private int thresholdForVisualisation;
	private float rocThresholdAUC;
	private SpeciesNomenclature speciesNomenclature;
	private CisTargetType cisCisTargetType;
	private String name;
	private float minOrthologous;
	private float maxMotifSimilarityFDR;
	
	//Database
	private boolean isRegionBased;
	private String database;
	private float overlap;
	private String delineation;
	private int upstream;
	private int downstream;
	
	
	
	public CisTargetXInput(Collection<GeneIdentifier> genes, float escore, float ROCthresholdAUC, 
			int visualisationThreshold, SpeciesNomenclature speciesNomenclature, 
			CisTargetType cisTargetType, String runName, float minOrthologous,
			float maxMotifSimilarityFDR){
		this.genes = genes;
		this.eScore = escore;
		this.thresholdForVisualisation = visualisationThreshold;
		this.rocThresholdAUC = ROCthresholdAUC;
		this.speciesNomenclature = speciesNomenclature;
		this.cisCisTargetType = cisTargetType;
		this.name = runName;
		this.minOrthologous = minOrthologous;
		this.maxMotifSimilarityFDR = maxMotifSimilarityFDR;
	}
	
	/**
	 * 
	 * @return an array of all the nodes selected as input
	 */
	public Collection<GeneIdentifier> getGenes(){
		return this.genes;
	}
	
	/**
	 * 
	 * @return the Enrichment score threshold
	 */
	public float getEScore(){
		return this.eScore;
	}
	
	/**
	 * 
	 * @return the x-axis cut-off for visualisation
	 */
	public int getThresholdForVisualisation(){
		return this.thresholdForVisualisation;
	}
	
	/**
	 * 
	 * @return the region rank cut-off (ROC), where to calculate the Area Under the Curve (AUC)
	 */
	public float getROCthresholdAUC(){
		return this.rocThresholdAUC;
	}
	
	public SpeciesNomenclature getSpeciesNomenclature(){
		return this.speciesNomenclature;
	}
	
	/**
	 * 
	 * @return the type of the cisTarget action
	 */
	public CisTargetType getCisTargetType(){
		return this.cisCisTargetType;
	}
	
	/**
	 * 
	 * @return the name of the run
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * 
	 * @return the minimal othologous
	 */
	public float getMinOrthologous(){
		return this.minOrthologous;
	}
	
	/**
	 * 
	 * @return the maximal motif Similarity FDR
	 */
	public float getMaxMotifSimilarityFDR(){
		return this.maxMotifSimilarityFDR;
	}
	
	/**
	 * 
	 * @return true if region based
	 */
	public boolean isRegionBased(){
		return this.isRegionBased;
	}
	
	/**
	 * 
	 * @return true if gene based
	 */
	public boolean isGeneBased(){
		return ! this.isRegionBased;
	}
	
	/**
	 * 
	 * @return the database
	 */
	public String getDatabase(){
		return this.database;
	}
	
	/**
	 * 
	 * @return the percentage of overlap (only regionbased)
	 */
	public float getOverlap(){
		return this.overlap;
	}
	
	/**
	 * 
	 * @return the name of the delineation (only regionbased)
	 */
	public String getDelineation(){
		return this.delineation;
	}
	
	/**
	 * 
	 * @return the bp upstream (only regionbased)
	 */
	public int getUpstream(){
		return this.upstream;
	}
	
	/**
	 * 
	 * @return the bp downstream (only regionbased)
	 */
	public int getDownstream(){
		return this.downstream;
	}
	
	
	/**
	 * 
	 * @return true if the parameters are valid
	 */
	public boolean parametersAreValid(){
		boolean parametersAreOkay = true;
		if (0 > this.getMaxMotifSimilarityFDR() || this.getMaxMotifSimilarityFDR() > 1){
			parametersAreOkay = false;
		}
		if (0 > this.getMinOrthologous() || this.getMaxMotifSimilarityFDR() > 1){
			parametersAreOkay = false;
		}
		if (1 > this.getThresholdForVisualisation()){
			parametersAreOkay = false;
		}
		if (0 > this.getROCthresholdAUC() || this.getROCthresholdAUC() > 1){
			parametersAreOkay = false;
		}
		if (1.5 > this.getEScore()){
			parametersAreOkay = false;
		}
		if (this.getName().isEmpty() || 
				this.getName().toLowerCase().equalsIgnoreCase("CisTarget name")){
			parametersAreOkay = false;
		}
		return parametersAreOkay;
	}
	
	/**
	 * 
	 * @return the message where the error occured
	 */
	public String getErrorMessage(){
		boolean parametersAreOkay = true;
		String message = "<html> Error: You have filled in a wrong parameter value: <br /> <br />";
		if (0 > this.getMaxMotifSimilarityFDR() || this.getMaxMotifSimilarityFDR() > 1){
			parametersAreOkay = false;
			message = message + "Max motif Similarity FDR must be between 0 and 1. <br /> <br />";
		}
		if (0 > this.getMinOrthologous() || this.getMaxMotifSimilarityFDR() > 1){
			parametersAreOkay = false;
			message = message + "Min Orthologous must be between 0 and 1. <br /> <br />";
		}
		if (1 > this.getThresholdForVisualisation()){
			parametersAreOkay = false;
			message = message + "The treshold for visualisation must be greater then 1. <br /> <br />";
		}
		if (0 > this.getROCthresholdAUC() || this.getROCthresholdAUC() > 1){
			parametersAreOkay = false;
			message = message + "The AUC threshold must be between 0 and 1. <br /> <br />";
		}
		if (1.5 > this.getEScore()){
			parametersAreOkay = false;
			message = message + "The E score must be greater then 1.5, else the prediction" +
					"of your motifs and transcription factors will take a lot of time. <br /> <br />";
		}
		if (this.getName().isEmpty() || 
				this.getName().toLowerCase().equalsIgnoreCase("CisTarget name")){
			parametersAreOkay = false;
			message = message + "You must choose a name for your research. <br /> <br />";
		}
		if (parametersAreOkay){
			return "No error message.";
		}
		message = message + "</html>";
		return message;
	}
	
	
}
