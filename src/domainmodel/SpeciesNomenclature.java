package domainmodel;

import infrastructure.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle {
    private static final Map<Integer, SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer, SpeciesNomenclature>();
    private static final Map<Integer, List<RankingsDatabase>> CODE2DATABASES = new HashMap<Integer, List<RankingsDatabase>>();
    private static final Map<String, SpeciesNomenclature> NOMENCLATURECODEANDASSEMBLY2NOMENCLATURE = new HashMap<String, SpeciesNomenclature>();

    static {
        for (RankingsDatabase db : RankingsDatabase.loadFromConfiguration()) {
            final List<RankingsDatabase> rankingsDatabaseList;
            if (CODE2DATABASES.containsKey(db.getSpeciesNomenclature())) {
                rankingsDatabaseList = CODE2DATABASES.get(db.getSpeciesNomenclature());
            } else {
                rankingsDatabaseList = new ArrayList<RankingsDatabase>();
                CODE2DATABASES.put(db.getSpeciesNomenclature(), rankingsDatabaseList);
            }
            rankingsDatabaseList.add(db);
        }
    }

    public static SpeciesNomenclature HOMO_SAPIENS_HG19_HGNC = new SpeciesNomenclature(1, 1, "Homo sapiens (hg19), HGNC symbols", "hg19");
    public static SpeciesNomenclature HOMO_SAPIENS_HG38_HGNC = new SpeciesNomenclature(5, 1, "Homo sapiens (hg38), HGNC symbols", "hg38");
    public static SpeciesNomenclature MUS_MUSCULUS_MM9_MGI = new SpeciesNomenclature(2, 2, "Mus musculus (mm9), MGI symbols", "mm9");
    public static SpeciesNomenclature MUS_MUSCULUS_MM10_MGI = new SpeciesNomenclature(6, 2, "Mus musculus (mm10), MGI symbols", "mm10");
    public static SpeciesNomenclature DROSOPHILA_DM3_FLYBASE = new SpeciesNomenclature(3, 3, "Drosophila melanogaster (dm3), FlyBase names", "dm3");
    public static SpeciesNomenclature DROSOPHILA_DM6_FLYBASE = new SpeciesNomenclature(4, 3, "Drosophila melanogaster (dm6), FlyBase names", "dm6");
    public static SpeciesNomenclature DANIO_RERIO_DANRER10_ZFIN = new SpeciesNomenclature(7, 6, "Danio rerio (danRer10), ZFIN symbols", "danrer10");
    public static SpeciesNomenclature UNKNOWN = new SpeciesNomenclature();

    public static SpeciesNomenclature getNomenclature(final int code) {
        return CODE2NOMENCLATURE.get(code);
    }

    public static SpeciesNomenclature getNomenclature(final int nomenclatureCode, final String assembly) {
        return NOMENCLATURECODEANDASSEMBLY2NOMENCLATURE.get(Integer.toString(nomenclatureCode) + "_" + assembly);
    }

    public static Collection<SpeciesNomenclature> getAllNomenclatures() {
        return CODE2NOMENCLATURE.values();
    }

    public static Collection<SpeciesNomenclature> getSelectableNomenclatures() {
        final List<SpeciesNomenclature> results = new ArrayList<SpeciesNomenclature>(CODE2NOMENCLATURE.values());
        results.remove(UNKNOWN);
        return Collections.unmodifiableList(results);
    }

    private final int code;
    private final int nomenclatureCode;
    private final String name;
    private final String assembly;
    private final List<RankingsDatabase> rankingsDatabases;

    private SpeciesNomenclature() {
        this(-1, -1, "?", "?");
    }

    private SpeciesNomenclature(final int code, final String name, final String assembly) {
        this(code, code, name, assembly);
    }

    private SpeciesNomenclature(final int code, final int nomenclatureCode, final String name, final String assembly) {
        this.code = code;
        this.nomenclatureCode = nomenclatureCode;
        this.name = name;
        this.assembly = assembly;
        this.rankingsDatabases = (this.code > 0) ? CODE2DATABASES.get(code) : Collections.<RankingsDatabase>emptyList();
        CODE2NOMENCLATURE.put(code, this);
        NOMENCLATURECODEANDASSEMBLY2NOMENCLATURE.put(Integer.toString(nomenclatureCode) + "_" + assembly, this);
    }

    public String getName() {
        return this.name;
    }

    public int getCode() {
        return this.code;
    }

    public int getNomenclatureCode() {
        return this.nomenclatureCode;
    }

    public String getAssembly() {
        return this.assembly;
    }

    public List<RankingsDatabase> getRankingsDatabases() {
        return rankingsDatabases;
    }

    public List<MotifCollection> getMotifCollections(RankingsDatabase.Type searchSpace) {
        final Set<MotifCollection> motifCollections = new HashSet<MotifCollection>();
        for (RankingsDatabase db : getRankingsDatabases()) {
            // Only add the rankings database if it has the specified search space ("gene" or "region" based).
            if (db.getType().equals(searchSpace)) {
                // Only add the rankings database if it has a motif collection.
                if (db.hasMotifCollection()) {
                    motifCollections.add(db.getMotifCollection());
                }
            }
        }
        final List<MotifCollection> motifCollectionArrayList = new ArrayList<MotifCollection>(motifCollections);
        Collections.sort(motifCollectionArrayList);
        // Add a "No motif collection" option at the end of the list.
        motifCollectionArrayList.add(MotifCollection.NONE);
        return motifCollectionArrayList;
    }

    public List<TrackCollection> getTrackCollections(RankingsDatabase.Type searchSpace) {
        final Set<TrackCollection> trackCollections = new HashSet<TrackCollection>();
        for (RankingsDatabase db : getRankingsDatabases()) {
            // Only add the rankings database if it has the specified search space ("gene" or "region" based).
            if (db.getType().equals(searchSpace)) {
                // Only add the rankings database if it has a track collection.
                if (db.hasTrackCollection()) {
                    trackCollections.add(db.getTrackCollection());
                }
            }
        }
        final List<TrackCollection> trackCollectionArrayList = new ArrayList<TrackCollection>(trackCollections);
        Collections.sort(trackCollectionArrayList);
        // Add a "No track collection" option at the end of the list.
        trackCollectionArrayList.add(TrackCollection.NONE);
        return trackCollectionArrayList;
    }

    public List<RankingsDatabase.Type> getSearchSpaceTypes() {
        final Set<RankingsDatabase.Type> types = new HashSet<RankingsDatabase.Type>();
        for (RankingsDatabase db : getRankingsDatabases()) {
            types.add(db.getType());
        }
        final List<RankingsDatabase.Type> result = new ArrayList<RankingsDatabase.Type>(types);
        Collections.sort(result);
        return result;
    }

    public List<GenePutativeRegulatoryRegion> getPutativeRegulatoryRegions(final MotifCollection motifCollection,
                                                                           final TrackCollection trackCollection,
                                                                           final RankingsDatabase.Type type) {
        if (RankingsDatabase.Type.REGION.equals(type)) {
            return Collections.singletonList(GenePutativeRegulatoryRegion.NONE);
        } else {
            boolean hasMotifRankingsDatabases = false;
            boolean hasTrackRankingsDatabases = false;

            final Set<GenePutativeRegulatoryRegion> genePutativeRegulatoryRegionMotifSet = new HashSet<GenePutativeRegulatoryRegion>();
            final Set<GenePutativeRegulatoryRegion> genePutativeRegulatoryRegionTrackSet = new HashSet<GenePutativeRegulatoryRegion>();
            final Set<GenePutativeRegulatoryRegion> genePutativeRegulatoryRegionSet;

            for (RankingsDatabase db : getRankingsDatabases()) {
                if (type.equals(db.getType())) {
                    if (db.hasMotifCollection() && db.getMotifCollection().equals(motifCollection)) {
                        genePutativeRegulatoryRegionMotifSet.add(db.getPutativeRegulatoryRegion());
                        hasMotifRankingsDatabases = true;
                    }
                    if (db.hasTrackCollection() && db.getTrackCollection().equals(trackCollection)) {
                        genePutativeRegulatoryRegionTrackSet.add(db.getPutativeRegulatoryRegion());
                        hasTrackRankingsDatabases = true;
                    }
                }
            }

            if (hasMotifRankingsDatabases && hasTrackRankingsDatabases) {
                // Take only those gene putative regulatory regions that are supported in both the motif and track rankings databases.
                genePutativeRegulatoryRegionSet = new TreeSet<GenePutativeRegulatoryRegion>(genePutativeRegulatoryRegionMotifSet);
                genePutativeRegulatoryRegionSet.retainAll(genePutativeRegulatoryRegionTrackSet);
            } else if (hasMotifRankingsDatabases) {
                // Take only those gene putative regulatory regions that are supported in the motif rankings databases.
                genePutativeRegulatoryRegionSet = new TreeSet<GenePutativeRegulatoryRegion>(genePutativeRegulatoryRegionMotifSet);
            } else {
                // Take only those gene putative regulatory regions that are supported in the track rankings databases.
                genePutativeRegulatoryRegionSet = new TreeSet<GenePutativeRegulatoryRegion>(genePutativeRegulatoryRegionTrackSet);
            }

            final List genePutativeRegulatoryRegionList = new ArrayList<Object>(genePutativeRegulatoryRegionSet);

            return genePutativeRegulatoryRegionList;
        }
    }

    public List<RankingsDatabase> getMotifDatabases(final MotifCollection motifCollection,
                                                    final RankingsDatabase.Type type,
                                                    final GenePutativeRegulatoryRegion region) {
        final List<RankingsDatabase> dbs = new ArrayList<RankingsDatabase>();
        for (RankingsDatabase db : getRankingsDatabases()) {
            if (type.equals(db.getType())
                    && db.getMotifCollection().equals(motifCollection)
                    && db.getPutativeRegulatoryRegion().equals(region))
                dbs.add(db);
        }
        return dbs;
    }

    public List<RankingsDatabase> getTrackDatabases(final TrackCollection trackCollection,
                                                    final RankingsDatabase.Type type,
                                                    final GenePutativeRegulatoryRegion region) {
        final List<RankingsDatabase> dbs = new ArrayList<RankingsDatabase>();
        for (RankingsDatabase db : getRankingsDatabases()) {
            if (type.equals(db.getType())
                    && db.getTrackCollection().equals(trackCollection)
                    && db.getPutativeRegulatoryRegion().equals(region))
                dbs.add(db);
        }
        return dbs;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpeciesNomenclature that = (SpeciesNomenclature) o;

        if (code != that.code) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + name.hashCode();
        return result;
    }
}
