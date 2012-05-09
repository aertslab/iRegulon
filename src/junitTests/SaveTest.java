package junitTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.junit.Test;


import domainModel.GeneIdentifier;
import domainModel.Motif;
import domainModel.Results;
import domainModel.SpeciesNomenclature;
import domainModel.TranscriptionFactor;
import exceptions.LoadException;

import saveActions.SaveLoadDialogs;
import saveActions.SaveResults;

public class SaveTest {

	@Test
	public void test() {
		SaveResults save = new SaveResults();
		Results resultS = new Results(getMotifs(0), null);
		String xml = save.saveResultsAsXML(resultS);
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test2(){
		SaveLoadDialogs dialog = new SaveLoadDialogs();
		
		
		
		SaveResults save = new SaveResults();
		Results resultS = new Results(getMotifs(0), null);
		String xml = save.saveResultsAsXML(resultS);
		
		dialog.saveDialogue(xml, "test", ".ctf");
		
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
			// TODO Auto-generated catch block
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
		
		
		List<domainModel.CandidateTargetGene> lctgs1 = new ArrayList<domainModel.CandidateTargetGene>();
		List<domainModel.CandidateTargetGene> lctgs2 = new ArrayList<domainModel.CandidateTargetGene>();
		List<domainModel.CandidateTargetGene> lctgs3 = new ArrayList<domainModel.CandidateTargetGene>();
		domainModel.CandidateTargetGene ctg0 = new domainModel.CandidateTargetGene(geneID0, 1);
		domainModel.CandidateTargetGene ctg1 = new domainModel.CandidateTargetGene(geneID1, 1);
		domainModel.CandidateTargetGene ctg2 = new domainModel.CandidateTargetGene(geneID2, 1);
		domainModel.CandidateTargetGene ctg3 = new domainModel.CandidateTargetGene(geneID3, 1);
		domainModel.CandidateTargetGene ctg4 = new domainModel.CandidateTargetGene(geneID4, 1);
		domainModel.CandidateTargetGene ctg5 = new domainModel.CandidateTargetGene(geneID5, 1);
		domainModel.CandidateTargetGene ctg6 = new domainModel.CandidateTargetGene(geneID6, 1);
		domainModel.CandidateTargetGene ctg7 = new domainModel.CandidateTargetGene(geneID7, 1);
		domainModel.CandidateTargetGene ctg8 = new domainModel.CandidateTargetGene(geneID8, 1);
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
