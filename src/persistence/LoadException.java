package persistence;

public class LoadException extends Exception{
	
	public LoadException(String message, Exception e){
		super(message, e);
	}
	
	public String toString(){
		return this.getMessage();
	}

}
