package domainModel;

import iRegulonInput.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle{
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();
	
	public static SpeciesNomenclature HOMO_SAPIENS_HGNC = new SpeciesNomenclature(1, "Homo sapiens, HGNC", "Hsapiens_HGNC_database");
	public static SpeciesNomenclature MUS_MUSCULUS_MGI = new SpeciesNomenclature(2, "Mus musculus, MGI", "Mmusculus_MGI_database");
	public static SpeciesNomenclature DROSOPHILA_FlyBase = new SpeciesNomenclature(3, "Drosophila melanogaster, FlyBase", "Dmelanogaster_FlyBase_database");
	public static SpeciesNomenclature DROSOPHILA_CG_numbers = new SpeciesNomenclature (4, "Drosophila melanogaster, CG-numbers", "Dmelanogaster_CG_database");
	//public static SpeciesNomenclature DROSOPHILA_FBgn_numbers = new SpeciesNomenclature (5, "Drosophila melanogaster, FBgn", "Dmelanogaster_FBgn_database");
	public static SpeciesNomenclature UNKNOWN = new SpeciesNomenclature(-1, "?", "?");
	
	public static SpeciesNomenclature getNomenclature(final int code) {
		return CODE2NOMENCLATURE.get(code);
	}
	
	public static Collection<SpeciesNomenclature> getAllNomenclatures() {
		return CODE2NOMENCLATURE.values(); 
	}
	
	public static Collection<SpeciesNomenclature> getSelectableNomenclatures() {
		Collection<SpeciesNomenclature> selectable = new ArrayList<SpeciesNomenclature>();
		for (int i = 1; i < CODE2NOMENCLATURE.size(); i++){
			selectable.add(CODE2NOMENCLATURE.get(i));
		}
		return selectable;
		//return CODE2NOMENCLATURE.values(); 
	}
	
	private final int code;
	private final String name;
	private final String databaseName;
	private Map<String, String> geneDatabase;
	private Map<String, String> regionsDatabase;
	private Map<String, String> regionsDelineation;
	
	private SpeciesNomenclature(final int code, final String name, final String databaseName) {
		this.code = code;
		this.name = name;
		this.databaseName = databaseName;
		
		//RegionsDatabase
		try{
			String databasesString = this.getBundle().getString(databaseName.concat("_RegionBased"));
			this.regionsDatabase = new LinkedHashMap<String, String>();
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					this.regionsDatabase.put(databaseSplit[0], databaseSplit[1]);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for regions of " + databaseName);
			this.regionsDatabase = new LinkedHashMap<String, String>();
		} 
		//GeneDatabase
		try{
			String databasesString = this.getBundle().getString(this.databaseName.concat("_GeneBased"));
			this.geneDatabase = new LinkedHashMap<String, String>();
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					this.geneDatabase.put(databaseSplit[0], databaseSplit[1]);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for genes " + this.databaseName);
			this.geneDatabase = new LinkedHashMap<String, String>();
		} 
		//RegionsDelineation
		try{
			String databasesString = this.getBundle().getString(this.databaseName.concat("_Delineation"));
			this.regionsDelineation = new LinkedHashMap<String, String>();
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					this.regionsDelineation.put(databaseSplit[0], databaseSplit[1]);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for region Delineation " + this.databaseName);
			this.regionsDelineation = new LinkedHashMap<String, String>();
		} 
		
		CODE2NOMENCLATURE.put(code, this);
	}
	
	public String toString() {
		return this.name;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public Map<String, String> getGeneDatabase(){
		return this.geneDatabase;
	}
	
	public Map<String, String> getRegionsDatabase(){
		return this.regionsDatabase;
	}
	
	public Map<String, String> getRegionsDelineation(){
		return this.regionsDelineation;
	}
	
	public String getGeneDatabaseOf(String key){
		if (this.geneDatabase.keySet().contains(key)){
			return this.geneDatabase.get(key);
		}else{
			return null;
		}
	}
	
	public String getRegionDatabaseOf(String key){
		if (this.regionsDatabase.keySet().contains(key)){
			return this.regionsDatabase.get(key);
		}else{
			return null;
		}
	}
	
	public String getDelineationOf(String key){
		if (this.regionsDelineation.keySet().contains(key)){
			return this.regionsDelineation.get(key);
		}else{
			return null;
		}
	}
	
	public String getDatabaseNameOf(String database){
		if (this.geneDatabase.containsValue(database)){
			for (String key : this.geneDatabase.keySet()){
				if (database.equals(this.geneDatabase.get(key))){
					return key;
				}
			}
		}else{
			for (String key : this.regionsDatabase.keySet()){
				if (database.equals(this.regionsDatabase.get(key))){
					return key;
				}
			}
		}
		return null;
	}
	
	public String getDelineationNameOf(String delineation){
		if (this.regionsDelineation.containsValue(delineation)){
			for (String key : this.regionsDelineation.keySet()){
				if (delineation.equals(this.regionsDelineation.get(key))){
					return key;
				}
			}
		}
		return null;
	}
	
}
