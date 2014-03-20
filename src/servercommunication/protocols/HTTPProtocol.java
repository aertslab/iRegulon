package servercommunication.protocols;

import cytoscape.CytoscapeVersion;
import domainmodel.*;
import infrastructure.Logger;
import servercommunication.ServerCommunicationException;
import view.IRegulonResourceBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


public class HTTPProtocol extends IRegulonResourceBundle implements Protocol {
    private final String userAgent = this.getBundle().getString("User_Agent")
            + " Cytoscape: " + CytoscapeVersion.version
            + "; " + System.getProperty("os.name")
            + "; " + System.getProperty("os.version")
            + "; " + System.getProperty("os.arch") + ')';

    public HTTPProtocol() {
    }

    private HttpURLConnection createConnection(String bundleKey) throws IOException {
        final URL url = new URL(getBundle().getString(bundleKey));
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Sent a request for a job in iRegulon
     *
     * @return the id of the job
     * @throws servercommunication.ServerCommunicationException
     *
     */
    public int sentJob(InputParameters input) throws ServerCommunicationException {
        String name = input.getName();
        Collection<GeneIdentifier> geneIDs = input.getGenes();
        float AUCThreshold = input.getROCthresholdAUC();
        int rankThreshold = input.getThresholdForVisualisation();
        float NESThreshold = input.getEScore();
        float minOrthologous = input.getMinOrthologous();
        float maxMotifSimilarityFDR = input.getMaxMotifSimilarityFDR();

        boolean isRegionBased = input.isRegionBased();
        String motifRankingsDatabase = input.getMotifRankingsDatabase().getCode();
        float overlap = input.getOverlap();
        String delineation = input.getDelineation().getCode();
        int upstream = input.getUpstream();
        int downstream = input.getDownstream();

        try {
            /* Creating the data to send as a POST request. */
            String data = "jobName=" + name
                    + "&AUCThreshold=" + AUCThreshold
                    + "&rankThreshold=" + rankThreshold
                    + "&NESThreshold=" + NESThreshold
                    + "&minOrthologous=" + minOrthologous
                    + "&maxMotifSimilarityFDR=" + maxMotifSimilarityFDR
                    + "&selectedMotifRankingsDatabase=" + motifRankingsDatabase;

            if (isRegionBased) {
                if (delineation != null) {
                    data += "&conversionFractionOfOverlap=" + overlap
                            + "&conversionDelineation=" + delineation;
                } else {
                    data += "&conversionFractionOfOverlap=" + overlap
                            + "&conversionUpstreamRegionInBp=" + upstream
                            + "&conversionDownstreamRegionInBp=" + downstream;
                }
            }

            GeneIdentifier[] geneIDArray = new GeneIdentifier[geneIDs.size()];
            int geneIDIndex = 0;
            for (GeneIdentifier gene : geneIDs) {
                geneIDArray[geneIDIndex] = gene;
                geneIDIndex++;
            }
            for (int index = 0; index < geneIDArray.length; index++) {
                if (index == 0) {
                    data += "&SpeciesNomenclature=" + geneIDArray[index].getSpeciesNomenclature().getCode()
                            + "&genes=" + geneIDArray[index].getGeneName();
                } else {
                    data += ";" + geneIDArray[index].getGeneName();
                }
            }

            /* Create connection. */
            final HttpURLConnection connection;
            try {
                connection = createConnection("URL_submit");

                /* Send POST request with all data fields to the server. */
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                wr.write(data);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                Logger.getInstance().error("Unable to submit job to iRegulon server. Server is not available.");
                throw new ServerCommunicationException("Unable to submit job to iRegulon server. Server is not available.", e);
            }

            /* Check if the requested web page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getInstance().error("Unable to submit job to iRegulon server. Unable to access '" + this.getBundle().getString("URL_submit") + "'.");
                throw new ServerCommunicationException("Unable to submit job to iRegulon server. Unable to access '" + this.getBundle().getString("URL_submit") + "'.");
            }

            /* Get the job ID from the reply. */
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            int jobID = 0;

            while ((line = rd.readLine()) != null) {
                String[] columns = line.split("\t");

                if (columns.length == 2) {
                    if (columns[0].equals("jobID:")) {
                        try {
                            jobID = Integer.parseInt(columns[1]);
                        } catch (NumberFormatException e) {
                            rd.close();
                            throw new ServerCommunicationException("Returned job ID is not a number.");
                        }
                    } else if (columns[0].equals("ERROR:")) {
                        rd.close();
                        throw new ServerCommunicationException(columns[1]);
                    }
                }
            }

            rd.close();

            return jobID;
        } catch (Exception e) {
            throw new ServerCommunicationException(e.getMessage(), e);
        }
    }

    /**
     * @param jobID the id of the job
     * @return the state of the job @see State
     */
    public State getState(int jobID) throws ServerCommunicationException {
        try {
            /* Create connection. */
            final HttpURLConnection connection;
            try {
                connection = createConnection("URL_stateCheck");

                /* Send POST request with jobID to server. */
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                wr.write("jobID=" + jobID);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                Logger.getInstance().error("Unable to check state of iRegulon job. Server is not available.");
                throw new ServerCommunicationException("Unable to check state of iRegulon job. Server is not available.", e);
            }

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getInstance().error("Unable to check state of iRegulon job. Unable to access '" + this.getBundle().getString("URL_stateCheck") + "'.");
                throw new ServerCommunicationException("Unable to check state of iRegulon job. Unable to access '" + this.getBundle().getString("URL_stateCheck") + "'.");
            }

            /* Get the job state from the reply. */
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            State state = State.ERROR;

            while ((line = rd.readLine()) != null) {
                String[] columns = line.split("\t");

                if (columns.length == 2) {
                    if (columns[0].equals("jobState:")) {
                        if (columns[1].equals("Requested")) {
                            state = State.REQUESTED;
                        } else if (columns[1].equals("Running")) {
                            state = State.RUNNING;
                        } else if (columns[1].equals("Finished")) {
                            state = State.FINISHED;
                        } else if (columns[1].equals("Error")) {
                            state = State.ERROR;
                        }
                    } else if (columns[0].equals("ERROR:")) {
                        rd.close();
                        throw new ServerCommunicationException(columns[1]);
                    }
                }
            }

            rd.close();

            return state;
        } catch (IOException e) {
            throw new ServerCommunicationException(e.getMessage());
        }
    }

