package exceptions;

public class SentRequestException extends Exception {

	public SentRequestException(String message, Exception e){
		super(message, e);
	}
	
	public String toString(){
		return this.getMessage();
	}
}
