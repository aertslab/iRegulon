package domainmodel;


import java.util.List;

public abstract class AbstractMotif {
    protected final int clusterCode;
    protected final List<CandidateTargetGene> candidateTargetGenes;
    protected final List<TranscriptionFactor> transcriptionFactors;


    public AbstractMotif(int clusterCode, List<CandidateTargetGene> candidateTargetGenes, List<TranscriptionFactor> transcriptionFactors) {
        this.clusterCode = clusterCode;
        this.candidateTargetGenes = candidateTargetGenes;
        this.transcriptionFactors = transcriptionFactors;

    }

    public abstract String getName();

    public abstract float getAUCValue();

    public abstract float getNEScore();

    public int getClusterCode(){
        return this.clusterCode;
    }

    public List<CandidateTargetGene> getCandidateTargetGenes(){
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
