package httpProtocols;

import iRegulonAnalysis.Input;
import iRegulonInput.CisTargetResourceBundle;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;


import domainModel.CandidateTargetGene;
import domainModel.GeneIdentifier;
import domainModel.Motif;
import domainModel.SpeciesNomenclature;
import domainModel.TranscriptionFactor;

public class HTTPService extends CisTargetResourceBundle implements Service{

	public HTTPService(){
		
	}
	
	/**
	 * Sent a request for a job in CisTarget
	 * @param geneIDs
	 * @param AUCThreshold
	 * @param rankThreshold
	 * @param NESThreshold
	 * @return the id of the job
	 */
	public int sentJob(Input input){
		String name = input.getName();
		Collection<GeneIdentifier> geneIDs = input.getGenes();
		float AUCThreshold = input.getROCthresholdAUC();
		int rankThreshold = input.getThresholdForVisualisation();
		float NESThreshold = input.getEScore();
		float minOrthologous = input.getMinOrthologous();
		float maxMotifSimilarityFDR = input.getMaxMotifSimilarityFDR();
		
		boolean isRegionBased = input.isRegionBased();
		String database = input.getDatabase();
		float overlap = input.getOverlap();
		String delineation = input.getDelineation();
		int upstream = input.getUpstream();
		int downstream = input.getDownstream();
		try {
			//creating the data
			String data = "jobName=" + name 
					+ "&AUCThreshold=" + AUCThreshold 
					+ "&rankThreshold=" + rankThreshold 
					+ "&NESThreshold=" + NESThreshold 
					+ "&minOrthologous=" + minOrthologous 
					+ "&maxMotifSimilarityFDR=" + maxMotifSimilarityFDR
					+ "&selectedDatabase=" + database;
			if (isRegionBased){
				if(delineation != null){
					data += "&conversionFractionOfOverlap=" + overlap 
							+ "&conversionDelineation=" + delineation;
				}else{
					data += "&conversionFractionOfOverlap=" + overlap 
							+ "&conversionUpstreamRegionInBp=" + upstream 
							+ "&conversionDownstreamRegionInBp=" + downstream;
				}
			}
			
			GeneIdentifier[] geneIDArray = new GeneIdentifier[geneIDs.size()];
		    int geneIDIndex = 0;
		    for (GeneIdentifier gene : geneIDs){
		    	geneIDArray[geneIDIndex] = gene;
		    	geneIDIndex++;
		    }
		    for (int index=0; index < geneIDArray.length; index++){
		    	if (index == 0){
		    		data = data + "&SpeciesNomenclature=" + geneIDArray[index].getSpeciesNomenclature().getCode();
		    		data = data + "&genes=" + geneIDArray[index].getGeneName();
		    	}else{
		    		data = data + ";" + geneIDArray[index].getGeneName();
		    	}
		    }
		    System.out.println(data);
			// Send data
		    URL url = new URL(this.getBundle().getString("URL_submit"));
		    URLConnection conn = url.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    
		    wr.write(data);
		    wr.flush();
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    int jobID = 0;
		    while ((line = rd.readLine()) != null) {
		    	//System.out.println(line);
		        jobID = Integer.parseInt(line);
		    }
		    System.out.println(jobID);
		    wr.close();
		    rd.close();
		    return jobID;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return -1;
	}
	
	/**
	 * 
	 * @param jobID the id of the job
	 * @return the state of the job @see State
	 */
	public State getState(int jobID){
		try {
			//creating the data
			
			// Send data
		    URL url = new URL(this.getBundle().getString("URL_stateCheck"));
		    URLConnection conn = url.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    
		    wr.write("jobID=" + jobID);
		    wr.flush();
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    State state = State.ERROR;
		    while ((line = rd.readLine()) != null) {
		    	String[] jobValues = line.split("\t");
		        //System.out.println("State: " + line);
		        //System.out.println(jobValues);
		        if (jobValues[0].toUpperCase().equals("ERROR")){
		        	state = State.ERROR;
		        }
		        if (jobValues[0].toUpperCase().equals("FINISHED")){
		        	state = State.FINISHED;
		        }
		        if (jobValues[0].toUpperCase().equals("RUNNING")){
		        	state = State.RUNNING;
		        }
		        if (jobValues[0].toUpperCase().equals("REQUESTED")){
		        	state = State.REQUESTED;
		        }
		    }
		    wr.close();
		    rd.close();
		    //System.out.println(state.toString());
		    return state;
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return State.ERROR;
	}
	
	/**
	 * 
	 * @param jobID the id of the job
	 * @return the amount of jobs before this job
	 */
	public int getJobsBeforeThis(int jobID){
		try {
			//creating the data
			
			// Send data
			URL url = new URL(this.getBundle().getString("URL_stateCheck"));
		    URLConnection conn = url.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    
		    wr.write("jobID=" + jobID);
		    wr.flush();
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    int jobsBefore = -1;
		    while ((line = rd.readLine()) != null) {
		    	String[] jobValues = line.split("\t");
		        //System.out.println("jobsBeforeThis: " + line);
		    	if (jobValues.length == 1){
		    		jobsBefore = 0;
		    	}else{
		    		jobsBefore = Integer.parseInt(jobValues[1]);
		    	}
		    }
		    wr.close();
		    rd.close();
		    return jobsBefore;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return -1;
	}
	
	/**
	 * 
	 * @param jobID the id of the job
	 * @return a collection of Motifs
	 */
	public Collection<Motif> getMotifs(int jobID){
		Collection<Motif> motifs = new ArrayList<Motif>();
		try {
			//creating the data
			
			// Send data
			URL url = new URL(this.getBundle().getString("URL_results"));
		    URLConnection conn = url.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    
		    wr.write("jobID=" + jobID);
		    wr.flush();
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    int[] clusterNr = new int[10];
		    for (int clusteri=0; clusteri < 10; clusteri++){
		    	clusterNr[clusteri] = -1;
		    }
		    int clustercount = 0;
		    while ((line = rd.readLine()) != null) {
		        //System.out.println(line);
		        //Following order of variables:
		        //	0 	nomenclature code, 
		        //	1 	motif rank, 
		        //	2 	motif name, 
		        // 	3	feature ID,
		        //	4 	motif description,
		        //	5 	AUCValue, 
		        //	6 	NEScore, 
		        //	7 	clusterNumber, 
		        //	8 	candidateTargetIDs (seperated by ;)
		        //	9 	candidateTargetRanks (seperated by ;), 
		        //	10 	transcriptionFactorNames (seperated by ;),
		        //	11	motifSimilarityFDR (seperated by ;) of the correspondating TF,
		        //	12	orthologousIdentity (seperated by ;) of the correspondating TF
		    	//	13	similarMotifName (seperated by ;) of the correspondating TF
		    	//	14	similarMotifDescription (seperated by ;) of the correspondating TF
		    	//	15	orthologousGeneName (seperated by ;) of the correspondating TF
		    	//	16	orthologousSpecies (seperated by ;) of the correspondating TF
		        String[] motifVariables = line.split("\t");
		        //System.out.println(Integer.parseInt(motifVariables[0]));
		        SpeciesNomenclature sn = SpeciesNomenclature.getNomenclature(Integer.parseInt(motifVariables[0])); 
		        
		        //Make all the CandidateTargetGenes
		        Collection<CandidateTargetGene> candidateTargetGenes = new ArrayList<CandidateTargetGene>();
		        //the IDs
		        String[] candidateTargetGeneIDs = motifVariables[8].split(";");
		        //there ranks
		        String[] candidateTargetGeneRanks = motifVariables[9].split(";");
		        for (int index=0; index < candidateTargetGeneIDs.length; index++){
		        	GeneIdentifier geneID = new GeneIdentifier(candidateTargetGeneIDs[index], sn);
		        	CandidateTargetGene gene = new CandidateTargetGene(geneID, Integer.parseInt(candidateTargetGeneRanks[index]));
		        	candidateTargetGenes.add(gene);
		        }

		        //Make all the transcriptionFactors
		        Collection<TranscriptionFactor> transcriptionFactors = new ArrayList<TranscriptionFactor>();
		        if (motifVariables.length > 10){
		        	//the names
		        	String[] transcriptionFactorNames = motifVariables[10].split(";");
		        	//the motifSimilarityFDR
		        	String[] transcriptionFactorMotifSimilarityFDR = motifVariables[11].split(";");
		        	//the ottholofousIdentifier
		        	String[] transcriptionFactorOrthologousIdentity = motifVariables[12].split(";");
		        	//the similarMotifName
		        	String[] transcriptionFactorSimilarMotifName = motifVariables[13].split(";");
			    	//the similarMotifDescription
		        	String[] transcriptionFactorSimilarMotifDescription = motifVariables[14].split(";");
			    	//the orthologousGeneName
		        	String[] transcriptionFactorOrthologousGeneName = motifVariables[15].split(";");
			    	//the orthologousSpecies
		        	String[] transcriptionFactorOrthologousSpecies = motifVariables[16].split(";");
		        	for (int index = 0; index < transcriptionFactorNames.length; index++){
		        		GeneIdentifier geneID = new GeneIdentifier(transcriptionFactorNames[index], sn);
		        		float tfOrthologousIdentity = Float.parseFloat(transcriptionFactorOrthologousIdentity[index]);
		        		float tfMotifSimilarity = Float.parseFloat(transcriptionFactorMotifSimilarityFDR[index]);
		        		if (tfOrthologousIdentity == -1){
		        			tfOrthologousIdentity = Float.NaN;
		        		}
		        		if (tfMotifSimilarity == -1){
		        			tfMotifSimilarity = Float.NaN;
		        		}
		        		String similarMotifName = transcriptionFactorSimilarMotifName[index];
		        		if (similarMotifName.equalsIgnoreCase("null")){
		        			similarMotifName = null;
		        		}
		        		String similarMotifDescription = transcriptionFactorSimilarMotifDescription[index];
		        		if (similarMotifDescription.equalsIgnoreCase("null")){
		        			similarMotifDescription = null;
		        		}
		        		String orthologousGeneName = transcriptionFactorOrthologousGeneName[index];
		        		if (orthologousGeneName.equalsIgnoreCase("null")){
		        			orthologousGeneName = null;
		        		}
		        		String orthologousSpecies = transcriptionFactorOrthologousSpecies[index];
		        		if (orthologousSpecies.equalsIgnoreCase("null")){
		        			orthologousSpecies = null;
		        		}
		        		TranscriptionFactor tf = new TranscriptionFactor(geneID, tfOrthologousIdentity, 
		        				tfMotifSimilarity, similarMotifName, similarMotifDescription, 
		        				orthologousGeneName, orthologousSpecies);
		        		transcriptionFactors.add(tf);
		        	}
		        }
		        
		        //Creating new Motif and add it to the collection
		        int clusternumber = Integer.parseInt(motifVariables[7]);
		        if (clusternumber >= clusterNr.length){
		        	int[] newclusterNr = new int[clusternumber + 1];
		        	int clusteri = 0;
		        	for (clusteri=0; clusteri < clusterNr.length; clusteri++){
				    	 newclusterNr[clusteri] = clusterNr[clusteri];
				    }
				    for (clusteri = clusteri; clusteri < newclusterNr.length; clusteri++){
				    	newclusterNr[clusteri] = -1;
				    }
				    clusterNr = newclusterNr;
		        }
		        int actualClusterNumber;
		        if (clusterNr[clusternumber] == -1){
		        	clusterNr[clusternumber] = clustercount;
		        	actualClusterNumber = clustercount;
		        	clustercount++;
		        }else{
		        	actualClusterNumber = clusterNr[clusternumber];
		        }
		        
		        
		        Motif mtf = new Motif(motifVariables[2], candidateTargetGenes, 
		        		transcriptionFactors, Float.parseFloat(motifVariables[6]), 
		        		actualClusterNumber + 1, Float.parseFloat(motifVariables[5]), 
		        		Integer.parseInt(motifVariables[1]), motifVariables[4], 
		        		Integer.parseInt(motifVariables[3]), jobID);
		        motifs.add(mtf);
		    }
		    wr.close();
		    rd.close();
		   
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return motifs;
	}

	@Override
	public String getErrorMessage(int jobID) {
		try {
			//creating the data
			
			// Send data
			URL url = new URL(this.getBundle().getString("URL_error"));
		    URLConnection conn = url.openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    
		    wr.write("jobID=" + jobID);
		    wr.flush();
		    // Get the response
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    String line;
		    String error = "";
		    while ((line = rd.readLine()) != null) {
		    	String[] jobValues = line.split("\t");
		        //System.out.println(line);
		        error += jobValues[0] + "\n";
		    }
		    wr.close();
		    rd.close();
		    return error;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return "Error: could not get the error message";
	}
	
	
	
}
