package domainmodel;

import java.awt.*;
import java.util.List;


public abstract class AbstractMotifAndTrack {
    protected TrackType trackType;
    protected final String clusterCode;
    protected int clusterNumber = 0;
    protected final List<CandidateTargetGene> candidateTargetGenes;
    protected final List<TranscriptionFactor> transcriptionFactors;

    private static final ClusterColors clusterColors = new ClusterColors();


    public AbstractMotifAndTrack(TrackType trackType, String clusterCode, List<CandidateTargetGene> candidateTargetGenes, List<TranscriptionFactor> transcriptionFactors) {

        this.trackType = trackType;
        this.clusterCode = clusterCode;
        this.candidateTargetGenes = candidateTargetGenes;
        this.transcriptionFactors = transcriptionFactors;
    }

    public enum TrackType {
        MOTIF,
        TRACK,
        MOTIF_CLUSTER,
        TRACK_CLUSTER
    }

    public abstract int getDatabaseID();

    public abstract String getName();

    public abstract String getDescription();

    public abstract float getAUCValue();

    public abstract float getNEScore();

    public TrackType getTrackType() {
        return this.trackType;
    }

    protected void setTrackType(TrackType trackType) {
        this.trackType = trackType;
    }

    protected void setClusterNumber(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    public boolean isMotif() {
        return getTrackType().equals(TrackType.MOTIF);
    }

    public boolean isTrack() {
        return getTrackType().equals(TrackType.TRACK);
    }

    public boolean isMotifCluster() {
        return getTrackType().equals(TrackType.MOTIF_CLUSTER);
    }

    public boolean isTrackCluster() {
        return getTrackType().equals(TrackType.TRACK_CLUSTER);
    }

    public String getClusterCode() {
        return this.clusterCode;
    }

    public int getClusterNumber() {
        return this.clusterNumber;
    }

    public Color getClusterColor() {
        return clusterColors.getClusterColor(getClusterNumber());
    }

    public int getClusterColorInt() {
        return clusterColors.getClusterColorInt(getClusterNumber());
    }

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
