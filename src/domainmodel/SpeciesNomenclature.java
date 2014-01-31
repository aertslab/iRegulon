package domainmodel;

import view.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle {
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();
    private static final Map<Integer, List<MotifRankingsDatabase>> CODE2MOTIFDATABASES = new HashMap<Integer, List<MotifRankingsDatabase>>();
    static {
        for (MotifRankingsDatabase db: MotifRankingsDatabase.loadFromConfiguration()) {
            final List<MotifRankingsDatabase> motifRankingsDatabaseList;
            if (CODE2MOTIFDATABASES.containsKey(db.getSpeciesNomenclature())) {
                motifRankingsDatabaseList = CODE2MOTIFDATABASES.get(db.getSpeciesNomenclature());
            } else {
                motifRankingsDatabaseList = new ArrayList<MotifRankingsDatabase>();
                CODE2MOTIFDATABASES.put(db.getSpeciesNomenclature(), motifRankingsDatabaseList);
            }
            motifRankingsDatabaseList.add(db);
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
    private final List<MotifRankingsDatabase> motifRankingsDatabases;

    private SpeciesNomenclature() {
        this(-1, "?", "?");
    }
	
	private SpeciesNomenclature(final int code, final String name, final String assembly) {
		this.code = code;
		this.name = name;
        this.assembly = assembly;
        this.motifRankingsDatabases = (this.code > 0) ? CODE2MOTIFDATABASES.get(code): Collections.<MotifRankingsDatabase>emptyList();
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

    public List<MotifRankingsDatabase> getMotifRankingsDatabases() {
        return motifRankingsDatabases;
    }

    public List<MotifCollection> getMotifCollections() {
        final Set<MotifCollection> motifCollections = new HashSet<MotifCollection>();
        for (MotifRankingsDatabase db: getMotifRankingsDatabases()) {
            motifCollections.add(db.getMotifCollection());
        }
        final List<MotifCollection> result = new ArrayList<MotifCollection>(motifCollections);
        Collections.sort(result);
        return result;
    }

    public List<MotifRankingsDatabase.Type> getSearchSpaceTypes() {
        final Set<MotifRankingsDatabase.Type> types = new HashSet<MotifRankingsDatabase.Type>();
        for (MotifRankingsDatabase db: getMotifRankingsDatabases()) {
            types.add(db.getType());
        }
        final List<MotifRankingsDatabase.Type> result = new ArrayList<MotifRankingsDatabase.Type>(types);
        Collections.sort(result);
        return result;
    }

    public List<GenePutativeRegulatoryRegion> getPutativeRegulatoryRegions(
                                                final MotifCollection motifCollection,
                                               final MotifRankingsDatabase.Type type) {
        if (MotifRankingsDatabase.Type.REGION.equals(type)) {
            return Collections.singletonList(GenePutativeRegulatoryRegion.UNKNOWN);
        } else {
            final List<GenePutativeRegulatoryRegion> result = new ArrayList<GenePutativeRegulatoryRegion>();
            for (MotifRankingsDatabase db: getMotifRankingsDatabases()) {
                if (type.equals(db.getType())
                        && db.getMotifCollection().equals(motifCollection)
                        && !result.contains(db.getPutativeRegulatoryRegion()))
                    result.add(db.getPutativeRegulatoryRegion());
            }
            return result;
        }
    }

    public List<MotifRankingsDatabase> getMotifDatabases(final MotifCollection motifCollection,
                                                    final MotifRankingsDatabase.Type type,
                                                    final GenePutativeRegulatoryRegion region) {
        final List<MotifRankingsDatabase> dbs = new ArrayList<MotifRankingsDatabase>();
        for (MotifRankingsDatabase db: getMotifRankingsDatabases()) {
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
