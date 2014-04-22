package view.resultspanel;


public enum FilterAttribute {
	MOTIF_OR_TRACK("motif/track"),
	TRANSCRIPTION_FACTOR("transcription factor"),
	TARGET_GENE("target");
	
	private String name;
	
	private FilterAttribute(String name){
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
}
