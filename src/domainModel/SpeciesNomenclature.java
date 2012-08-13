package domainmodel;

import iRegulonInput.IRegulonResourceBundle;

import java.util.*;


public final class SpeciesNomenclature extends IRegulonResourceBundle{
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();
	
	public static SpeciesNomenclature HOMO_SAPIENS_HGNC = new SpeciesNomenclature(1, "Homo sapiens, HGNC symbols", "Hsapiens_HGNC_database");
	public static SpeciesNomenclature MUS_MUSCULUS_MGI = new SpeciesNomenclature(2, "Mus musculus, MGI symbols", "Mmusculus_MGI_database");
	public static SpeciesNomenclature DROSOPHILA_FlyBase = new SpeciesNomenclature(3, "Drosophila melanogaster, FlyBase names", "Dmelanogaster_FlyBase_database");
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
	private List<Database> geneDatabase;
	private List<Database> regionsDatabase;
	private List<Delineation> regionsDelineation;
	
	private SpeciesNomenclature(final int code, final String name, final String databaseName) {
		this.code = code;
		this.name = name;
		this.databaseName = databaseName;
		this.geneDatabase = new ArrayList<Database>();
		this.regionsDatabase = new ArrayList<Database>();
		
		LinkedHashMap AUCmap = new LinkedHashMap<String, String>();
		LinkedHashMap VisualisationMap = new LinkedHashMap<String, String>();
		//the AUCmap
		try{
			String databasesString = this.getBundle().getString("special_ROC");
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					AUCmap.put(databaseSplit[0], databaseSplit[1]);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for special ROC");
		} 
		//the AUCmap
		try{
			String databasesString = this.getBundle().getString("special_visualisation");
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					VisualisationMap.put(databaseSplit[0], databaseSplit[1]);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for special visualisation");
		} 
		
		
		//RegionsDatabase
		try{
			String databasesString = this.getBundle().getString(databaseName.concat("_RegionBased"));
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					float AUC = Float.parseFloat(this.getBundle().getString("standard_ROC"));
					if (AUCmap.containsKey(databaseSplit[1])){
						AUC = Float.parseFloat((String) AUCmap.get(databaseSplit[1]));
					}
					int visualisation = Integer.parseInt(this.getBundle().getString("standard_visualisation"));
					if (VisualisationMap.containsKey(databaseSplit[1])){
						visualisation = Integer.parseInt((String) VisualisationMap.get(databaseSplit[1]));
					}
					Database aDatabase = new Database(databaseSplit[1], 
							databaseSplit[0], 
							AUC, 
							visualisation);
					this.regionsDatabase.add(aDatabase);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for regions of " + databaseName);
		} 
		//GeneDatabase
		try{
			String databasesString = this.getBundle().getString(databaseName.concat("_GeneBased"));
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					float AUC = Float.parseFloat(this.getBundle().getString("standard_ROC"));
					if (AUCmap.containsKey(databaseSplit[1])){
						AUC = Float.parseFloat((String) AUCmap.get(databaseSplit[1]));
					}
					int visualisation = Integer.parseInt(this.getBundle().getString("standard_visualisation"));
					if (VisualisationMap.containsKey(databaseSplit[1])){
						visualisation = Integer.parseInt((String) VisualisationMap.get(databaseSplit[1]));
					}
					Database aDatabase = new Database(databaseSplit[1], 
							databaseSplit[0], 
							AUC, 
							visualisation);
					this.geneDatabase.add(aDatabase);
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for genes of " + databaseName);
		} 
		
		
		//RegionsDelineation
		
		
		try{
			String databasesString = this.getBundle().getString(this.databaseName.concat("_Delineation"));
			this.regionsDelineation = new ArrayList<Delineation>();
			String[] databasesArray = databasesString.split(";");
			for (String database : databasesArray){
				String[] databaseSplit = database.split("\\|");
				if (databaseSplit.length == 2){
					this.regionsDelineation.add(new Delineation(databaseSplit[1], databaseSplit[0]));
				}
			}
		}catch (MissingResourceException e){
			System.err.println("Missing resource for region Delineation " + this.databaseName);
		} 
		
		CODE2NOMENCLATURE.put(code, this);
	}
	
	public String toString() {
		return this.name;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public List<Database> getGeneDatabase(){
		return this.geneDatabase;
	}
	
	public List<Database> getRegionsDatabase(){
		return this.regionsDatabase;
	}
	
	public List<Delineation> getRegionsDelineation(){
		return this.regionsDelineation;
	}
	
	
}
