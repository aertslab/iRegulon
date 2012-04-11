package cisTargetConnection;

import httpProtocols.*;

import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import domainModel.GeneIdentifier;
import domainModel.Motif;

public class ClassicalTask implements Task {
	private cytoscape.task.TaskMonitor taskMonitor;
	
	private Collection<GeneIdentifier> geneIDs;
	private float AUCThreshold;
	private int rankThreshold;
	private float NESThreshold;
	private Collection<Motif> motifs;
	private State state = State.ERROR;
	private boolean interrupted = false;
	private Service service;
	private String errorMessage = "No error has occured";
	private String name;
	private float minOrthologous;
	private float maxMotifSimilarityFDR;

	
	public ClassicalTask(Service service, String name, Collection<GeneIdentifier> geneIDs, float AUCThreshold, 
			int rankThreshold, float NESThreshold, float minOrthologous, float maxMotifSimilarityFDR) {
		this.geneIDs = geneIDs;
		this.AUCThreshold = AUCThreshold;
		this.rankThreshold = rankThreshold;
		this.NESThreshold = NESThreshold;
		this.motifs = Collections.EMPTY_LIST;
		this.service = service;
		this.name = name;
		this.minOrthologous = minOrthologous;
		this.maxMotifSimilarityFDR = maxMotifSimilarityFDR;
	}
	
	public void halt() {
		// not implemented
		this.interrupted = true;
	}

	public String getTitle() {
		return "Classical " + ResourceBundle.getBundle("cistargetx").getString("plugin_name");
	}

	public void run() {
		int progress = -1;
		taskMonitor.setStatus("Starting Request");
		taskMonitor.setPercentCompleted(progress);
		
		int jobID = this.service.sentJob(this.name, this.geneIDs, this.AUCThreshold, 
						this.rankThreshold, this.NESThreshold, this.minOrthologous, this.maxMotifSimilarityFDR);
		progress = 10;
		if (jobID == -1){
			this.interrupted = true;
			
		}
		taskMonitor.setPercentCompleted(progress);
		int jobsBefore = this.service.getJobsBeforeThis(jobID);
		int progressJobs = 65;
		if (jobsBefore != 0){
			progressJobs = 65 / jobsBefore;
		}
		taskMonitor.setStatus("Requesting server");
		boolean otherMessage = true;
		while(this.service.getState(jobID).equals(State.REQUESTED) && ! this.interrupted){
			int jobsBeforeNow = this.service.getJobsBeforeThis(jobID);
			if (jobsBefore > jobsBeforeNow){
				jobsBefore = jobsBeforeNow;
				progress += progressJobs;
				taskMonitor.setPercentCompleted(progress);
				otherMessage = !otherMessage;
				if (otherMessage){
					taskMonitor.setStatus("Requesting server");
				}else{
					taskMonitor.setStatus("Waiting until other jobs are finished");
				}
			}
		}
		progress = 70;
		progress += 10;
		taskMonitor.setPercentCompleted(progress);
		if (! this.interrupted){
			taskMonitor.setStatus("Running your analysis");
		}
		while(this.service.getState(jobID).equals(State.RUNNING)  && ! this.interrupted){
			
		}
		if (! this.interrupted){
			progress = progress + 10;
			taskMonitor.setPercentCompleted(progress);
			State state = this.service.getState(jobID);
			if (this.service.getState(jobID).equals(State.FINISHED)){
				taskMonitor.setStatus("Recalculating your motifs");
				Collection<Motif> motifs = this.service.getMotifs(jobID);
				this.motifs = motifs;
				this.state = this.service.getState(jobID);
				progress = 100;
				taskMonitor.setPercentCompleted(progress);
				taskMonitor.setStatus("Creating view");
			}
			if (this.service.getState(jobID).equals(State.ERROR)){
				taskMonitor.setStatus("Error: getting error message.");
				this.errorMessage = this.service.getErrorMessage(jobID);
			}
		}
		
	}

	@Override
	public void setTaskMonitor(TaskMonitor monitor)
			throws IllegalThreadStateException {
		taskMonitor = monitor;
	}
	
	public Collection<Motif> getMotifs(){
		return this.motifs;
	}
	
	public State getFinishedState(){
		return this.state;
	}
	
	public boolean getIsInterupted(){
		return this.interrupted;
	}
	
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	
	
	
	
}
