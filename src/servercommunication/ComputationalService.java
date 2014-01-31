package servercommunication;


import domainmodel.*;

import java.net.URI;
import java.util.List;
import java.util.Set;


public interface ComputationalService {
    public List<Motif> findPredictedRegulators(InputParameters input) throws ServerCommunicationException;

    public Set<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome(final SpeciesNomenclature speciesNomenclature) throws ServerCommunicationException;

    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor, List<TargetomeDatabase> targetomeDatabases, int occurrenceCountThreshold, int maxNodeCount)
            throws ServerCommunicationException;

    public List<EnhancerRegion> getEnhancerRegions(final AbstractMotif motif) throws ServerCommunicationException;

    public URI getLink2GenomeBrowser4EnhancerRegions(final AbstractMotif motif);
}
