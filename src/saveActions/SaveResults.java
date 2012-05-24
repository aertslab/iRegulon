package saveActions;

import iRegulonInput.IRegulonType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.thoughtworks.xstream.XStream;

import domainModel.*;
import exceptions.LoadException;

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
		this.xstream.alias("iRegulonType", IRegulonType.class);
		this.xstream.alias("CisTargetType", IRegulonType.class);
		
		this.xstream.setMode(XStream.NO_REFERENCES);
		this.xstream.registerConverter(new SpeciesNomenclatureConverter());
	}
	
	/**
	 * transforms the given collection of motifs to xml
	 * @param motifs
	 * @return String xml
	 */
	public String saveResultsAsXML(Results result){
		//Set-up of the XStream
		this.xstream = new XStream();
		this.xstream.alias("motif", Motif.class);
		this.xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		this.xstream.alias("geneIdentifier", GeneIdentifier.class);
		this.xstream.alias("transcriptionFactor", TranscriptionFactor.class);
		this.xstream.alias("inputParameters", Input.class);
		this.xstream.alias("iRegulonType", IRegulonType.class);
				
		this.xstream.setMode(XStream.NO_REFERENCES);
		this.xstream.registerConverter(new SpeciesNomenclatureConverter());
		
		String xml = this.xstream.toXML(result);
		//System.out.println(xml);
		return xml;
	}
	
	/**
	 * transforms the given xml to a collection of motifs
	 * @param String xml
	 * @return the motifs in a Collection of the given xml
	 * @throws LoadException 
	 */
	public Results loadResultsFromXML(String xml) throws LoadException{
		this.xstream = new XStream();
		
		//this.xstream.aliasField("iRegulonType", IRegulonType.class, "iRegulonType");
		
		//this.xstream.aliasField("iRegulonType", IRegulonType.class, "cisCisTargetType");
		//this.xstream.aliasField("CisTargetType", IRegulonType.class, "iRegulonType");
		//this.xstream.aliasType("iRegulonType", IRegulonType.class);
		//this.xstream.aliasType("cisCisTargetType", IRegulonType.class);
		//this.xstream.aliasType("CisTargetType", IRegulonType.class);
		//this.xstream.aliasAttribute("iRegulonType", "iRegulonType");
		//this.xstream.aliasAttribute("cisCisTargetType", "iRegulonType");
		//this.xstream.aliasAttribute("CisTargetType", "iRegulonType");
		
		this.xstream.alias("motif", Motif.class);
		this.xstream.alias("candidateTargetGene", CandidateTargetGene.class);
		this.xstream.alias("geneIdentifier", GeneIdentifier.class);
		this.xstream.alias("transcriptionFactor", TranscriptionFactor.class);
		this.xstream.alias("inputParameters", Input.class);
		this.xstream.alias("iRegulonType", IRegulonType.class);
		this.xstream.aliasField("cisCisTargetType", Input.class, "iRegulonType");
		//this.xstream.alias("cisCisTargetType", IRegulonType.class);
		//this.xstream.alias("CisTargetType", IRegulonType.class);

		
		
		//this.xstream.setMode(XStream.NO_REFERENCES);
		this.xstream.registerConverter(new SpeciesNomenclatureConverter());
		try{
			Results result = (Results) this.xstream.fromXML(xml);
			//Results result = new Results(motifs, null);
				return result;
		}catch (Exception e){
			throw new LoadException("The file can't be loaded: wrong format", e);
		}
			
	}
	
	public String saveResultsAsTabDelimited(Collection<Motif> motifs){
		//Header
		String tabfile = "Rank" + "\t"
						+ "Motif id" + "\t"
						+ "AUC" + "\t"
						+ "NES" + "\t"
						+ "ClusterCode" + "\t"
						+ "Transcription factor" + "\t"
						+ "Target genes" + "\n";
		for (Motif motif : motifs){
			//Motif specifications
			tabfile += motif.getRank() + "\t"
					+ motif.getEnrichedMotifID() + "\t"
					+ motif.getAucValue() + "\t"
					+ motif.getNeScore() + "\t"
					+ motif.getClusterCode() + "\t";
			// a komma seperated list of TFs
			Iterator<TranscriptionFactor> tfIterator = motif.getTranscriptionFactors().iterator();
			while (tfIterator.hasNext()){
				tabfile += tfIterator.next().getName();
				if (tfIterator.hasNext()){
					tabfile += ",";
				}
			}
			tabfile += "\t";
			// a komma seperated list of TGs
			Iterator<CandidateTargetGene> tgIterator = motif.getCandidateTargetGenes().iterator();
			while (tgIterator.hasNext()){
				tabfile += tgIterator.next().getGeneName();
				if (tgIterator.hasNext()){
					tabfile += ",";
				}
			}
			// end of the line (of the motif)
			tabfile += "\n";
		}
		return tabfile;
	}
	
	
}
