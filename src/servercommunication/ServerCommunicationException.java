package servercommunication;

public class ServerCommunicationException extends Exception {

    public ServerCommunicationException(String message, Exception e) {
        super(message, e);
    }

    public ServerCommunicationException(String message) {
        super(message);
    }

    public String toString() {
        return this.getMessage();
    }
}
