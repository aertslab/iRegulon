package httpProtocols;

import iRegulonAnalysis.Input;

import java.util.Collection;


import domainModel.Motif;

public interface Service {

	public int sentJob(Input input);
	
	public State getState(int jobID);
	
	public int getJobsBeforeThis(int jobID);
	
	public Collection<Motif> getMotifs(int jobID);
	
	public String getErrorMessage(int jobID);
		
	
}
