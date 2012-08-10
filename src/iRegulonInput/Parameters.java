package iRegulonInput;

import domainModel.Input;

public interface Parameters {
	
	public int getRankThreshold();
	
	public float getAUCThreshold();
	
	public float getNESThreshold();
	
	public Input getInput();
	
	public void generateInput();
	
	public float getMinOrthologous();
	
	public float getMaxMotifSimilarityFDR();
	
	public String getAttributeName();
}