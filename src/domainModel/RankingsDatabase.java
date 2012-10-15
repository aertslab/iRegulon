package domainmodel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.List;

public class RankingsDatabase {
    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String TYPE_TAG_NAME = "type";
    private static final String NAME_TAG_NAME = "name";
    private static final String SPECIES_TAG_NAME = "species";
    private static final String NOMENCLATURE_TAG_NAME = "nomenclature";
    private static final String MOTIF_COLLECTION_TAG_NAME = "motif-collection";


    public static List<RankingsDatabase> create(final Document document) {
        return Collections.emptyList(); //TODO:
    }


	private final String code;
	private final String name;
	private final float AUCvalue;
	private final int visualisationValue;
	
	public RankingsDatabase(String code, String name, float AUCvalue, int visualisationValue){
		this.code = code;
		this.name = name;
		this.AUCvalue = AUCvalue;
		this.visualisationValue = visualisationValue;
	}

	
	public String getCode(){
		return this.code;
	}
	
	public String getName(){
		return this.name;
	}
	
	public float getAUCvalue(){
		return this.AUCvalue;
	}
	
	public int getVisualisationValue(){
		return this.visualisationValue;
	}
	
	public String toString(){
		return this.name;
	}
	
	
	
}
