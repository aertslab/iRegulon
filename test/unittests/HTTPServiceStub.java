package unittests;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import domainmodel.GeneIdentifier;
import domainmodel.InputParameters;
import domainmodel.Motif;
import domainmodel.SpeciesNomenclature;
import domainmodel.TranscriptionFactor;
import servercommunication.protocols.Service;
import servercommunication.protocols.State;

public class HTTPServiceStub implements Service {

	private int jobsBefore;
	private int attemts = 10;
	
	
	public HTTPServiceStub(){
		this.jobsBefore = 5;
	}
	
	@Override
	public int sentJob(InputParameters input) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public State getState(int jobID) {
		if (this.jobsBefore > 0){
			return State.REQUESTED;
		}
		if (this.jobsBefore == 0){
			if (this.attemts == 0){
				this.attemts = 11;
				this.jobsBefore--;
			}
			try {
				Thread.sleep(100);	
				this.attemts--;
			}
			catch (InterruptedException ex) {
				
			}
			return State.RUNNING;
		}
		if (this.jobsBefore == -1){
			return State.FINISHED;
		}
		
		return null;
	}

	@Override
	public int getJobsBeforeThis(int jobID) {
		if (this.attemts == 0){
			this.attemts = 11;
			this.jobsBefore--;
		}
		try {
			Thread.sleep(200);	
			this.attemts--;
		}
		catch (InterruptedException ex) {
			
		}
		return this.jobsBefore;
	}

	@Override
	public Collection<Motif> getMotifs(int jobID) {

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
		Motif mtf2 = new Motif("jaspar-MA0384.1", lctgs2, ltf2, 2f, 2, 2f, 2, "Some motif", 1, 1);
		Motif mtf3 = new Motif("yetfasco-2071", lctgs3, ltf3, 3f, 3, 3f, 3, "Some motif", 1, 1);
		
		motifs.add(mtf1);
		motifs.add(mtf2);
		motifs.add(mtf3);
		
		return motifs;
	}

	@Override
	public String getErrorMessage(int jobID) {
		// TODO Auto-generated method stub
		return "The error message";
	}

}
