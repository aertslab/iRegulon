package view.resultspanel.motifview.detailpanel;

import domainmodel.InputParameters;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;

public class TFandMotifSelected {

	private Motif motif;
	private TranscriptionFactor tf;
	private InputParameters input;
	
	public TFandMotifSelected(InputParameters input){
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
	
	public InputParameters getInput(){
		return this.input;
	}
	
}
