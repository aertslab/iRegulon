package domainmodel;

import java.util.List;

public class MotifCluster {
    private final int clusterCode;
    private final List<Motif> motifs;
    private final List<TranscriptionFactor> transcriptionFactors;
    private final List<CandidateTargetGene> targetGenes;

    MotifCluster(int clusterCode, List<Motif> motifs, List<TranscriptionFactor> transcriptionFactors, List<CandidateTargetGene> targetGenes) {
        this.clusterCode = clusterCode;
        this.motifs = motifs;
        this.transcriptionFactors = transcriptionFactors;
        this.targetGenes = targetGenes;
    }

    public int getClusterCode() {
        return clusterCode;
    }

    public float getAUCValue() {
        final Motif motif = getBestMotif();
        return (motif == null) ? Float.NaN : motif.getAucValue();
    }

    public float getNEScore() {
        final Motif motif = getBestMotif();
        return (motif == null) ? Float.NaN : motif.getNeScore();
    }

    public Motif getBestMotif() {
        if (getMotifs().isEmpty()) return null;
        else return getMotifs().get(0);
    }

    public List<Motif> getMotifs() {
        return motifs;
    }

    public TranscriptionFactor getBestTranscriptionFactor() {
        if (getTranscriptionFactors().isEmpty()) return null;
        else return getTranscriptionFactors().get(0);
    }

    public List<TranscriptionFactor> getTranscriptionFactors() {
        return transcriptionFactors;
    }

    public List<CandidateTargetGene> getTargetGenes() {
        return targetGenes;
    }
}
