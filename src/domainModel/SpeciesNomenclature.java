package domainModel;

import java.util.*;

public final class SpeciesNomenclature {
	private static final Map<Integer,SpeciesNomenclature> CODE2NOMENCLATURE = new HashMap<Integer,SpeciesNomenclature>();
	
	public static SpeciesNomenclature HOMO_SAPIENS_HGNC = new SpeciesNomenclature(1, "Homo sapiens, HGNC");
	public static SpeciesNomenclature MUS_MUSCULUS_MGI = new SpeciesNomenclature(2, "Mus musculus, MGI");
	public static SpeciesNomenclature DROSOPHILA_FlyBase = new SpeciesNomenclature(3, "Drosophila melanogaster, FlyBase");
	//public static SpeciesNomenclature DROSOPHILA_CG_numbers = new SpeciesNomenclature (4, "Drosophila melanogaster, CG-numbers");
	//public static SpeciesNomenclature DROSOPHILA_FBgn_numbers = new SpeciesNomenclature (5, "Drosophila melanogaster, FBgn");
	public static SpeciesNomenclature UNKNOWN = new SpeciesNomenclature(-1, "?");
	
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
	
	private SpeciesNomenclature(final int code, final String name) {
		this.code = code;
		this.name = name;
		
		CODE2NOMENCLATURE.put(code, this);
	}
	
	public String toString() {
		return this.name;
	}
	
	public int getCode(){
		return this.code;
	}
}
