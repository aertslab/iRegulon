package domainModel;

public class Delineation {

	private final String code;
	private final String name;
	
	public Delineation(String code, String name){
		this.code = code;
		this.name = name;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String toString(){
		return this.name;
	}
	
}
