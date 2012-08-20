package domainmodel;

import java.util.List;

public class MotifCluster extends AbstractMotif {
    private final List<Motif> motifs;

    MotifCluster(int clusterCode, List<Motif> motifs, List<TranscriptionFactor> transcriptionFactors, List<CandidateTargetGene> targetGenes) {
        super(clusterCode, targetGenes, transcriptionFactors);
        this.motifs = motifs;
    }

    public String getName() {
        final StringBuilder buffer = new StringBuilder();
        for (Motif motif: getMotifs()) {
            buffer.append(motif.getName());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotifCluster that = (MotifCluster) o;

        if (!motifs.equals(that.motifs)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return motifs.hashCode();
    }
}
