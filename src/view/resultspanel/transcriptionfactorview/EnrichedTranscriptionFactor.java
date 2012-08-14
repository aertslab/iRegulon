package view.resultspanel.transcriptionfactorview;

import java.util.ArrayList;
import java.util.List;

import domainmodel.GeneIdentifier;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class EnrichedTranscriptionFactor implements Comparable<EnrichedTranscriptionFactor>{

	private final GeneIdentifier geneID;
	private int timesPredicted;
	private List<Motif> motifs;
	private List<Motif> perfectMotifs;
	private final int totalMotifs;
	private float score;
	private int noOrthologes;
	private int exactMotif;
	private float scoreNotPerfect;

	private int timesPerfect;
	private float scorePerfect;
	private int timesOrthologous;
	private float scoreOrthologous;
	private int timesSimilar;
	private float scoreSimilar;
	private int timesRest;
	private float scoreRest;
	
	private static final float WEIGHT_PERFECT = 1;
	private static final float WEIGHT_ORTHOLOGOUS = 0;
	private static final float WEIGHT_SIMILAR = 1;
	private static final float WEIGHT_REST = 0;
	
	public EnrichedTranscriptionFactor(GeneIdentifier geneID, int totalMotifs){
		this.geneID = geneID;
		this.totalMotifs = totalMotifs;
		this.timesPerfect = 0;
		this.timesPredicted = 0;
		this.motifs = new ArrayList<Motif>();
		this.perfectMotifs = new ArrayList<Motif>();
		this.noOrthologes = 0;
		this.exactMotif = 0;
		this.scoreNotPerfect = 0;
		this.timesOrthologous = 0;
		this.scoreOrthologous = 0;
		this.timesSimilar = 0;
		this.scoreSimilar = 0;
		this.timesRest = 0;
		this.scoreRest = 0;
	}
	
	public void addMotif(Motif motif){
		List<TranscriptionFactor> tfs = motif.getTranscriptionFactors();
		for (TranscriptionFactor tf : tfs){
			if (tf.getGeneID().getGeneName().equals(this.geneID.getGeneName())){
				this.timesPredicted++;
				this.motifs.add(motif);
				this.score += (motif.getNeScore());
				if (Float.isNaN(tf.getMinOrthologousIdentity())
						&& Float.isNaN(tf.getMaxMotifSimilarityFDR())){
					//perfect
					this.timesPerfect++;
					this.perfectMotifs.add(motif);
					this.scorePerfect += (motif.getNeScore());
				}else{
					this.scoreNotPerfect += (motif.getNeScore());
					if (Float.isNaN(tf.getMinOrthologousIdentity())){
						//this.score += 1;
						this.noOrthologes+=1;
						if (! Float.isNaN(tf.getMaxMotifSimilarityFDR())){
							//ortholog
							this.timesOrthologous++;
							this.scoreOrthologous += motif.getNeScore();
						}
					}else{
						//this.score -= tf.getMinOrthologousIdentity();
						if (Float.isNaN(tf.getMaxMotifSimilarityFDR())){
							//this.score += 1;
							this.exactMotif += 1;
							if (! Float.isNaN(tf.getMinOrthologousIdentity())){
								//motif
								this.timesSimilar++;
								this.scoreSimilar += motif.getNeScore() * tf.getMinOrthologousIdentity();
							}
						}else{
							//this.score -= tf.getMaxMotifSimilarityFDR();
							this.timesRest++;
							this.scoreRest+= motif.getNeScore();
						}
						
					}
					
				}
			}
		}
	}
	
	
	public String getName(){
		return this.geneID.getGeneName();
	}
	
	public int getTimesPredicted(){
		return this.timesPredicted;
	}
	
	public float calculateMeanPerfectScore(){
		if (this.perfectMotifs.size() == 0){
			return 0;
		}
		return this.scorePerfect / this.perfectMotifs.size();
	}
	
	public float calculatePerfectScore(){
		return this.scorePerfect / this.totalMotifs;
	}
	
	public float calculateMeanScore(){
		return this.score / this.timesPredicted;
	}
	
	public int getTimesPerfectMatch(){
		return this.timesPerfect;
	}
	
	public int getTimesExactMotifMatch(){
		return this.timesSimilar;
	}

	public int getTimesOrthologousMatch(){
		return this.timesOrthologous;
	}
	
	public int getTimesRestMatch(){
		return this.timesRest;
	}
	
	public float getTotalScore(){
		float score = 0;
		score = ((this.scorePerfect * this.WEIGHT_PERFECT) + 
				(this.scoreOrthologous * this.WEIGHT_ORTHOLOGOUS) + 
				(this.scoreSimilar * this.WEIGHT_SIMILAR) + 
				(this.scoreRest * this.WEIGHT_REST))
				/ (this.timesPredicted);
		score = score * this.timesPredicted / this.totalMotifs;
		return score;
	}
	
	/**
	 * @param PredictedTF tf
	 * @return -1 if this EnrichedTranscriptionFactor is better than tf
	 * 			0 if both EnrichedTranscriptionFactor are equal
	 * 			1 if tf is better than this EnrichedTranscriptionFactor
	 */
	@Override
	public int compareTo(EnrichedTranscriptionFactor tf) {
		// TODO Auto-generated method stub
		if (tf.getTotalScore() > this.getTotalScore()){
			return 1;
		}
		if (tf.getTotalScore() < this.getTotalScore()){
			return -1;
		}
		return 0;
	}
	
	
	
	
}
