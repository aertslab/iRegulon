package servercommunication.protocols;

import domainmodel.AbstractMotifAndTrack;
import domainmodel.PredictRegulatorsParameters;
import servercommunication.ServerCommunicationException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;


public interface Protocol {
    public HttpURLConnection createConnection(String urlString) throws IOException;

    public HttpURLConnection createConnection4ResourceBundleKey(String resourceBundleKey) throws IOException;

    public int sentJob(PredictRegulatorsParameters predictRegulatorsParameters) throws ServerCommunicationException;

    public State getState(int jobID) throws ServerCommunicationException;

    public int getJobsBeforeThis(int jobID) throws ServerCommunicationException;

    public List<AbstractMotifAndTrack> getMotifsAndTracks(PredictRegulatorsParameters predictRegulatorsParameters, int jobID) throws ServerCommunicationException;

    public String getErrorMessage(int jobID) throws ServerCommunicationException;
}
