package domainmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Motif extends AbstractMotif implements Comparable<Motif> {
    final private String name;
    final private int rank;
	final private String description;
	final private int featureID;
    private final float neScore;
    private final float aucValue;

	public Motif(String name, List<CandidateTargetGene> candidateTargetGenes,
			List<TranscriptionFactor> transcriptionFactors, float neScore, int clusterCode
			, float aucValue, int rank, String description, int featureID, int jobID){
        super(clusterCode, candidateTargetGenes, transcriptionFactors);
        this.name = name;
        Collections.sort(new ArrayList<CandidateTargetGene>(this.candidateTargetGenes));
        Collections.sort(new ArrayList<TranscriptionFactor>(this.transcriptionFactors));
        this.rank = rank;
		this.description = description;
		this.featureID = featureID;
        this.neScore = neScore;
        this.aucValue = aucValue;
	}

    public int getRank(){
		return this.rank;
	}

    public String getName() {
        return this.name;
    }

    public String getDescription(){
		return this.description;
	}

    public float getAUCValue() {
        return this.aucValue;
    }

    public float getNEScore(){
        return this.neScore;
    }

	public int getDatabaseID(){
		return this.featureID;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Motif motif = (Motif) o;

        if (!name.equals(motif.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int compareTo(final Motif other) {
        return this.getName().compareTo(other.getName());
    }
}
