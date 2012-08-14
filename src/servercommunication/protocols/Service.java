package servercommunication.protocols;


import java.util.Collection;


import domainmodel.InputParameters;
import domainmodel.Motif;
import servercommunication.SentRequestException;

public interface Service {

	public int sentJob(InputParameters input) throws SentRequestException;
	
	public State getState(int jobID);
	
	public int getJobsBeforeThis(int jobID);
	
	public Collection<Motif> getMotifs(int jobID);
	
	public String getErrorMessage(int jobID);
		
	
}
