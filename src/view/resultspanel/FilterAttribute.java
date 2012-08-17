package view.resultspanel;


public enum FilterAttribute {
	MOTIF("motif"),
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
