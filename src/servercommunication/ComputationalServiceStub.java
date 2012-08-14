package servercommunication;


import java.util.*;


import domainmodel.GeneIdentifier;
import domainmodel.Input;
import domainmodel.Motif;
import domainmodel.SpeciesNomenclature;
import domainmodel.TranscriptionFactor;

public class ComputationalServiceStub implements ComputationalService {

	@Override
	public List<Motif> findPredictedRegulators(Input input) {
		
		
		//String[] proteinen = {"603627", "603626", "555135", "603627", "603627"};
		//String[] proteinen2 = {"603627", "555134", "603627", "555134", "555135"};
		
		
		GeneIdentifier geneID0 = new GeneIdentifier("603627", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID1 = new GeneIdentifier("603626", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID2 = new GeneIdentifier("555135", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		GeneIdentifier geneID3 = new GeneIdentifier("555134", SpeciesNomenclature.HOMO_SAPIENS_HGNC);
		
		List<domainmodel.CandidateTargetGene> lctgs1 = new ArrayList<domainmodel.CandidateTargetGene>();
		List<domainmodel.CandidateTargetGene> lctgs2 = new ArrayList<domainmodel.CandidateTargetGene>();
		List<domainmodel.CandidateTargetGene> lctgs3 = new ArrayList<domainmodel.CandidateTargetGene>();
		domainmodel.CandidateTargetGene ctg0 = new domainmodel.CandidateTargetGene(geneID0, 1);
		domainmodel.CandidateTargetGene ctg1 = new domainmodel.CandidateTargetGene(geneID1, 1);
		domainmodel.CandidateTargetGene ctg2 = new domainmodel.CandidateTargetGene(geneID2, 1);
		domainmodel.CandidateTargetGene ctg3 = new domainmodel.CandidateTargetGene(geneID3, 1);
		lctgs1.add(ctg0);
		lctgs1.add(ctg1);
		lctgs1.add(ctg2);
		lctgs2.add(ctg3);
		lctgs2.add(ctg0);
		lctgs3.add(ctg1);
		lctgs3.add(ctg2);
		lctgs3.add(ctg3);
		lctgs3.add(ctg0);
		
		List<TranscriptionFactor> ltf1 = new ArrayList<TranscriptionFactor>();
		List<TranscriptionFactor> ltf2 = new ArrayList<TranscriptionFactor>();
		List<TranscriptionFactor> ltf3 = new ArrayList<TranscriptionFactor>();
		TranscriptionFactor tf0 = new TranscriptionFactor(geneID0, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf1 = new TranscriptionFactor(geneID1, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf2 = new TranscriptionFactor(geneID2, 1f, 1f, null, null, null, null);
		TranscriptionFactor tf3 = new TranscriptionFactor(geneID3, 1f, 1f, null, null, null, null);
		ltf1.add(tf0);
		ltf1.add(tf1);
		ltf2.add(tf2);
		ltf2.add(tf3);
		ltf3.add(tf0);
		
		List<Motif> motifs = new ArrayList<Motif>();
		Motif mtf1 = new Motif("elemento-AAAATGGCG", lctgs1, ltf1, 1f, 1, 1f, 1, "Some motif", 1, 1);
		Motif mtf2 = new Motif("jaspar-MA0384.1", lctgs2, ltf2, 2f, 2, 2f, 2, "Some motif", 1, 2);
		Motif mtf3 = new Motif("yetfasco-2071", lctgs3, ltf3, 3f, 3, 3f, 3, "Some motif", 1, 3);
		
		motifs.add(mtf1);
		motifs.add(mtf2);
		motifs.add(mtf3);
		
		
		return motifs;
	}
	
	
	public List<Motif> queryDatabaseForRegulators(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR){
		return null;
	}
	
	public List<Motif> queryDatabaseForTargetome(GeneIdentifier geneID, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR){
		return null;
	}
	
	public List<Motif> queryDatabaseNetworkAnnotations(Collection<Collection<GeneIdentifier>> geneIDPairs, float minNEScore, float minOrthologousIdentity, float minSimilarityFDR){
		return null;
	}
	
	
	

}
