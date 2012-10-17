package domainmodel;

import view.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle {
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();
    private static final Map<Integer, List<RankingsDatabase>> CODE2DATABASES = new HashMap<Integer, List<RankingsDatabase>>();
    static {
        for (RankingsDatabase db: RankingsDatabase.loadFromConfiguration()) {
            final List<RankingsDatabase> dbs;
            if (CODE2DATABASES.containsKey(db.getSpeciesNomenclature())) {
                dbs = CODE2DATABASES.get(db.getSpeciesNomenclature());
            } else {
                dbs = new ArrayList<RankingsDatabase>();
                CODE2DATABASES.put(db.getSpeciesNomenclature(), dbs);
            }
            dbs.add(db);
        }
    }

	public static SpeciesNomenclature HOMO_SAPIENS_HGNC = new SpeciesNomenclature(1, "Homo sapiens, HGNC symbols");
	public static SpeciesNomenclature MUS_MUSCULUS_MGI = new SpeciesNomenclature(2, "Mus musculus, MGI symbols");
	public static SpeciesNomenclature DROSOPHILA_FlyBase = new SpeciesNomenclature(3, "Drosophila melanogaster, FlyBase names");
	public static SpeciesNomenclature DROSOPHILA_CG_numbers = new SpeciesNomenclature (4, "Drosophila melanogaster, CG-numbers");
	//public static SpeciesNomenclature DROSOPHILA_FBgn_numbers = new SpeciesNomenclature (5, "Drosophila melanogaster, FBgn");
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
    private final List<RankingsDatabase> databases;

    private SpeciesNomenclature() {
        this(-1, "?");
    }
	
	private SpeciesNomenclature(final int code, final String name) {
		this.code = code;
		this.name = name;
        this.databases = (this.code > 0) ? CODE2DATABASES.get(code): Collections.<RankingsDatabase>emptyList();
        CODE2NOMENCLATURE.put(code, this);
	}

    public String getName() {
        return this.name;
    }

	public int getCode(){
		return this.code;
	}

    public List<RankingsDatabase> getDatabases() {
        return databases;
    }

    public List<MotifCollection> getMotifCollections() {
        final Set<MotifCollection> collections = new HashSet<MotifCollection>();
        for (RankingsDatabase db: getDatabases()) {
            collections.add(db.getMotifCollection());
        }
        final List<MotifCollection> result = new ArrayList<MotifCollection>(collections);
        Collections.sort(result);
        return result;
    }

    public List<RankingsDatabase.Type> getSearchSpaceTypes() {
        final Set<RankingsDatabase.Type> types = new HashSet<RankingsDatabase.Type>();
        for (RankingsDatabase db: getDatabases()) {
            types.add(db.getType());
        }
        final List<RankingsDatabase.Type> result = new ArrayList<RankingsDatabase.Type>(types);
        Collections.sort(result);
        return result;
    }

    public List<GenePutativeRegulatoryRegion> getPutativeRegulatoryRegions(
                                                final MotifCollection collection,
                                               final RankingsDatabase.Type type) {
        if (RankingsDatabase.Type.REGION.equals(type)) {
            return Collections.singletonList(GenePutativeRegulatoryRegion.UNKNOWN);
        } else {
            final List<GenePutativeRegulatoryRegion> result = new ArrayList<GenePutativeRegulatoryRegion>();
            for (RankingsDatabase db: getDatabases()) {
                if (type.equals(db.getType())
                        && db.getMotifCollection().equals(collection)
                        && !result.contains(db.getPutativeRegulatoryRegion()))
                    result.add(db.getPutativeRegulatoryRegion());
            }
            return result;
        }
    }

    public List<RankingsDatabase> getDatabases(final MotifCollection collection,
                                               final RankingsDatabase.Type type,
                                               final GenePutativeRegulatoryRegion region) {
        final List<RankingsDatabase> dbs = new ArrayList<RankingsDatabase>();
        for (RankingsDatabase db: getDatabases()) {
            if (type.equals(db.getType())
                 && db.getMotifCollection().equals(collection)
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