    /**
     * @param jobID the id of the job
     * @return the number of jobs before this job
     */
    public int getJobsBeforeThis(int jobID) throws ServerCommunicationException {
        try {
            /* Create connection. */
            final HttpURLConnection connection;
            try {
                connection = createConnection("URL_stateCheck");

                /* Send POST request with jobID to server. */
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                wr.write("jobID=" + jobID);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                Logger.getInstance().error("Unable to check number of jobs to wait for. Server is not available.");
                throw new ServerCommunicationException("Unable to check number of jobs to wait for. Server is not available.", e);
            }

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getInstance().error("Unable to check number of jobs to wait for. Unable to access '" + this.getBundle().getString("URL_stateCheck") + "'.");
                throw new ServerCommunicationException("Unable to check number of jobs to wait for. Unable to access '" + this.getBundle().getString("URL_stateCheck") + "'.");
            }

            /* Get the job state from the reply. */
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            int jobsBefore = -1;

            while ((line = rd.readLine()) != null) {
                String[] columns = line.split("\t");

                if (columns.length == 2) {
                    if (columns[0].equals("jobsBeforeThis:")) {
                        try {
                            jobsBefore = Integer.parseInt(columns[1]);
                        } catch (NumberFormatException e) {
                            rd.close();
                            throw new ServerCommunicationException("Returned number of jobs to wait for is not a number.");
                        }
                    } else if (columns[0].equals("ERROR:")) {
                        rd.close();
                        throw new ServerCommunicationException(columns[1]);
                    }
                }
            }

            rd.close();

            return jobsBefore;
        } catch (IOException e) {
            throw new ServerCommunicationException(e.getMessage());
        }
    }

    /**
     * @param jobID the id of the job
     * @return a collection of Motifs
     */
    public Collection<Motif> getMotifs(int jobID) throws ServerCommunicationException {
        Collection<Motif> motifs = new ArrayList<Motif>();
        try {
            /* Create connection. */
            final HttpURLConnection connection;
            try {
                connection = createConnection("URL_results");

                /* Send POST request with jobID to server. */
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                wr.write("jobID=" + jobID);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                Logger.getInstance().error("Unable to get motifs from iRegulon server. Server is not available.");
                throw new ServerCommunicationException("Unable to get motifs from iRegulon server. Server is not available.", e);
            }

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getInstance().error("Unable to get motifs from iRegulon server. Unable to access '" + this.getBundle().getString("URL_results") + "'.");
                throw new ServerCommunicationException("Unable to get motifs from iRegulon server. Unable to access '" + this.getBundle().getString("URL_results") + "'.");
            }

            /* Get and parse the reply. */
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            Map<Object, Integer> clusterNrMap = new HashMap<Object, Integer>();
            int clustercount = 0;

            while ((line = rd.readLine()) != null) {
                String[] motifVariables = line.split("\t");

                if (motifVariables.length == 10 || motifVariables.length == 17) {
                    /**
                     * The following columns are always available:
                     *    0 	nomenclature code
                     *    1 	motif rank
                     *    2 	motif name
                     *    3     feature ID
                     *    4 	motif description
                     *    5 	AUCValue
                     *    6 	NEScore
                     *    7 	clusterNumber
                     *    8 	candidateTargetIDs (separated by ;)
                     *    9 	candidateTargetRanks (separated by ;)
                     *
                     * The following columns are only available (actually not empty) if the motif is annotated:
                     *    10 	transcriptionFactorNames (separated by ;)
                     *    11	motifSimilarityFDR (separated by ;) of the corresponding TF
                     *    12	orthologousIdentity (separated by ;) of the corresponding TF
                     *    13	similarMotifName (separated by ;) of the corresponding TF
                     *    14	similarMotifDescription (separated by ;) of the corresponding TF
                     *    15	orthologousGeneName (separated by ;) of the corresponding TF
                     *    16	orthologousSpecies (separated by ;) of the corresponding TF
                     */

                    SpeciesNomenclature sn = SpeciesNomenclature.getNomenclature(Integer.parseInt(motifVariables[0]));

                    /* The candidate target genes. */
                    List<CandidateTargetGene> candidateTargetGenes = new ArrayList<CandidateTargetGene>();
                    /* The candidate target gene IDs */
                    String[] candidateTargetGeneIDs = motifVariables[8].split(";");
                    /* The ranks of the target genes. */
                    String[] candidateTargetGeneRanks = motifVariables[9].split(";");
                    for (int index = 0; index < candidateTargetGeneIDs.length; index++) {
                        GeneIdentifier geneID = new GeneIdentifier(candidateTargetGeneIDs[index], sn);
                        CandidateTargetGene gene = new CandidateTargetGene(geneID, Integer.parseInt(candidateTargetGeneRanks[index]));
                        candidateTargetGenes.add(gene);
                    }

                    /* Make all the transcription factors related stuff if the 7 last columns have data. */
                    List<TranscriptionFactor> transcriptionFactors = new ArrayList<TranscriptionFactor>();
                    if (motifVariables.length == 17) {
                        /* Transcription factor names. */
                        String[] transcriptionFactorNames = motifVariables[10].split(";");
                        /* False discovery rate of similar motifs. */
                        String[] transcriptionFactorMotifSimilarityFDR = motifVariables[11].split(";");
                        /* Identity between orthologous genes. */
                        String[] transcriptionFactorOrthologousIdentity = motifVariables[12].split(";");
                        /* Names of similar motifs. */
                        String[] transcriptionFactorSimilarMotifName = motifVariables[13].split(";");
                        /* Motif descriptions of similar motifs.  */
                        String[] transcriptionFactorSimilarMotifDescription = motifVariables[14].split(";");
                        /* Orthologous gene names. */
                        String[] transcriptionFactorOrthologousGeneName = motifVariables[15].split(";");
                        /* Orthologous species. */
                        String[] transcriptionFactorOrthologousSpecies = motifVariables[16].split(";");

                        for (int index = 0; index < transcriptionFactorNames.length; index++) {
                            GeneIdentifier geneID = new GeneIdentifier(transcriptionFactorNames[index], sn);
                            float tfOrthologousIdentity = Float.parseFloat(transcriptionFactorOrthologousIdentity[index]);
                            float tfMotifSimilarity = Float.parseFloat(transcriptionFactorMotifSimilarityFDR[index]);
                            if (tfOrthologousIdentity == -1) {
                                tfOrthologousIdentity = Float.NaN;
                            }
                            if (tfMotifSimilarity == -1) {
                                tfMotifSimilarity = Float.NaN;
                            }
                            String similarMotifName = transcriptionFactorSimilarMotifName[index];
                            if (similarMotifName.equalsIgnoreCase("null")) {
                                similarMotifName = null;
                            }
                            String similarMotifDescription = transcriptionFactorSimilarMotifDescription[index];
                            if (similarMotifDescription.equalsIgnoreCase("null")) {
                                similarMotifDescription = null;
                            }
                            String orthologousGeneName = transcriptionFactorOrthologousGeneName[index];
                            if (orthologousGeneName.equalsIgnoreCase("null")) {
                                orthologousGeneName = null;
                            }
                            String orthologousSpecies = transcriptionFactorOrthologousSpecies[index];
                            if (orthologousSpecies.equalsIgnoreCase("null")) {
                                orthologousSpecies = null;
                            }
                            TranscriptionFactor tf = new TranscriptionFactor(geneID, tfOrthologousIdentity,
                                    tfMotifSimilarity, similarMotifName, similarMotifDescription,
                                    orthologousGeneName, orthologousSpecies);
                            transcriptionFactors.add(tf);
                        }
                    }

                    /* Create a new motif and add it to the collection. */
                    int clusternumber = Integer.parseInt(motifVariables[7]);

                    if (!clusterNrMap.containsKey(clusternumber)) {
                        clusterNrMap.put(clusternumber, clustercount);
                        clustercount++;
                    }

                    int actualClusterNumber = clusterNrMap.get(clusternumber);


                    Motif mtf = new Motif(motifVariables[2], candidateTargetGenes,
                            transcriptionFactors, Float.parseFloat(motifVariables[6]),
                            actualClusterNumber + 1, Float.parseFloat(motifVariables[5]),
                            Integer.parseInt(motifVariables[1]), motifVariables[4],
                            Integer.parseInt(motifVariables[3]), jobID);

                    motifs.add(mtf);

                } else if (motifVariables.length == 2) {
                    if (motifVariables[0].equals("ERROR:")) {
                        rd.close();
                        throw new ServerCommunicationException(motifVariables[1]);
                    }
                }
            }

            rd.close();

        } catch (IOException e) {
            throw new ServerCommunicationException(e.getMessage());
        }
        return motifs;
    }

    @Override
    public String getErrorMessage(int jobID) throws ServerCommunicationException {
        try {
            /* Create connection. */
            final HttpURLConnection connection;
            try {
                connection = createConnection("URL_error");

                /* Send POST request with jobID to server. */
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                wr.write("jobID=" + jobID);
                wr.flush();
                wr.close();
            } catch (IOException e) {
                Logger.getInstance().error("Unable to get job error message. Server is not available.");
                throw new ServerCommunicationException("Unable to get job error message. Server is not available.", e);
            }

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getInstance().error("Unable to get job error message from iRegulon server. Unable to access '" + this.getBundle().getString("URL_error") + "'.");
                throw new ServerCommunicationException("Unable to get job error message from iRegulon server. Unable to access '" + this.getBundle().getString("URL_error") + "'.");
            }

            /* Get the job state from the reply. */
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            String jobError = "";

            while ((line = rd.readLine()) != null) {
                String[] columns = line.split("\t");

                if (columns.length == 2) {
                    if (columns[0].equals("JOB_ERROR:")) {
                        jobError = "<html>" + columns[1].replaceAll("\\\\n", "<br>") + "</html>";
                    } else if (columns[0].equals("ERROR:")) {
                        rd.close();
                        throw new ServerCommunicationException(columns[1]);
                    }
                }
            }

            rd.close();

            return jobError;
        } catch (IOException e) {
            throw new ServerCommunicationException(e.getMessage());
        }
    }
}
