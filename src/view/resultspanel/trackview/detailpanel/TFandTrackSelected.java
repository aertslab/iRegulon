package view.resultspanel.trackview.detailpanel;

import domainmodel.InputParameters;
import domainmodel.Track;
import domainmodel.TranscriptionFactor;

public class TFandTrackSelected {

	private Track track;
	private TranscriptionFactor tf;
	private InputParameters input;
	
	public TFandTrackSelected(InputParameters input){
		this.track = null;
		this.tf = null;
		this.input = input;
	}
	
	
	public Track getTrack(){
		return this.track;
	}
	
	public void setTrack(Track track){
		this.track = track;
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
