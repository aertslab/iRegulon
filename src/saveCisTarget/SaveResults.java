package saveCisTarget;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;

import com.thoughtworks.xstream.XStream;

import domainModel.*;

public class SaveResults {

	private XStream xstream;
	
	/**
	 * creates a new stream for loading motifs in xml
	 */
	public SaveResults(){
		//Set-up of the XStream
		this.xstream = new XStream();
		this.xstream.alias("motif", Motif.class);
		this.xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		this.xstream.alias("geneIdentifier", GeneIdentifier.class);
		this.xstream.alias("transcriptionFactor", TranscriptionFactor.class);

		
		this.xstream.setMode(XStream.NO_REFERENCES);
		this.xstream.registerConverter(new SpeciesNomenclatureConverter());
	}
	
	/**
	 * transforms the given collection of motifs to xml
	 * @param motifs
	 * @return String xml
	 */
	public String saveResults(Collection<Motif> motifs){
		String xml = this.xstream.toXML(motifs);
		//System.out.println(xml);
		return xml;
	}
	
	/**
	 * transforms the given xml to a collection of motifs
	 * @param String xml
	 * @return the motifs in a Collection of the given xml
	 */
	public ArrayList<Motif> loadResults(String xml){
		//System.out.println("xml to motifs");
		ArrayList<Motif> motifs = (ArrayList<Motif>) this.xstream.fromXML(xml);
		//System.out.println("xml to motifs completed");
		return motifs;	
	}
	
	
}
