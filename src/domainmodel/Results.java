package domainmodel;

import view.parametersform.IRegulonType;

import java.util.*;


public class Results {
    private final List<Motif> motifs;
    private final List<Track> tracks;
    private final InputParameters inputParameters;

    public Results(final Collection<AbstractMotifAndTrack> motifsAndTracks, final InputParameters inputParameters) {
        this.motifs = new ArrayList<Motif>();
        this.tracks = new ArrayList<Track>();

        for (AbstractMotifAndTrack motifOrTrack : motifsAndTracks) {
            if (motifOrTrack.isMotif()) {
                this.motifs.add((Motif) motifOrTrack);
            } else if (motifOrTrack.isTrack()) {
                this.tracks.add((Track) motifOrTrack);
            }
        }

        Collections.sort(this.motifs, new Comparator<Motif>() {
            @Override
            public int compare(Motif o1, Motif o2) {
                return new Integer(o1.getRank()).compareTo(o2.getRank());
            }
        });

        Collections.sort(this.tracks, new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                return new Integer(o1.getRank()).compareTo(o2.getRank());
            }
        });

        this.inputParameters = inputParameters;
    }

    public List<Motif> getMotifs() {
        return this.motifs;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public boolean hasParameters() {
        return (this.inputParameters != null);
    }

    public InputParameters getParameters() {
        return this.inputParameters;
    }

    public Collection<GeneIdentifier> getGenes() {
        return this.inputParameters.getGenes();
    }

    public float getEScore() {
        return this.inputParameters.getEScore();
    }

    public int getThresholdForVisualisation() {
        return this.inputParameters.getThresholdForVisualisation();
    }

    public float getROCthresholdAUC() {
        return this.inputParameters.getROCthresholdAUC();
    }

    public SpeciesNomenclature getSpeciesNomenclature() {
        return this.inputParameters.getSpeciesNomenclature();
    }

    public IRegulonType getIRegulonType() {
        return this.inputParameters.getIRegulonType();
    }

    public String getName() {
        return this.inputParameters.getName();
    }

    public String getMotifCollection() {
        return this.inputParameters.getMotifCollection();
    }

    public String getTrackCollection() {
        return this.inputParameters.getTrackCollection();
    }

    public float getMinOrthologous() {
        return this.inputParameters.getMinOrthologous();
    }

    public float getMaxMotifSimilarityFDR() {
        return this.inputParameters.getMaxMotifSimilarityFDR();
    }

    public boolean hasMotifCollection() {
        return ! getMotifCollection().equals(MotifCollection.NONE.getDescription());
    }

    public boolean hasTrackCollection() {
        return ! getTrackCollection().equals(TrackCollection.NONE.getDescription());
    }

    public boolean isRegionBased() {
        return this.inputParameters.isRegionBased();
    }

    public boolean isGeneBased() {
        return !this.inputParameters.isGeneBased();
    }

    public String getMotifRankingsDatabaseName() {
        return this.inputParameters.getMotifRankingsDatabase().getName();
    }

    public String getMotifRankingsDatabase() {
        return this.inputParameters.getMotifRankingsDatabase().getCode();
    }

    public String getTrackRankingsDatabaseName() {
        return this.inputParameters.getTrackRankingsDatabase().getName();
    }

    public String getTrackRankingsDatabase() {
        return this.inputParameters.getTrackRankingsDatabase().getCode();
    }

    public float getOverlap() {
        return this.inputParameters.getOverlap();
    }

    public String getDelineationName() {
        if (this.isDelineationBased()) {
            return this.inputParameters.getDelineation().getName();
        }
        return null;
    }

    public Delineation getDelineation() {
        return this.inputParameters.getDelineation();
    }

    public String getDelineationDatabase() {
        return this.inputParameters.getDelineation().getCode();
    }

    public boolean isDelineationBased() {
        return this.inputParameters.isDelineationBased();
    }

    public int getUpstream() {
        return this.inputParameters.getUpstream();
    }

    public int getDownstream() {
        return this.inputParameters.getDownstream();
    }

    public List<MotifAndTrackCluster> getMotifAndTrackClusters(final Set<String> geneIDs) {
        // 1. Group motifs according to STAMP cluster number.
        final Map<Integer, List<AbstractMotifAndTrack>> code2motifsAndTracks = new HashMap<Integer, List<AbstractMotifAndTrack>>();
        for (Motif curMotif : getMotifs()) {
            final int curCode = curMotif.getClusterCode();
            final List<AbstractMotifAndTrack> bucket;
            if (code2motifsAndTracks.containsKey(curCode)) {
                bucket = code2motifsAndTracks.get(curCode);
            } else {
                bucket = new ArrayList<AbstractMotifAndTrack>();
                code2motifsAndTracks.put(curCode, bucket);
            }
            bucket.add(curMotif);
        }

        // 2. Group tracks in clusters by transcription factor name.
        for (Track curTrack : getTracks()) {
            final int curCode = curTrack.getClusterCode();
            final List<AbstractMotifAndTrack> bucket;
            if (code2motifsAndTracks.containsKey(curCode)) {
                bucket = code2motifsAndTracks.get(curCode);
            } else {
                bucket = new ArrayList<AbstractMotifAndTrack>();
                code2motifsAndTracks.put(curCode, bucket);
            }
            bucket.add(curTrack);
        }

        // 3. Sort clusters according to maximum NESCore.
        final List<List<AbstractMotifAndTrack>> clusters = new ArrayList<List<AbstractMotifAndTrack>>(code2motifsAndTracks.values());
        Collections.sort(clusters, new Comparator<List<AbstractMotifAndTrack>>() {
            private float getMaximumNEScore(List<AbstractMotifAndTrack> motifsAndTracks) {
                return Collections.min(motifsAndTracks, new AbstractMotifAndTrackComparator()).getNEScore();
            }

            @Override
            public int compare(List<AbstractMotifAndTrack> o1, List<AbstractMotifAndTrack> o2) {
                return Float.compare(getMaximumNEScore(o2), getMaximumNEScore(o1));
            }
        });

        // 4. Iterate motifs and tracks and translate them to MotifAndTrackCluster objects.
        final Set<String> alreadyProcessedTFIDsForMotifs = new HashSet<String>();
        final Set<String> alreadyProcessedTFIDsForTracks = new HashSet<String>();
        final List<MotifAndTrackCluster> result = new ArrayList<MotifAndTrackCluster>();
        for (List<AbstractMotifAndTrack> motifsAndTracks : clusters) {
            final int clusterCode = motifsAndTracks.get(0).getClusterCode();

            final List<AbstractMotifAndTrack> sortedMotifsOrTracks = new ArrayList<AbstractMotifAndTrack>(motifsAndTracks);
            Collections.sort(sortedMotifsOrTracks, new AbstractMotifAndTrackComparator());

            final List<TranscriptionFactor> transcriptionFactors = combineTranscriptionFactors(motifsAndTracks, geneIDs);
            if (transcriptionFactors.isEmpty()) continue;

            final String curTFID = transcriptionFactors.get(0).getGeneID().getGeneName();

            if (motifsAndTracks.get(0).isMotif()) {
                if (alreadyProcessedTFIDsForMotifs.contains(curTFID)) continue;

                result.add(new MotifAndTrackCluster(clusterCode, sortedMotifsOrTracks, transcriptionFactors, combineTargetGenes(motifsAndTracks)));

                alreadyProcessedTFIDsForMotifs.add(curTFID);
            } else if (motifsAndTracks.get(0).isTrack()) {
                if (alreadyProcessedTFIDsForTracks.contains(curTFID)) continue;

                result.add(new MotifAndTrackCluster(clusterCode, sortedMotifsOrTracks, transcriptionFactors, combineTargetGenes(motifsAndTracks)));

                alreadyProcessedTFIDsForTracks.add(curTFID);
            }
        }
        return result;
    }

    private List<TranscriptionFactor> combineTranscriptionFactors(final List<AbstractMotifAndTrack> motifsAndTracks, final Set<String> geneIDs) {
        final Map<TranscriptionFactor, TranscriptionFactorAttributes> tf2attributes = new HashMap<TranscriptionFactor, TranscriptionFactorAttributes>();
        for (AbstractMotifAndTrack motifOrTrack : motifsAndTracks) {
            for (TranscriptionFactor tf : motifOrTrack.getTranscriptionFactors()) {
                if (tf2attributes.containsKey(tf)) {
                    final TranscriptionFactorAttributes attributes = tf2attributes.get(tf);
                    attributes.update(motifOrTrack);
                } else {
                    final TranscriptionFactorAttributes attributes = new TranscriptionFactorAttributes(tf, motifOrTrack, geneIDs.contains(tf.getName()));
                    tf2attributes.put(tf, attributes);
                }
            }
        }

        final List<TranscriptionFactorAttributes> tfAttributes = new ArrayList<TranscriptionFactorAttributes>(tf2attributes.values());
        Collections.sort(tfAttributes);

        final List<TranscriptionFactor> result = new ArrayList<TranscriptionFactor>();
        final Set<String> IDs = new HashSet<String>();
        for (TranscriptionFactorAttributes attributes : tfAttributes) {
            final TranscriptionFactor factor = attributes.createTranscriptionFactor();
            if (!IDs.contains(factor.getName())) {
                result.add(factor);
                IDs.add(factor.getName());
            }
        }

        return result;
    }

    private List<CandidateTargetGene> combineTargetGenes(final List<AbstractMotifAndTrack> motifsAndTracks) {
        final Map<GeneIdentifier, TargetGeneAttributes> geneID2attributes = new HashMap<GeneIdentifier, TargetGeneAttributes>();
        for (AbstractMotifAndTrack motifOrTrack : motifsAndTracks) {
            for (CandidateTargetGene targetGene : motifOrTrack.getCandidateTargetGenes()) {
                if (geneID2attributes.containsKey(targetGene.getGeneID())) {
                    final TargetGeneAttributes curAttributes = geneID2attributes.get(targetGene.getGeneID());
                    curAttributes.update(targetGene.getRank());
                } else {
                    geneID2attributes.put(targetGene.getGeneID(), new TargetGeneAttributes(targetGene.getRank()));
                }
            }
        }

        final List<CandidateTargetGene> targetGenes = new ArrayList<CandidateTargetGene>();
        for (GeneIdentifier ID : geneID2attributes.keySet()) {
            final TargetGeneAttributes curAttributes = geneID2attributes.get(ID);
            targetGenes.add(new CandidateTargetGene(ID, curAttributes.getMinRank(), curAttributes.getMotifAndTrackCount()));
        }

        Collections.sort(targetGenes);

        return targetGenes;
    }

    private static class TargetGeneAttributes {
        private int minRank = 0;
        private int motifAndTrackCount = 0;

        public TargetGeneAttributes(final int rank) {
            motifAndTrackCount = 1;
            minRank = rank;
        }

        public void update(final int rank) {
            incrementMotifAndTrackCount();
            updateRank(rank);
        }

        private void updateRank(final int rank) {
            minRank = (rank < minRank) ? rank : minRank;
        }

        private void incrementMotifAndTrackCount() {
            motifAndTrackCount++;
        }

        public int getMinRank() {
            return minRank;
        }

        public int getMotifAndTrackCount() {
            return motifAndTrackCount;
        }
    }

    private static class TranscriptionFactorAttributes implements Comparable<TranscriptionFactorAttributes> {
        private final TranscriptionFactor transcriptionFactor;
        private final boolean isPresentInSignature;

        private float NEScore;
        private final Set<AbstractMotif> motifs = new HashSet<AbstractMotif>();
        private final Set<AbstractTrack> tracks = new HashSet<AbstractTrack>();

        private TranscriptionFactorAttributes(TranscriptionFactor transcriptionFactor, final AbstractMotifAndTrack motifOrTrack, boolean presentInSignature) {
            this.transcriptionFactor = transcriptionFactor;
            this.isPresentInSignature = presentInSignature;
            update(motifOrTrack);
        }

        public void update(final AbstractMotifAndTrack motifOrTrack) {
            NEScore = (motifOrTrack.getNEScore() > NEScore) ? motifOrTrack.getNEScore() : NEScore;
            if (motifOrTrack.isMotif()) {
                motifs.add((Motif) motifOrTrack);
            } else if (motifOrTrack.isTrack()) {
                tracks.add((Track) motifOrTrack);
            }
        }

        public TranscriptionFactor createTranscriptionFactor() {
            return new TranscriptionFactor(transcriptionFactor.getGeneID(),
                    transcriptionFactor.getMinOrthologousIdentity(),
                    transcriptionFactor.getMaxMotifSimilarityFDR(),
                    transcriptionFactor.getSimilarMotifName(),
                    transcriptionFactor.getSimilarMotifDescription(),
                    transcriptionFactor.getOrthologousGeneName(),
                    transcriptionFactor.getOrthologousSpecies(),
                    motifs,
                    tracks);
        }

        public TranscriptionFactor getTranscriptionFactor() {
            return transcriptionFactor;
        }

        public float getNEScore() {
            return NEScore;
        }

        public boolean isPresentInSignature() {
            return isPresentInSignature;
        }

        public Set<AbstractMotif> getMotifs() {
            return motifs;
        }

        public Set<AbstractTrack> getTracks() {
            return tracks;
        }

        @Override
        public int compareTo(TranscriptionFactorAttributes other) {
            if (isPresentInSignature() && !other.isPresentInSignature()) {
                return -1;
            } else if (!isPresentInSignature() && other.isPresentInSignature()) {
                return 1;
            }

            int r = Float.compare(other.getNEScore(), getNEScore());
            if (r != 0) return r;

            return getTranscriptionFactor().compareTo(other.getTranscriptionFactor());
        }
    }

    private static class AbstractMotifAndTrackComparator implements Comparator<AbstractMotifAndTrack> {
        public int compare(AbstractMotifAndTrack o1, AbstractMotifAndTrack o2) {
            return Float.compare(o2.getNEScore(), o1.getNEScore());
        }
    }
}
