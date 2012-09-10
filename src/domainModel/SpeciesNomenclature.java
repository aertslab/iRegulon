package domainmodel;

import infrastructure.Logger;
import view.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle {
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();

    private static final String GENE_DATABASE_SUFFIX = "_GeneBased";
    private static final String REGION_DATABASE_SUFFIX = "_RegionBased";
    private static final String DEFAULT_AUC_THRESHOLD = "standard_ROC";
    private static final String DEFAULT_RANK_THRESHOLD = "standard_visualisation";
    private static final String DELINEATION_SUFFIX = "_Delineation";
    private static final String PARTICULAR_AUC_THRESHOLD = "special_ROC";
    private static final String PARTICULAR_RANK_THRESHOLD = "special_visualisation";

	public static SpeciesNomenclature HOMO_SAPIENS_HGNC = new SpeciesNomenclature(1, "Homo sapiens, HGNC symbols", "Hsapiens_HGNC_database");
	public static SpeciesNomenclature MUS_MUSCULUS_MGI = new SpeciesNomenclature(2, "Mus musculus, MGI symbols", "Mmusculus_MGI_database");
	public static SpeciesNomenclature DROSOPHILA_FlyBase = new SpeciesNomenclature(3, "Drosophila melanogaster, FlyBase names", "Dmelanogaster_FlyBase_database");
	public static SpeciesNomenclature DROSOPHILA_CG_numbers = new SpeciesNomenclature (4, "Drosophila melanogaster, CG-numbers", "Dmelanogaster_CG_database");
	//public static SpeciesNomenclature DROSOPHILA_FBgn_numbers = new SpeciesNomenclature (5, "Drosophila melanogaster, FBgn", "Dmelanogaster_FBgn_database");
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

	private final List<RankingsDatabase> geneDatabases;
	private final List<RankingsDatabase> regionDatabases;
	private final List<Delineation> regionDelineations;

    private SpeciesNomenclature() {
        this.code = -1;
		this.name = "?";
		this.geneDatabases = Collections.emptyList();
		this.regionDatabases = Collections.emptyList();
        this.regionDelineations = Collections.emptyList();
    }
	
	private SpeciesNomenclature(final int code, final String name, final String databaseName) {
		this.code = code;
		this.name = name;

        final LinkedHashMap<String, Float> dbName2AUCThreshold = new LinkedHashMap<String, Float>();
        try {
            String databasesString = getBundle().getString(PARTICULAR_AUC_THRESHOLD);
            String[] databasesArray = databasesString.split(";");
            for (String database : databasesArray) {
                String[] databaseSplit = database.split("\\|");
                if (databaseSplit.length == 2) {
                    dbName2AUCThreshold.put(databaseSplit[0], Float.parseFloat(databaseSplit[1]));
                }
            }
        } catch (MissingResourceException e) {
            Logger.getInstance().error("Missing resource for AUC threshold.");
        }
        final LinkedHashMap<String, Integer> dbName2RankThreshold = new LinkedHashMap<String, Integer>();
        try {
            String databasesString = getBundle().getString(PARTICULAR_RANK_THRESHOLD);
            String[] databasesArray = databasesString.split(";");
            for (String database : databasesArray) {
                String[] databaseSplit = database.split("\\|");
                if (databaseSplit.length == 2) {
                    dbName2RankThreshold.put(databaseSplit[0], Integer.parseInt(databaseSplit[1]));
                }
            }
        } catch (MissingResourceException e) {
            Logger.getInstance().error("Missing resource for rank threshold.");
        }

        this.regionDatabases = loadDatabasesFromBundle(databaseName, REGION_DATABASE_SUFFIX, dbName2AUCThreshold, dbName2RankThreshold);
		this.geneDatabases = loadDatabasesFromBundle(databaseName, GENE_DATABASE_SUFFIX, dbName2AUCThreshold, dbName2RankThreshold);
        this.regionDelineations = loadDelineationsFromBundle(databaseName);
		
		CODE2NOMENCLATURE.put(code, this);
	}


    private Float getAUCThreshold(final String dbName, final Map<String, Float> dbName2AUCThreshold) {
        if (dbName2AUCThreshold.containsKey(dbName)) {
            return dbName2AUCThreshold.get(dbName);
        } else {
            return Float.parseFloat(getBundle().getString(DEFAULT_AUC_THRESHOLD));
        }
    }

    private Integer getRankThreshold(final String dbName, final Map<String,Integer> dbName2RankThreshold) {
        if (dbName2RankThreshold.containsKey(dbName)) {
            return dbName2RankThreshold.get(dbName);
        } else {
            return Integer.parseInt(getBundle().getString(DEFAULT_RANK_THRESHOLD));
        }
    }

    private List<RankingsDatabase> loadDatabasesFromBundle(final String databaseName, final String suffix,
                                                   final Map<String,Float> dbName2AUCThreshold,
                                                   final Map<String,Integer> dbName2RankThreshold) {
        final List<RankingsDatabase> databases = new ArrayList<RankingsDatabase>();
        try {
            final String databasesString = getBundle().getString(databaseName.concat(suffix));
            if (databasesString.trim().equals("")) {
                return Collections.emptyList();
            }
            for (String database : databasesString.split(";")) {
                final String[] databaseSplit = database.split("\\|");
                if (databaseSplit.length == 2) {
                    databases.add(new RankingsDatabase(databaseSplit[1], databaseSplit[0],
                            getAUCThreshold(databaseSplit[1], dbName2AUCThreshold),
                            getRankThreshold(databaseSplit[1], dbName2RankThreshold)));
                } else {
                    Logger.getInstance().error("Invalid resource \"databases\" for database: \"" + databaseName + "\".");
                }
            }
        } catch (MissingResourceException e) {
            Logger.getInstance().error("Missing resource \"databases\" for database: \"" + databaseName + "\".");
        }
        return Collections.unmodifiableList(databases);
    }


    private List<Delineation> loadDelineationsFromBundle(final String databaseName) {
        final List<Delineation> delineations = new ArrayList<Delineation>();
        try {
            final String databasesString = getBundle().getString(databaseName.concat(DELINEATION_SUFFIX));
            if (databasesString.trim().equals("")) {
                return Collections.emptyList();
            }
            for (String database : databasesString.split(";")) {
                final String[] databaseSplit = database.split("\\|");
                if (databaseSplit.length == 2) {
                    delineations.add(new Delineation(databaseSplit[1], databaseSplit[0]));
                } else {
                    Logger.getInstance().error("Invalid resource \"region delineation\" for database: \"" + databaseName + "\".");
                }
            }
        } catch (MissingResourceException e) {
            Logger.getInstance().error("Missing resource \"region delineation\" for database: \"" + databaseName + "\".");
        }
        return Collections.unmodifiableList(delineations);
    }

    public String getName() {
        return this.name;
    }

	public int getCode(){
		return this.code;
	}
	
	public List<RankingsDatabase> getGeneDatabases(){
		return this.geneDatabases;
	}
	
	public List<RankingsDatabase> getRegionDatabases(){
		return this.regionDatabases;
	}
	
	public List<Delineation> getRegionDelineations(){
		return this.regionDelineations;
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
