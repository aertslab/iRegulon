package domainmodel;

import view.parametersform.IRegulonType;

import java.util.*;


public class Results {
	private final List<Motif> motifs;
	private final InputParameters inputParameters;
	
	public Results(final Collection<Motif> motifs, final InputParameters inputParameters){
		this.motifs = new ArrayList<Motif>(motifs);
        Collections.sort(this.motifs, new Comparator<Motif>() {
            @Override
            public int compare(Motif o1, Motif o2) {
                return new Integer(o1.getRank()).compareTo(o2.getRank());
            }
        });
		this.inputParameters = inputParameters;
	}
	
	public List<Motif> getMotifs(){
		return this.motifs;
	}
	
	public boolean hasParameters(){
		return (this.inputParameters != null);
	}

	public InputParameters getParameters() {
		return this.inputParameters;
	}
	
	public Collection<GeneIdentifier> getGenes(){
		return this.inputParameters.getGenes();
	}
	
	public float getEScore(){
		return this.inputParameters.getEScore();
	}
	
	public int getThresholdForVisualisation(){
		return this.inputParameters.getThresholdForVisualisation();
	}
	
	public float getROCthresholdAUC(){
		return this.inputParameters.getROCthresholdAUC();
	}
	
	public SpeciesNomenclature getSpeciesNomenclature(){
		return this.inputParameters.getSpeciesNomenclature();
	}
	
	public IRegulonType getIRegulonType(){
		return this.inputParameters.getIRegulonType();
	}
	
	public String getName(){
		return this.inputParameters.getName();
	}
	
	public float getMinOrthologous(){
		return this.inputParameters.getMinOrthologous();
	}
	
	public float getMaxMotifSimilarityFDR(){
		return this.inputParameters.getMaxMotifSimilarityFDR();
	}
	
	public boolean isRegionBased(){
		return this.inputParameters.isRegionBased();
	}
	
	public boolean isGeneBased(){
		return ! this.inputParameters.isGeneBased();
	}
	
	public String getDatabaseName(){
		return this.inputParameters.getDatabase().getName();
	}
	
	public String getDatabase(){
		return this.inputParameters.getDatabase().getCode();
	}
	
	public float getOverlap(){
		return this.inputParameters.getOverlap();
	}
	
	public String getDelineationName(){
		if (this.isDelineationBased()){
			return this.inputParameters.getDelineation().getName();
		}
		return null;
	}
	
	public Delineation getDelineation(){
		return this.inputParameters.getDelineation();
	}
	
	public String getDelineationDatabase(){
		return this.inputParameters.getDelineation().getCode();
	}
	
	public boolean isDelineationBased(){
		return this.inputParameters.isDelineationBased();
	}
	
	public int getUpstream(){
		return this.inputParameters.getUpstream();
	}
	
	public int getDownstream(){
		return this.inputParameters.getDownstream();
	}

    public List<MotifCluster> getMotifClusters(final Set<String> geneIDs) {
        // 1. Group motifs according to STAMP clusters ...
        final Map<Integer, List<Motif>> code2motifs = new HashMap<Integer, List<Motif>>();
        for (Motif curMotif: getMotifs()) {
            final int curCode = curMotif.getClusterCode();
            final List<Motif> bucket;
            if (code2motifs.containsKey(curCode)) {
                bucket = code2motifs.get(curCode);
            } else {
                bucket = new ArrayList<Motif>();
                code2motifs.put(curCode, bucket);
            }
            bucket.add(curMotif);
        }

        // 2. Sort clusters according to maximum NESCore ...
        final List<List<Motif>> clusters = new ArrayList<List<Motif>>(code2motifs.values());
        Collections.sort(clusters, new Comparator<List<Motif>>() {
            private float getMaximumNEScore(List<Motif> motifs) {
                return Collections.min(motifs, new MotifComparator()).getNEScore();
            }

            @Override
            public int compare(List<Motif> o1, List<Motif> o2) {
                return Float.compare(getMaximumNEScore(o2), getMaximumNEScore(o1));
            }
        });

        // 3. Iterate motifs and translate them to MotifCluster objects ...
        final Set<String> alreadyProcessedTFIDs = new HashSet<String>();
        final List<MotifCluster> result = new ArrayList<MotifCluster>();
        for(List<Motif> motifs: clusters) {
            final int clusterCode = motifs.get(0).getClusterCode();

            final List<Motif> sortedMotifs = new ArrayList<Motif>(motifs);
            Collections.sort(sortedMotifs, new MotifComparator());

            final List<TranscriptionFactor> transcriptionFactors = combineTranscriptionFactors(motifs, geneIDs);
            if (transcriptionFactors.isEmpty()) continue;
            final String curTFID = transcriptionFactors.get(0).getGeneID().getGeneName();
            if (alreadyProcessedTFIDs.contains(curTFID)) continue;

            result.add(new MotifCluster(clusterCode, sortedMotifs, transcriptionFactors, combineTargetGenes(motifs)));
            alreadyProcessedTFIDs.add(curTFID);
        }
        return result;
    }

