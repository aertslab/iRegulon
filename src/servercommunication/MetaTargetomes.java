package servercommunication;

import domainmodel.GeneIdentifier;
import domainmodel.SpeciesNomenclature;
import infrastructure.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public final class MetaTargetomes {
    private static Map<SpeciesNomenclature, Set<GeneIdentifier>> SPECIES_NOMENCLATURE2FACTORS;

    static {
        try {
            SPECIES_NOMENCLATURE2FACTORS = queryForFactors();
        } catch (ServerCommunicationException e) {
            Logger.getInstance().error(e);
            SPECIES_NOMENCLATURE2FACTORS = Collections.emptyMap();
        }
    }

    private static Map<SpeciesNomenclature, Set<GeneIdentifier>> queryForFactors() throws ServerCommunicationException {
        final ComputationalService service = ComputationalServiceFactory.getInstance().getService();
        final Map<SpeciesNomenclature, Set<GeneIdentifier>> speciesNomenclature2factors = new HashMap<SpeciesNomenclature, Set<GeneIdentifier>>();
        for (SpeciesNomenclature speciesNomenclature : SpeciesNomenclature.getAllNomenclatures()) {
            speciesNomenclature2factors.put(speciesNomenclature, service.queryTranscriptionFactorsWithPredictedTargetome(speciesNomenclature));
        }
        return speciesNomenclature2factors;
    }

    public static Set<GeneIdentifier> getAvailableFactors(final SpeciesNomenclature speciesNomenclature) {
        if (SPECIES_NOMENCLATURE2FACTORS.containsKey(speciesNomenclature)) {
            return SPECIES_NOMENCLATURE2FACTORS.get(speciesNomenclature);
        } else {
            return Collections.emptySet();
        }
    }

    public static boolean hasAvailableFactors(final SpeciesNomenclature speciesNomenclature) {
        return SPECIES_NOMENCLATURE2FACTORS.containsKey(speciesNomenclature);
    }

    public static boolean hasAvailableFactors() {
        return !SPECIES_NOMENCLATURE2FACTORS.isEmpty();
    }

    private MetaTargetomes() {
    }
}
