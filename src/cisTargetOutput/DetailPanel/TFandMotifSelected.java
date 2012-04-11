package cisTargetOutput.DetailPanel;

import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class TFandMotifSelected {

	private Motif motif;
	private TranscriptionFactor tf;
	
	public TFandMotifSelected(){
		this.motif = null;
		this.tf = null;
	}
	
	
	public Motif getMotif(){
		return this.motif;
	}
	
	public void setMotif(Motif motif){
		this.motif = motif;
	}
	
	public TranscriptionFactor getTranscriptionFactor(){
		return this.tf;
	}
	
	public void setTranscriptionFactor(TranscriptionFactor tf){
		this.tf = tf;
	}
	
}
