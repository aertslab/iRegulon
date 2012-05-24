package httpProtocols;


import java.util.Collection;


import domainModel.Input;
import domainModel.Motif;
import exceptions.SentRequestException;

public interface Service {

	public int sentJob(Input input) throws SentRequestException;
	
	public State getState(int jobID);
	
	public int getJobsBeforeThis(int jobID);
	
	public Collection<Motif> getMotifs(int jobID);
	
	public String getErrorMessage(int jobID);
		
	
}
