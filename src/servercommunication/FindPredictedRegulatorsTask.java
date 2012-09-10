package servercommunication;

import infrastructure.Logger;
import servercommunication.protocols.*;
import view.IRegulonResourceBundle;

import java.util.Collection;
import java.util.Collections;


import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import domainmodel.InputParameters;
import domainmodel.Motif;

public class FindPredictedRegulatorsTask extends IRegulonResourceBundle implements Task {
    private static final int WAITING_TIME_IN_MS = 3000;

    private cytoscape.task.TaskMonitor taskMonitor;

    private Collection<Motif> motifs = Collections.emptyList();
    private State state = State.ERROR;
    private boolean interrupted = false;
    private Protocol service;
    private String errorMessage = "";

    private InputParameters input;


    public FindPredictedRegulatorsTask(Protocol service, InputParameters input) {
        this.service = service;
        this.input = input;
    }

    public void halt() {
        this.interrupted = true;
    }

    public String getTitle() {
        return getBundle().getString("plugin_name") + ": Prediction of transcription factors";
    }

    private void interrupt(final String msg) {
        this.interrupted = true;
        this.errorMessage = msg;
        this.motifs = Collections.emptyList();
        this.state = State.ERROR;
    }

    public void run() {
        try {
            taskMonitor.setStatus("Starting request");
            taskMonitor.setPercentCompleted(0);

            final int jobID = service.sentJob(input);
            if (jobID < 0) {
                interrupt("Invalid job ID received from server.");
                return;
            }

            if (interrupted) {
                interrupt("Job cancelled by user");
                return;
            }

            taskMonitor.setStatus("Requesting server");
            taskMonitor.setPercentCompleted(10);

            while (service.getState(jobID).equals(State.REQUESTED)) {
                if (interrupted) {
                    interrupt("Job cancelled by user");
                    return;
                }

                final int numberOfJobsInQueue = service.getJobsBeforeThis(jobID);
                taskMonitor.setStatus(numberOfJobsInQueue == 0
                        ? "Requesting server"
                        : "Waiting until other jobs are finished");

                final int progressJobs = (numberOfJobsInQueue != 0) ? 70 / numberOfJobsInQueue : 70;
                taskMonitor.setPercentCompleted(10 + progressJobs);

                try {
                    Thread.sleep(WAITING_TIME_IN_MS);
                } catch (InterruptedException e) {
                    Logger.getInstance().error(e);
                }
            }

            taskMonitor.setStatus("Running your analysis");
            taskMonitor.setPercentCompleted(90);

            while (this.service.getState(jobID).equals(State.RUNNING)) {
                if (interrupted) {
                    interrupt("Job cancelled by user");
                    return;
                }

                try {
                    Thread.sleep(WAITING_TIME_IN_MS);
                } catch (InterruptedException e) {
                    Logger.getInstance().error(e);
                }
            }

            this.state = service.getState(jobID);
            taskMonitor.setPercentCompleted(100);
            if (State.FINISHED.equals(this.state)) {
                taskMonitor.setStatus("Receiving analysis results");
                this.errorMessage = "";
                this.motifs = service.getMotifs(jobID);
            } else if (State.ERROR.equals(this.state)) {
                taskMonitor.setStatus("Error");
                this.motifs = Collections.emptyList();
                this.errorMessage = service.getErrorMessage(jobID);
            }
        } catch (ServerCommunicationException e) {
            Logger.getInstance().error(e);
            interrupt(e.getMessage());
        }
    }

    @Override
    public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
        taskMonitor = monitor;
    }

    public Collection<Motif> getMotifs() {
        return this.motifs;
    }

    public State getFinishedState() {
        return this.state;
    }

    public boolean getIsInterupted() {
        return this.interrupted;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
