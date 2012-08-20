package view.resultspanel.transcriptionfactorview;

import domainmodel.GeneIdentifier;
import domainmodel.Motif;
import domainmodel.TranscriptionFactor;


public final class EnrichedTranscriptionFactor implements Comparable<EnrichedTranscriptionFactor> {
    private final TranscriptionFactor transcriptionFactor;
    private final Motif motif;

    public EnrichedTranscriptionFactor(final TranscriptionFactor transcriptionFactor, final Motif motif) {
        this.transcriptionFactor = transcriptionFactor;
        this.motif = motif;
    }

    public Motif getMotif() {
        return motif;
    }

    public TranscriptionFactor getTranscriptionFactor() {
        return transcriptionFactor;
    }

    public GeneIdentifier getTranscriptionFactorID() {
        return transcriptionFactor.getGeneID();
    }

    public String getMotifID() {
        return motif.getName();
    }

    public float getNES() {
        return motif.getNEScore();
    }

    public float getOrthologousIdentity() {
        return transcriptionFactor.getMinOrthologousIdentity();
    }

    public String getOrthologousSpecies() {
        return transcriptionFactor.getOrthologousSpecies();
    }

    public String getOrthologousGeneID() {
        return transcriptionFactor.getOrthologousGeneName();
    }

    public float getMotifSimilarityFDR() {
        return transcriptionFactor.getMaxMotifSimilarityFDR();
    }

    public String getSimilarMotifID() {
        return transcriptionFactor.getSimilarMotifName();
    }

    public String getSimilarMotifDescription() {
        return transcriptionFactor.getSimilarMotifDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnrichedTranscriptionFactor that = (EnrichedTranscriptionFactor) o;

        if (!motif.equals(that.motif)) return false;
        if (!transcriptionFactor.equals(that.transcriptionFactor)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = transcriptionFactor.hashCode();
        result = 31 * result + motif.hashCode();
        return result;
    }

    public int compareTo(final EnrichedTranscriptionFactor other) {
        int r = Float.compare(other.getNES(), this.getNES());
        if (r != 0) return r;
        return this.transcriptionFactor.compareTo(other.transcriptionFactor);
    }
}
