package domainmodel;


import java.util.List;

public abstract class AbstractTrack extends AbstractMotifAndTrack {

    public AbstractTrack(String clusterCode, List<CandidateTargetGene> candidateTargetGenes, List<TranscriptionFactor> transcriptionFactors) {
        super(TrackType.TRACK, clusterCode, candidateTargetGenes, transcriptionFactors);
    }

    public abstract int getDatabaseID();

    public abstract String getName();

    public abstract String getDescription();

    public abstract float getAUCValue();

    public abstract float getNEScore();

    public String getClusterCode() {
        return this.clusterCode;
    }

    public abstract Track getBestTrack();

    public abstract List<Track> getTracks();

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
