package resultsview;

public enum FilteringOn {

	MOTIF("motif"),
	TRANSCRIPTION_FACTOR("transcription factor"),
	TARGET_GENE("target");
	
	private String name;
	
	private FilteringOn(String name){
		this.name = name;
	}
	
	public String toString(){
		return this.name;
	}
	
}
