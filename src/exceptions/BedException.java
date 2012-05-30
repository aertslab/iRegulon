package exceptions;

public class BedException extends Exception{

	public BedException(String message, Exception e){
		super(message, e);
	}
	
	public String toString(){
		return this.getMessage();
	}
	
}
