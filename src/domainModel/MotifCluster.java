package domainmodel;

import java.util.List;

public class MotifCluster extends AbstractMotif {
    private final List<Motif> motifs;

    MotifCluster(int clusterCode, List<Motif> motifs, List<TranscriptionFactor> transcriptionFactors, List<CandidateTargetGene> targetGenes) {
        super(clusterCode, targetGenes, transcriptionFactors);
        this.motifs = motifs;
    }

    public String getID() {
        final StringBuilder buffer = new StringBuilder();
        for (Motif motif: getMotifs()) {
            buffer.append(motif.getID());
            buffer.append("/");
        }
        buffer.setLength(buffer.length()-1);
        return buffer.toString();
    }

    public float getAUCValue() {
        final Motif motif = getBestMotif();
        return (motif != null) ? motif.getAUCValue() : Float.NaN;
    }

    public float getNEScore(){
        final Motif motif = getBestMotif();
        return (motif != null) ? motif.getNEScore() : Float.NaN;
    }

    public Motif getBestMotif() {
        if (getMotifs().isEmpty()) return null;
        else return getMotifs().get(0);
    }

    public List<Motif> getMotifs() {
        return motifs;
    }
}
