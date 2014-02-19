package domainmodel;

import view.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle {
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();
    private static final Map<Integer, List<RankingsDatabase>> CODE2DATABASES = new HashMap<Integer, List<RankingsDatabase>>();
    static {
        for (RankingsDatabase db: RankingsDatabase.loadFromConfiguration()) {
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

	public static SpeciesNomenclature HOMO_SAPIENS_HGNC = new SpeciesNomenclature(1, "Homo sapiens, HGNC symbols", "hg19");
	public static SpeciesNomenclature MUS_MUSCULUS_MGI = new SpeciesNomenclature(2, "Mus musculus, MGI symbols", "mm9");
	public static SpeciesNomenclature DROSOPHILA_FlyBase = new SpeciesNomenclature(3, "Drosophila melanogaster, FlyBase names", "dm3");
	public static SpeciesNomenclature DROSOPHILA_CG_numbers = new SpeciesNomenclature (4, "Drosophila melanogaster, CG-numbers", "dm3");
	//public static SpeciesNomenclature DROSOPHILA_FBgn_numbers = new SpeciesNomenclature (5, "Drosophila melanogaster, FBgn", "dm3");
	public static SpeciesNomenclature UNKNOWN = new SpeciesNomenclature();

    public static SpeciesNomenclature getNomenclature(final int code) {
		return CODE2NOMENCLATURE.get(code);
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
	private final String name;
    private final String assembly;
    private final List<RankingsDatabase> rankingsDatabases;

    private SpeciesNomenclature() {
        this(-1, "?", "?");
    }
	
	private SpeciesNomenclature(final int code, final String name, final String assembly) {
		this.code = code;
		this.name = name;
        this.assembly = assembly;
        this.rankingsDatabases = (this.code > 0) ? CODE2DATABASES.get(code): Collections.<RankingsDatabase>emptyList();
        CODE2NOMENCLATURE.put(code, this);
	}

    public String getName() {
        return this.name;
    }

	public int getCode(){
		return this.code;
	}

    public String getAssembly(){
        return this.assembly;
    }

    public List<RankingsDatabase> getRankingsDatabases() {
        return rankingsDatabases;
    }

    public List<ChipCollection> getChipCollections() {
        final Set<ChipCollection> chipCollections = new HashSet<ChipCollection>();
        for (RankingsDatabase db: getRankingsDatabases()) {
            // When the rankings database doesn't contain a ChIP collection, exclude it.
            if (db.hasChipCollection()) {
                chipCollections.add(db.getChipCollection());
            }
        }
        final List<ChipCollection> chipCollectionArrayList = new ArrayList<ChipCollection>(chipCollections);
        Collections.sort(chipCollectionArrayList);
        // Add a "No ChIP collection" option at the end of the list.
        chipCollectionArrayList.add(ChipCollection.NONE);
        return chipCollectionArrayList;
    }

    public List<MotifCollection> getMotifCollections() {
        final Set<MotifCollection> motifCollections = new HashSet<MotifCollection>();
        for (RankingsDatabase db: getRankingsDatabases()) {
            // When the rankings database doesn't contain a motif collection, exclude it.
            if (db.hasMotifCollection()) {
                motifCollections.add(db.getMotifCollection());
            }
        }
        final List<MotifCollection> motifCollectionArrayList = new ArrayList<MotifCollection>(motifCollections);
        Collections.sort(motifCollectionArrayList);
        // Add a "No motif collection" option at the end of the list.
        motifCollectionArrayList.add(MotifCollection.NONE);
        return motifCollectionArrayList;
    }

    public List<RankingsDatabase.Type> getSearchSpaceTypes() {
        final Set<RankingsDatabase.Type> types = new HashSet<RankingsDatabase.Type>();
        for (RankingsDatabase db: getRankingsDatabases()) {
            types.add(db.getType());
        }
        final List<RankingsDatabase.Type> result = new ArrayList<RankingsDatabase.Type>(types);
        Collections.sort(result);
        return result;
    }

    public List<GenePutativeRegulatoryRegion> getPutativeRegulatoryRegions(final ChipCollection chipCollection,
                                                                           final MotifCollection motifCollection,
                                                                           final RankingsDatabase.Type type) {
        if (RankingsDatabase.Type.REGION.equals(type)) {
            return Collections.singletonList(GenePutativeRegulatoryRegion.NONE);
        } else {
            boolean hasChipRankingsDatabases = false;
            boolean hasMotifRankingsDatabases = false;

            final Set<GenePutativeRegulatoryRegion> genePutativeRegulatoryRegionChipSet = new HashSet<GenePutativeRegulatoryRegion>();
            final Set<GenePutativeRegulatoryRegion> genePutativeRegulatoryRegionMotifSet = new HashSet<GenePutativeRegulatoryRegion>();
            final Set<GenePutativeRegulatoryRegion> genePutativeRegulatoryRegionSet;

            for (RankingsDatabase db : getRankingsDatabases()) {
                if (type.equals(db.getType())) {
                    if (db.hasChipCollection() && db.getChipCollection().equals(chipCollection)) {
                        genePutativeRegulatoryRegionChipSet.add(db.getPutativeRegulatoryRegion());
                        hasChipRankingsDatabases = true;
                    }
                    if (db.hasMotifCollection() && db.getMotifCollection().equals(motifCollection)) {
                        genePutativeRegulatoryRegionMotifSet.add(db.getPutativeRegulatoryRegion());
                        hasMotifRankingsDatabases = true;
                    }
                }
            }

            if (hasChipRankingsDatabases && hasMotifRankingsDatabases) {
                // Take only those gene putative regulatory regions that are supported in both the ChIP and motif rankings databases.
                genePutativeRegulatoryRegionSet = new TreeSet<GenePutativeRegulatoryRegion>(genePutativeRegulatoryRegionChipSet);
                genePutativeRegulatoryRegionSet.retainAll(genePutativeRegulatoryRegionMotifSet);
            } else if (hasChipRankingsDatabases) {
                // Take only those gene putative regulatory regions that are supported in the ChIP rankings databases.
                genePutativeRegulatoryRegionSet = new TreeSet<GenePutativeRegulatoryRegion>(genePutativeRegulatoryRegionChipSet);
            } else {
                // Take only those gene putative regulatory regions that are supported in the motif rankings databases.
                genePutativeRegulatoryRegionSet = new TreeSet<GenePutativeRegulatoryRegion>(genePutativeRegulatoryRegionMotifSet);
            }

            final List genePutativeRegulatoryRegionList = new ArrayList<Object>(genePutativeRegulatoryRegionSet);

            return genePutativeRegulatoryRegionList;
        }
    }

    public List<RankingsDatabase> getChipDatabases(final ChipCollection chipCollection,
                                                   final RankingsDatabase.Type type,
                                                   final GenePutativeRegulatoryRegion region) {
        final List<RankingsDatabase> dbs = new ArrayList<RankingsDatabase>();
        for (RankingsDatabase db: getRankingsDatabases()) {
            if (type.equals(db.getType())
                    && db.getChipCollection().equals(chipCollection)
                    && db.getPutativeRegulatoryRegion().equals(region))
                dbs.add(db);
        }
        return dbs;
    }

    public List<RankingsDatabase> getMotifDatabases(final MotifCollection motifCollection,
                                                    final RankingsDatabase.Type type,
                                                    final GenePutativeRegulatoryRegion region) {
        final List<RankingsDatabase> dbs = new ArrayList<RankingsDatabase>();
        for (RankingsDatabase db: getRankingsDatabases()) {
            if (type.equals(db.getType())
                 && db.getMotifCollection().equals(motifCollection)
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
