package servercommunication;

public class ServerCommunicationException extends Exception {

	public ServerCommunicationException(String message, Exception e){
		super(message, e);
	}
	
	public String toString(){
		return this.getMessage();
	}
}
