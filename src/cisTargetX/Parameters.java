package cisTargetX;

import cisTargetAnalysis.CisTargetXInput;

public interface Parameters {
	
	public int getRankThreshold();
	
	public float getAUCThreshold();
	
	public float getNESThreshold();
	
	public CisTargetXInput getInput();
	
	public void generateInput();
	
	public float getMinOrthologous();
	
	public float getMaxMotifSimilarityFDR();
	
	public String getAttributeName();
}
