package servercommunication.protocols;


import domainmodel.AbstractMotifAndTrack;
import domainmodel.InputParameters;
import servercommunication.ServerCommunicationException;

import java.util.Collection;

public interface Protocol {
    public int sentJob(InputParameters input) throws ServerCommunicationException;

    public State getState(int jobID) throws ServerCommunicationException;

    public int getJobsBeforeThis(int jobID) throws ServerCommunicationException;

    public Collection<AbstractMotifAndTrack> getMotifsAndTracks(InputParameters input, int jobID) throws ServerCommunicationException;

    public String getErrorMessage(int jobID) throws ServerCommunicationException;
}
