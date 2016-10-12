package servercommunication.tasks;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.Motif;
import domainmodel.PredictRegulatorsParameters;
import domainmodel.Track;
import infrastructure.IRegulonResourceBundle;
import infrastructure.Logger;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import servercommunication.ServerCommunicationException;
import servercommunication.protocols.Protocol;
import servercommunication.protocols.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class FindPredictedRegulatorsTask extends IRegulonResourceBundle implements Task, EnrichedMotifsAndTracksResults {
    private static final int WAITING_TIME_IN_MS = 3000;

    private List<AbstractMotifAndTrack> motifsAndTracks = Collections.emptyList();
    private List<Motif> motifs = new ArrayList<Motif>();
    private List<Track> tracks = new ArrayList<Track>();

    private State state = State.ERROR;
    private boolean interrupted = false;
    private Protocol service;
    private String errorMessage = "";

    private PredictRegulatorsParameters predictRegulatorsParameters;

    public FindPredictedRegulatorsTask(Protocol service, PredictRegulatorsParameters predictRegulatorsParameters) {
        this.service = service;
        this.predictRegulatorsParameters = predictRegulatorsParameters;
    }

    public String getTitle() {
        return PLUGIN_NAME + " analysis: " + predictRegulatorsParameters.getName();
    }

    private void interrupt(final String msg) {
        this.interrupted = true;
        this.errorMessage = msg;
        this.motifsAndTracks = Collections.emptyList();
        this.state = State.ERROR;
    }

    public void run(final TaskMonitor taskMonitor) {
        try {
            taskMonitor.setTitle(getTitle());

            taskMonitor.setStatusMessage("Starting request");
            taskMonitor.setProgress(0.0);

            final int jobID = service.sentJob(predictRegulatorsParameters);

            if (jobID < 0) {
                interrupt("Invalid job ID received from server.");
                return;
            }

            if (interrupted) {
                interrupt("Job cancelled by user.");
                return;
            }

            taskMonitor.setStatusMessage("Requesting server...");
            taskMonitor.setProgress(0.10);

            while (service.getState(jobID).equals(State.REQUESTED)) {
                if (interrupted) {
                    interrupt("Job cancelled by user.");
                    return;
                }

                final int numberOfJobsInQueue = service.getJobsBeforeThis(jobID);
                taskMonitor.setStatusMessage(numberOfJobsInQueue == 0
                        ? "Requesting server..."
                        : "Waiting until other jobs are finished...");

                final double progressJobs = (numberOfJobsInQueue != 0) ? 0.70 / numberOfJobsInQueue : 0.70;
                taskMonitor.setProgress(0.10 + progressJobs);

                try {
                    Thread.sleep(WAITING_TIME_IN_MS);
                } catch (InterruptedException e) {
                    Logger.getInstance().error(e);
                }
            }

            taskMonitor.setStatusMessage("Running your analysis...");
            taskMonitor.setProgress(0.90);

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
            taskMonitor.setProgress(1.00);
            if (State.FINISHED.equals(this.state)) {
                taskMonitor.setStatusMessage("Receiving analysis results.");
                this.errorMessage = "";
                this.motifsAndTracks = service.getMotifsAndTracks(predictRegulatorsParameters, jobID);

                for (AbstractMotifAndTrack motifOrTrack : this.motifsAndTracks) {
                    if (motifOrTrack.isMotif()) {
                        motifs.add((Motif) motifOrTrack);
                    } else if (motifOrTrack.isTrack()) {
                        tracks.add((Track) motifOrTrack);
                    }
                }

                if (this.motifsAndTracks.isEmpty()) {
                    this.errorMessage = "Not a single motif or track is enriched for your input gene signature. Please change your input parameters.";
                }
            } else if (State.ERROR.equals(this.state)) {
                taskMonitor.setStatusMessage("Error.");
                this.motifsAndTracks = Collections.emptyList();
                this.errorMessage = service.getErrorMessage(jobID);
            }
        } catch (ServerCommunicationException e) {
            Logger.getInstance().error(e.getMessage());
            interrupt(e.getMessage());
        }
    }

    @Override
    public void cancel() {
        this.interrupted = true;
    }

    public List<AbstractMotifAndTrack> getMotifsAndTracks() {
        return this.motifsAndTracks;
    }

    public List<Motif> getMotifs() {
        return this.motifs;
    }

    public List<Track> getTracks() {
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
