package servercommunication.protocols;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.PredictRegulatorsParameters;
import servercommunication.ServerCommunicationException;

import java.util.Collection;

public interface Protocol {
    public int sentJob(PredictRegulatorsParameters predictRegulatorsParameters) throws ServerCommunicationException;

    public State getState(int jobID) throws ServerCommunicationException;

    public int getJobsBeforeThis(int jobID) throws ServerCommunicationException;

    public Collection<AbstractMotifAndTrack> getMotifsAndTracks(PredictRegulatorsParameters predictRegulatorsParameters, int jobID) throws ServerCommunicationException;

    public String getErrorMessage(int jobID) throws ServerCommunicationException;
}
