package servercommunication;

import domainmodel.*;
import servercommunication.tasks.EnrichedMotifsAndTracksResults;

import java.net.URI;
import java.util.List;
import java.util.Set;


public interface ComputationalService {
    public EnrichedMotifsAndTracksResults createPredictRegulatorsTask(PredictRegulatorsParameters predictRegulatorsParameters);

    public List<AbstractMotifAndTrack> findPredictedRegulators(PredictRegulatorsParameters predictRegulatorsParameters) throws ServerCommunicationException;

    public Set<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome(final SpeciesNomenclature speciesNomenclature) throws ServerCommunicationException;

    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor,
                                                             List<TargetomeDatabase> targetomeDatabases,
                                                             int occurrenceCountThreshold,
                                                             int maxNodeCount)
            throws ServerCommunicationException;

    public List<EnhancerRegion> getEnhancerRegions(final AbstractMotifAndTrack motifOrTrack) throws ServerCommunicationException;

    public URI getLink2GenomeBrowser4EnhancerRegions(final AbstractMotifAndTrack motifOrTrack);
}
