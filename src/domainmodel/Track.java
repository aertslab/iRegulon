package domainmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Track extends AbstractTrack implements Comparable<Track> {
    final private String name;
    final private int rank;
    final private String description;
    final private int featureID;
    private final float neScore;
    private final float aucValue;

    public Track(String name, List<CandidateTargetGene> candidateTargetGenes,
                 List<TranscriptionFactor> transcriptionFactors, float neScore, String clusterCode, int clusterNumber,
                 float aucValue, int rank, String description, int featureID, int jobID) {
        super(clusterCode, candidateTargetGenes, transcriptionFactors);
        this.name = name;
        Collections.sort(new ArrayList<CandidateTargetGene>(this.candidateTargetGenes));
        Collections.sort(new ArrayList<TranscriptionFactor>(this.transcriptionFactors));
        this.neScore = neScore;
        setClusterNumber(clusterNumber);
        this.aucValue = aucValue;
        this.rank = rank;
        this.description = description;
        this.featureID = featureID;
    }

    public int getRank() {
        return this.rank;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public float getAUCValue() {
        return this.aucValue;
    }

    public float getNEScore() {
        return this.neScore;
    }

    public int getDatabaseID() {
        return this.featureID;
    }

    public Track getBestTrack() {
        return this;
    }

    public List<Track> getTracks() {
        return Collections.singletonList(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (!name.equals(track.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(final Track other) {
        return this.getName().compareTo(other.getName());
    }
}