    private List<TranscriptionFactor> combineTranscriptionFactors(final List<Motif> motifs, final Set<String> geneIDs) {
        final Map<TranscriptionFactor, TranscriptionFactorAttributes> tf2attributes = new HashMap<TranscriptionFactor, TranscriptionFactorAttributes>();
        for (Motif motif : motifs) {
            for (TranscriptionFactor tf : motif.getTranscriptionFactors()) {
                if (tf2attributes.containsKey(tf)) {
                    final TranscriptionFactorAttributes attributes = tf2attributes.get(tf);
                    attributes.update(motif);
                } else {
                    final TranscriptionFactorAttributes attributes = new TranscriptionFactorAttributes(tf, motif.getNEScore(), geneIDs.contains(tf.getName()));
                    tf2attributes.put(tf, attributes);
                }
            }
        }

        final List<TranscriptionFactorAttributes> tfAttributes = new ArrayList<TranscriptionFactorAttributes>(tf2attributes.values());
        Collections.sort(tfAttributes);

        final List<TranscriptionFactor> result = new ArrayList<TranscriptionFactor>();
        for (TranscriptionFactorAttributes attributes: tfAttributes) {
            result.add(attributes.createTranscriptionFactor());
        }

        return result;
    }

    private List<CandidateTargetGene> combineTargetGenes(final List<Motif> motifs) {
        final Map<GeneIdentifier, TargetGeneAttributes> geneID2attributes = new HashMap<GeneIdentifier, TargetGeneAttributes>();
        for (Motif motif : motifs) {
            for (CandidateTargetGene targetGene : motif.getCandidateTargetGenes()) {
                if (geneID2attributes.containsKey(targetGene.getGeneID())) {
                    final TargetGeneAttributes curAttributes = geneID2attributes.get(targetGene.getGeneID());
                    curAttributes.update(targetGene.getRank());
                } else {
                    geneID2attributes.put(targetGene.getGeneID(), new TargetGeneAttributes(targetGene.getRank()));
                }
            }
        }

        final List<CandidateTargetGene> targetGenes = new ArrayList<CandidateTargetGene>();
        for (GeneIdentifier ID: geneID2attributes.keySet()) {
            final TargetGeneAttributes curAttributes = geneID2attributes.get(ID);
            targetGenes.add(new CandidateTargetGene(ID, curAttributes.getMaxRank(), curAttributes.getMotifCount()));
        }

        Collections.sort(targetGenes);

        return targetGenes;
    }

    private static class TargetGeneAttributes {
        private int maxRank = 0;
        private int motifCount = 0;

        public TargetGeneAttributes(final int rank) {
            motifCount = 1;
            maxRank = rank;
        }

        public void update(final int rank) {
            incrementMotifCount();
            updateRank(rank);
        }

        private void updateRank(final int rank) {
            maxRank = (rank > maxRank) ? rank : maxRank;
        }

        private void incrementMotifCount() {
            motifCount++;
        }

        public int getMaxRank() {
            return maxRank;
        }

        public int getMotifCount() {
            return motifCount;
        }
    }

    private static class TranscriptionFactorAttributes implements Comparable<TranscriptionFactorAttributes> {
        private final TranscriptionFactor transcriptionFactor;
        private final boolean isPresentInSignature;

        private float NEScore;
        private final Set<AbstractMotif> motifs = new HashSet<AbstractMotif>();

        private TranscriptionFactorAttributes(TranscriptionFactor transcriptionFactor, float NEScore, boolean presentInSignature) {
            this.transcriptionFactor = transcriptionFactor;
            this.isPresentInSignature = presentInSignature;
            this.NEScore = NEScore;
        }

        public void update(final Motif motif) {
            NEScore = (motif.getNEScore() > NEScore) ? motif.getNEScore() : NEScore;
            motifs.add(motif);
        }

        public TranscriptionFactor createTranscriptionFactor() {
            return new TranscriptionFactor(transcriptionFactor.getGeneID(),
                    transcriptionFactor.getMinOrthologousIdentity(),
                    transcriptionFactor.getMaxMotifSimilarityFDR(),
                    transcriptionFactor.getSimilarMotifName(),
                    transcriptionFactor.getSimilarMotifDescription(),
                    transcriptionFactor.getOrthologousGeneName(),
                    transcriptionFactor.getOrthologousSpecies(),
                    motifs);
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

    private static class MotifComparator implements Comparator<Motif> {
        public int compare(Motif o1, Motif o2) {
            return Float.compare(o2.getNEScore(), o1.getNEScore());
        }
    }
}
