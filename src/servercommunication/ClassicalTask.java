package servercommunication;

import servercommunication.protocols.*;
import view.IRegulonResourceBundle;

import java.util.Collection;


import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import domainmodel.InputParameters;
import domainmodel.Motif;

public class ClassicalTask extends IRegulonResourceBundle implements Task {
	private cytoscape.task.TaskMonitor taskMonitor;
	
	private Collection<Motif> motifs;
	private State state = State.ERROR;
	private boolean interrupted = false;
	private Service service;
	private String errorMessage = "No error has occured";

	private InputParameters input;

	
	public ClassicalTask(Service service, InputParameters input) {
		this.service = service;
		this.input = input;
	}
	
	public void halt() {
		// not implemented
		this.interrupted = true;
	}

	public String getTitle() {
		return "Classical " + this.getBundle().getString("plugin_name");
	}

	public void run() {
		int progress = -1;
		taskMonitor.setStatus("Starting Request");
		taskMonitor.setPercentCompleted(progress);
		
		int jobID;
		try {
			jobID = this.service.sentJob(this.input);
		} catch (ServerCommunicationException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			jobID = -1;
			this.interrupted = true;
			this.errorMessage = e.getMessage();
			this.state = State.ERROR;
		}
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
			try {
				Thread.sleep(3000);
				//System.out.println("check");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (! this.interrupted){
			progress = progress + 10;
			taskMonitor.setPercentCompleted(progress);
			this.state = this.service.getState(jobID);
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
