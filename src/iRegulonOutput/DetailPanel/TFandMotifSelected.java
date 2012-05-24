package iRegulonOutput.DetailPanel;

import domainModel.Input;
import domainModel.Motif;
import domainModel.TranscriptionFactor;

public class TFandMotifSelected {

	private Motif motif;
	private TranscriptionFactor tf;
	private Input input;
	
	public TFandMotifSelected(Input input){
		this.motif = null;
		this.tf = null;
		this.input = input;
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
	
	public Input getInput(){
		return this.input;
	}
	
}
