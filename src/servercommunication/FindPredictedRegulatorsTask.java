package servercommunication;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import domainmodel.InputParameters;
import domainmodel.AbstractMotifAndTrack;
import domainmodel.Motif;
import domainmodel.Track;
import infrastructure.Logger;
import servercommunication.protocols.Protocol;
import servercommunication.protocols.State;
import view.IRegulonResourceBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FindPredictedRegulatorsTask extends IRegulonResourceBundle implements Task {
    private static final int WAITING_TIME_IN_MS = 3000;

    private cytoscape.task.TaskMonitor taskMonitor;

    private Collection<AbstractMotifAndTrack> motifsAndTracks = Collections.emptyList();
    private Collection<Motif> motifs = new ArrayList<Motif>();
    private Collection<Track> tracks = new ArrayList<Track>();

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
        this.motifsAndTracks = Collections.emptyList();
        this.state = State.ERROR;
    }

    public void run() {
        try {
            taskMonitor.setStatus("Starting request.");
            taskMonitor.setPercentCompleted(0);

            final int jobID = service.sentJob(input);

            if (jobID < 0) {
                interrupt("Invalid job ID received from server.");
                return;
            }

            if (interrupted) {
                interrupt("Job cancelled by user.");
                return;
            }

            taskMonitor.setStatus("Requesting server...");
            taskMonitor.setPercentCompleted(10);

            while (service.getState(jobID).equals(State.REQUESTED)) {
                if (interrupted) {
                    interrupt("Job cancelled by user.");
                    return;
                }

                final int numberOfJobsInQueue = service.getJobsBeforeThis(jobID);
                taskMonitor.setStatus(numberOfJobsInQueue == 0
                        ? "Requesting server..."
                        : "Waiting until other jobs are finished...");

                final int progressJobs = (numberOfJobsInQueue != 0) ? 70 / numberOfJobsInQueue : 70;
                taskMonitor.setPercentCompleted(10 + progressJobs);

                try {
                    Thread.sleep(WAITING_TIME_IN_MS);
                } catch (InterruptedException e) {
                    Logger.getInstance().error(e);
                }
            }

            taskMonitor.setStatus("Running your analysis...");
            taskMonitor.setPercentCompleted(90);

            while (this.service.getState(jobID).equals(State.RUNNING)) {
                if (interrupted) {
                    interrupt("Job cancelled by user.");
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
                taskMonitor.setStatus("Receiving analysis results.");
                this.errorMessage = "";
                this.motifsAndTracks = service.getMotifsAndTracks(input, jobID);

                for (AbstractMotifAndTrack motifOrTrack : this.motifsAndTracks) {
                    if(motifOrTrack.isMotif()) {
                        motifs.add((Motif) motifOrTrack);
                    } else if(motifOrTrack.isTrack()) {
                        tracks.add((Track) motifOrTrack);
                    }
                }
            } else if (State.ERROR.equals(this.state)) {
                taskMonitor.setStatus("Error.");
                this.motifsAndTracks = Collections.emptyList();
                this.errorMessage = service.getErrorMessage(jobID);
            }
        } catch (ServerCommunicationException e) {
            Logger.getInstance().error(e.getMessage());
            interrupt(e.getMessage());
            this.interrupted = false;
            return;
        }
    }

    @Override
    public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
        taskMonitor = monitor;
    }

    public Collection<AbstractMotifAndTrack> getMotifsAndTracks() {
        return this.motifsAndTracks;
    }

    public Collection<Motif> getMotifs() {
        return this.motifs;
    }

    public Collection<Track> getTracks() {
        return this.tracks;
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
