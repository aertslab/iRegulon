package servercommunication;


import java.util.*;


import domainmodel.*;


public interface ComputationalService {
	public List<Motif> findPredictedRegulators(InputParameters input);

    public Set<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome(final SpeciesNomenclature speciesNomenclature) throws ServerCommunicationException;

    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor, List<TargetomeDatabase> databases) throws ServerCommunicationException;
}
