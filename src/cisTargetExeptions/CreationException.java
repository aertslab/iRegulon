package cisTargetExeptions;

public class CreationException extends Exception{

	String error;
	
	public CreationException(){
		super();
		this.error = "The creation of an object didnt succeded";
	}
	
	public CreationException(String mistake){
		super();
		this.error = mistake;
	}
	
	public String getError(){
		return this.error;
	}
	
	public String toString(){
		return "The following error is occurred: " + this.error;
	}
	
	
}
