package servercommunication.protocols;


import java.util.Collection;


import domainmodel.InputParameters;
import domainmodel.Motif;
import servercommunication.ServerCommunicationException;

public interface Protocol {
	public int sentJob(InputParameters input) throws ServerCommunicationException;
	
	public State getState(int jobID) throws ServerCommunicationException;
	
	public int getJobsBeforeThis(int jobID) throws ServerCommunicationException;
	
	public Collection<Motif> getMotifs(int jobID) throws ServerCommunicationException;
	
	public String getErrorMessage(int jobID) throws ServerCommunicationException;
}
