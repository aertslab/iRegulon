package domainModel;

public class Database {

	private final String code;
	private final String name;
	private final float AUCvalue;
	private final int visualisationValue;
	
	public Database(String code, String name, float AUCvalue, int visualisationValue){
		this.code = code;
		this.name = name;
		this.AUCvalue = AUCvalue;
		this.visualisationValue = visualisationValue;
	}

	
	public String getCode(){
		return this.code;
	}
	
	public String getName(){
		return this.name;
	}
	
	public float getAUCvalue(){
		return this.AUCvalue;
	}
	
	public int getVisualisationValue(){
		return this.visualisationValue;
	}
	
	public String toString(){
		return this.name;
	}
	
	
	
}
