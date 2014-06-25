package servercommunication.tasks;

import domainmodel.AbstractMotifAndTrack;
import org.cytoscape.work.Task;

import java.util.List;


public interface EnrichedMotifsAndTracksResults extends Task {
    List<AbstractMotifAndTrack> getMotifsAndTracks();
}
