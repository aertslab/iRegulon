package httpProtocols;

import java.util.Collection;

import cisTargetAnalysis.CisTargetXInput;

import domainModel.Motif;

public interface Service {

	public int sentJob(CisTargetXInput input);
	
	public State getState(int jobID);
	
	public int getJobsBeforeThis(int jobID);
	
	public Collection<Motif> getMotifs(int jobID);
	
	public String getErrorMessage(int jobID);
		
	
}
