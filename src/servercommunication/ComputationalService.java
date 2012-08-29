package servercommunication;


import java.util.*;


import domainmodel.*;


public interface ComputationalService {
	public List<Motif> findPredictedRegulators(InputParameters input);

    public List<GeneIdentifier> queryTranscriptionFactorsWithPredictedTargetome(final SpeciesNomenclature speciesNomenclature);

    public List<CandidateTargetGene> queryPredictedTargetome(GeneIdentifier factor, List<TargetomeDatabase> databases);
}
