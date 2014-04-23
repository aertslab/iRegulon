package domainmodel;

import java.util.List;

public class MotifAndTrackCluster extends AbstractMotifAndTrack {
    private final List<AbstractMotifAndTrack> motifsAndTracks;

    MotifAndTrackCluster(String clusterCode, List<AbstractMotifAndTrack> motifsAndTracks, List<TranscriptionFactor> transcriptionFactors, List<CandidateTargetGene> targetGenes) {
        super(motifsAndTracks.get(0).getTrackType(), clusterCode, targetGenes, transcriptionFactors);
        this.motifsAndTracks = motifsAndTracks;

        setClusterNumber(motifsAndTracks.get(0).getClusterNumber());

        TrackType trackType = motifsAndTracks.get(0).getTrackType();
        if (trackType.equals(TrackType.MOTIF)) {
            trackType = TrackType.MOTIF_CLUSTER;
        } else if (trackType.equals(TrackType.TRACK)) {
            trackType = TrackType.TRACK_CLUSTER;
        }
        setTrackType(trackType);
    }

    public int getDatabaseID() {
        final AbstractMotifAndTrack motifOrTrack = getBestMotifOrTrack();
        return (motifOrTrack != null) ? motifOrTrack.getDatabaseID() : -1;
    }

    public String getName() {
        final StringBuilder buffer = new StringBuilder();
        for (AbstractMotifAndTrack motifOrTrack: getMotifsAndTracks()) {
            buffer.append(motifOrTrack.getName());
            buffer.append("/");
        }
        buffer.setLength(buffer.length()-1);
        return buffer.toString();
    }

    public String getDescription() {
        return "STAMP cluster for transcription factor \"" + getBestTranscriptionFactor().getName() + "\".";
    }

    public float getAUCValue() {
        final AbstractMotifAndTrack motifsAndTracks = getBestMotifOrTrack();
        return (motifsAndTracks != null) ? motifsAndTracks.getAUCValue() : Float.NaN;
    }

    public float getNEScore(){
        final AbstractMotifAndTrack motifOrTrack = getBestMotifOrTrack();
        return (motifOrTrack != null) ? motifOrTrack.getNEScore() : Float.NaN;
    }

    public AbstractMotifAndTrack getBestMotifOrTrack() {
        if (getMotifsAndTracks().isEmpty()) return null;
        else return getMotifsAndTracks().get(0);
    }

    public List<AbstractMotifAndTrack> getMotifsAndTracks() {
        return motifsAndTracks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotifAndTrackCluster that = (MotifAndTrackCluster) o;

        if (!motifsAndTracks.equals(that.motifsAndTracks)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return motifsAndTracks.hashCode();
    }
}
