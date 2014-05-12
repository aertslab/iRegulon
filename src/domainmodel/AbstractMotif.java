package domainmodel;


import java.util.List;

public abstract class AbstractMotif extends AbstractMotifAndTrack {

    public AbstractMotif(String clusterCode, List<CandidateTargetGene> candidateTargetGenes, List<TranscriptionFactor> transcriptionFactors) {
        super(TrackType.MOTIF, clusterCode, candidateTargetGenes, transcriptionFactors);
    }

    public abstract int getDatabaseID();

    public abstract String getName();

    public abstract String getDescription();

    public abstract float getAUCValue();

    public abstract float getNEScore();

    public String getClusterCode() {
        return this.clusterCode;
    }

    public abstract Motif getBestMotif();

    public abstract List<Motif> getMotifs();

    public List<CandidateTargetGene> getCandidateTargetGenes() {
        return this.candidateTargetGenes;
    }

    public List<TranscriptionFactor> getTranscriptionFactors() {
        return this.transcriptionFactors;
    }

    public TranscriptionFactor getBestTranscriptionFactor() {
        if (getTranscriptionFactors().isEmpty()) return null;
        else return getTranscriptionFactors().get(0);
    }
}
