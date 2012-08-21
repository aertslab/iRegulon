package unittests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.junit.Test;


import domainmodel.GeneIdentifier;
import domainmodel.Motif;
import domainmodel.Results;
import domainmodel.SpeciesNomenclature;
import domainmodel.TranscriptionFactor;
import persistence.LoadException;
import persistence.PersistenceUtilities;

import view.actions.SaveLoadDialogs;

public class SaveTest {

	@Test
	public void test() {
		PersistenceUtilities save = new PersistenceUtilities();
		Results resultS = new Results(getMotifs(0), null);
		String xml = save.convertResultsToXML(resultS);
		
		Results result;
		try {
			result = save.loadResultsFromXML(xml);
			Collection<Motif> motifs = result.getMotifs();
		
		
			Motif[] motifsArr = new Motif[motifs.size()];
			int index = 0;
			for (Motif m : motifs){
				motifsArr[index] = m;
				index++;
			}
			assertEquals(3, motifsArr.length);
			TranscriptionFactor[] tf = new TranscriptionFactor[motifsArr[0].getTranscriptionFactors().size()];
			index = 0;
			for (TranscriptionFactor t : motifsArr[0].getTranscriptionFactors()){
				tf[index] = t;
				index++;
			}
			assertEquals(SpeciesNomenclature.HOMO_SAPIENS_HGNC, tf[0].getSpeciesNomeclature());
		} catch (LoadException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test2(){
		SaveLoadDialogs dialog = new SaveLoadDialogs();
		
		
		
		PersistenceUtilities save = new PersistenceUtilities();
		Results resultS = new Results(getMotifs(0), null);
		String xml = save.convertResultsToXML(resultS);
		
		dialog.showDialog(xml, "test", ".ctf");
		
		String fileInput = dialog.openDialogue();
		if (fileInput == null){
			System.out.println("fileInput is null");
		}
		Results result;
		try {
			result = save.loadResultsFromXML(xml);
		
			Collection<Motif> motifs = result.getMotifs();
			
			Motif[] motifsArr = new Motif[motifs.size()];
			int index = 0;
			for (Motif m : motifs){
				motifsArr[index] = m;
				index++;
			}
			assertEquals(3, motifsArr.length);
			TranscriptionFactor[] tf = new TranscriptionFactor[motifsArr[0].getTranscriptionFactors().size()];
			index = 0;
			for (TranscriptionFactor t : motifsArr[0].getTranscriptionFactors()){
				tf[index] = t;
				index++;
			}
			assertEquals(SpeciesNomenclature.HOMO_SAPIENS_HGNC, tf[0].getSpeciesNomeclature());
		} catch (LoadException e) {
			e.printStackTrace();
		}
		
	}

	
	
	public Collection<Motif> getMotifs(int jobID) {

		String[] proteinen = {"603627", "603626", "555135", "603627", "603627"};
		String[] proteinen2 = {"603627", "555134", "603627", "555134", "555135"};
		
		
		GeneIdentifier geneID0 = new GeneIdentifier("603627", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID1 = new GeneIdentifier("603626", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID2 = new GeneIdentifier("555135", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID3 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID4 = new GeneIdentifier("603627", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID5 = new GeneIdentifier("603626", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID6 = new GeneIdentifier("555135", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID7 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID8 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID9 = new GeneIdentifier("603626", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID10 = new GeneIdentifier("555135", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID11 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID12 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID13 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		
		
		List<domainmodel.CandidateTargetGene> lctgs1 = new ArrayList<domainmodel.CandidateTargetGene>();
		List<domainmodel.CandidateTargetGene> lctgs2 = new ArrayList<domainmodel.CandidateTargetGene>();
		List<domainmodel.CandidateTargetGene> lctgs3 = new ArrayList<domainmodel.CandidateTargetGene>();
		domainmodel.CandidateTargetGene ctg0 = new domainmodel.CandidateTargetGene(geneID0, 1);
		domainmodel.CandidateTargetGene ctg1 = new domainmodel.CandidateTargetGene(geneID1, 1);
		domainmodel.CandidateTargetGene ctg2 = new domainmodel.CandidateTargetGene(geneID2, 1);
		domainmodel.CandidateTargetGene ctg3 = new domainmodel.CandidateTargetGene(geneID3, 1);
		domainmodel.CandidateTargetGene ctg4 = new domainmodel.CandidateTargetGene(geneID4, 1);
		domainmodel.CandidateTargetGene ctg5 = new domainmodel.CandidateTargetGene(geneID5, 1);
		domainmodel.CandidateTargetGene ctg6 = new domainmodel.CandidateTargetGene(geneID6, 1);
		domainmodel.CandidateTargetGene ctg7 = new domainmodel.CandidateTargetGene(geneID7, 1);
		domainmodel.CandidateTargetGene ctg8 = new domainmodel.CandidateTargetGene(geneID8, 1);
		lctgs1.add(ctg0);
		lctgs1.add(ctg1);
		lctgs1.add(ctg2);
		lctgs2.add(ctg3);
		lctgs2.add(ctg4);
		lctgs3.add(ctg5);
		lctgs3.add(ctg6);
		lctgs3.add(ctg7);
		lctgs3.add(ctg8);
		
		List<TranscriptionFactor> ltf1 = new ArrayList<TranscriptionFactor>();
		List<TranscriptionFactor> ltf2 = new ArrayList<TranscriptionFactor>();
		List<TranscriptionFactor> ltf3 = new ArrayList<TranscriptionFactor>();
		TranscriptionFactor tf0 = new TranscriptionFactor(geneID9, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf1 = new TranscriptionFactor(geneID10, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf2 = new TranscriptionFactor(geneID11, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf3 = new TranscriptionFactor(geneID12, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf4 = new TranscriptionFactor(geneID13, 1f, 1f, null, null, null, null);
		ltf1.add(tf0);
		ltf1.add(tf1);
		ltf2.add(tf2);
		ltf2.add(tf3);
		ltf3.add(tf4);
		
		List<Motif> motifs = new ArrayList<Motif>();
		Motif mtf1 = new Motif("elemento-AAAATGGCG", lctgs1, ltf1, 1f, 1, 1f, 1, "Some motif", 1, 1);
		Motif mtf2 = new Motif("jaspar-MA0384.1", lctgs2, ltf2, 2f, 2, 2f, 2, "Some motif", 1, 1);
		Motif mtf3 = new Motif("yetfasco-2071", lctgs3, ltf3, 3f, 3, 3f, 3, "Some motif", 1, 1);
		
		motifs.add(mtf1);
		motifs.add(mtf2);
		motifs.add(mtf3);
		
		return motifs;
	}
	
	
	
}
