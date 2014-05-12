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
    public int sentJob(PredictRegulatorsParameters predictRegulatorsParameters) throws ServerCommunicationException {
        String name = predictRegulatorsParameters.getName();
        Collection<GeneIdentifier> geneIDs = predictRegulatorsParameters.getGenes();
        float AUCThreshold = predictRegulatorsParameters.getROCthresholdAUC();
        int rankThreshold = predictRegulatorsParameters.getThresholdForVisualisation();
        float NESThreshold = predictRegulatorsParameters.getEScore();
        float minOrthologous = predictRegulatorsParameters.getMinOrthologous();
        float maxMotifSimilarityFDR = predictRegulatorsParameters.getMaxMotifSimilarityFDR();

        boolean isRegionBased = predictRegulatorsParameters.isRegionBased();
        String motifRankingsDatabase;
        if (predictRegulatorsParameters.getMotifCollection().equals(MotifCollection.NONE.getDescription())) {
            motifRankingsDatabase = "none";
        } else {
            motifRankingsDatabase = predictRegulatorsParameters.getMotifRankingsDatabase().getCode();
        }
        String trackRankingsDatabase;
        if (predictRegulatorsParameters.getTrackCollection().equals(TrackCollection.NONE.getDescription())) {
            trackRankingsDatabase = "none";
        } else {
            trackRankingsDatabase = predictRegulatorsParameters.getTrackRankingsDatabase().getCode();
        }
        float overlap = predictRegulatorsParameters.getOverlap();
        String delineation = predictRegulatorsParameters.getDelineation().getCode();
        int upstream = predictRegulatorsParameters.getUpstream();
        int downstream = predictRegulatorsParameters.getDownstream();

        try {
            /* Creating the data to send as a POST request. */
            String data = "jobName=" + name
                    + "&AUCThreshold=" + AUCThreshold
                    + "&rankThreshold=" + rankThreshold
                    + "&NESThreshold=" + NESThreshold
                    + "&minOrthologous=" + minOrthologous
                    + "&maxMotifSimilarityFDR=" + maxMotifSimilarityFDR
                    + "&selectedMotifRankingsDatabase=" + motifRankingsDatabase
                    + "&selectedTrackRankingsDatabase=" + trackRankingsDatabase;

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
     * @param predictRegulatorsParameters
     * @param jobID the id of the job
     * @return a collection of Motifs
     */
    public Collection<AbstractMotifAndTrack> getMotifsAndTracks(PredictRegulatorsParameters predictRegulatorsParameters, int jobID) throws ServerCommunicationException {
        String motifRankingsDatabase;
        if (predictRegulatorsParameters.getMotifCollection().equals(MotifCollection.NONE.getDescription())) {
            motifRankingsDatabase = "none";
        } else {
            motifRankingsDatabase = predictRegulatorsParameters.getMotifRankingsDatabase().getCode();
        }
        String trackRankingsDatabase;
        if (predictRegulatorsParameters.getTrackCollection().equals(TrackCollection.NONE.getDescription())) {
            trackRankingsDatabase = "none";
        } else {
            trackRankingsDatabase = predictRegulatorsParameters.getTrackRankingsDatabase().getCode();
        }

        Collection<AbstractMotifAndTrack> motifsAndTracks = new ArrayList<AbstractMotifAndTrack>();

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
                Logger.getInstance().error("Unable to get motifs and/or tracks from iRegulon server. Server is not available.");
                throw new ServerCommunicationException("Unable to get motifs and/or tracks from iRegulon server. Server is not available.", e);
            }

            /* Check if the requested page exists. */
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Logger.getInstance().error("Unable to get motifs and/or tracks from iRegulon server. Unable to access '" + this.getBundle().getString("URL_results") + "'.");
                throw new ServerCommunicationException("Unable to get motifs and/or tracks from iRegulon server. Unable to access '" + this.getBundle().getString("URL_results") + "'.");
            }

            /* Get and parse the reply. */
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            Map<String, Integer> clusterCodeToNumber = new HashMap<String, Integer>();
            Map<String, Integer> motifClusterCodeToNumber = new HashMap<String, Integer>();
            Map<String, Integer> trackClusterCodeToNumber = new HashMap<String, Integer>();
            int totalClusterCount = 0;
            int motifClusterCount = 0;
            int trackClusterCount = 0;

            while ((line = rd.readLine()) != null) {
                String[] motifOrTrackVariables = line.split("\t");

                if (motifOrTrackVariables.length == 11 || motifOrTrackVariables.length == 18) {
                    /**
                     * The following columns are always available:
                     *     0	nomenclature code
                     *     1	motif/track rankingsdatabase ID
                     *     2	motif/track rank
                     *     3	motif/track name
                     *     4	feature ID
                     *     5	motif/track description
                     *     6	AUCValue
                     *     7	NEScore
                     *     8	clusterNumber
                     *     9	candidateTargetIDs (separated by ;)
                     *    10	candidateTargetRanks (separated by ;)
                     *
                     * The following columns are only available (actually not empty) if the motif/track is annotated:
                     *    11	transcriptionFactorNames (separated by ;)
                     *    12	motifSimilarityFDR (separated by ;) of the corresponding TF
                     *    13	orthologousIdentity (separated by ;) of the corresponding TF
                     *    14	similarMotifName (separated by ;) of the corresponding TF
                     *    15	similarMotifDescription (separated by ;) of the corresponding TF
                     *    16	orthologousGeneName (separated by ;) of the corresponding TF
                     *    17	orthologousSpecies (separated by ;) of the corresponding TF
                     */

                    SpeciesNomenclature sn = SpeciesNomenclature.getNomenclature(Integer.parseInt(motifOrTrackVariables[0]));

                    /* The candidate target genes. */
                    List<CandidateTargetGene> candidateTargetGenes = new ArrayList<CandidateTargetGene>();
                    /* The candidate target gene IDs. */
                    String[] candidateTargetGeneIDs = motifOrTrackVariables[9].split(";");
                    /* The ranks of the target genes. */
                    String[] candidateTargetGeneRanks = motifOrTrackVariables[10].split(";");
                    for (int index = 0; index < candidateTargetGeneIDs.length; index++) {
                        GeneIdentifier geneID = new GeneIdentifier(candidateTargetGeneIDs[index], sn);
                        CandidateTargetGene gene = new CandidateTargetGene(geneID, Integer.parseInt(candidateTargetGeneRanks[index]));
                        candidateTargetGenes.add(gene);
                    }

                    /* Make all the transcription factors related stuff if the 7 last columns have data. */
                    List<TranscriptionFactor> transcriptionFactors = new ArrayList<TranscriptionFactor>();
                    if (motifOrTrackVariables.length == 18) {
                        /* Transcription factor names. */
                        String[] transcriptionFactorNames = motifOrTrackVariables[11].split(";");
                        /* False discovery rate of similar motifs. */
                        String[] transcriptionFactorMotifSimilarityFDR = motifOrTrackVariables[12].split(";");
                        /* Identity between orthologous genes. */
                        String[] transcriptionFactorOrthologousIdentity = motifOrTrackVariables[13].split(";");
                        /* Names of similar motifs. */
                        String[] transcriptionFactorSimilarMotifName = motifOrTrackVariables[14].split(";");
                        /* Motif descriptions of similar motifs.  */
                        String[] transcriptionFactorSimilarMotifDescription = motifOrTrackVariables[15].split(";");
                        /* Orthologous gene names. */
                        String[] transcriptionFactorOrthologousGeneName = motifOrTrackVariables[16].split(";");
                        /* Orthologous species. */
                        String[] transcriptionFactorOrthologousSpecies = motifOrTrackVariables[17].split(";");

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

                    if (motifOrTrackVariables[1].equals(motifRankingsDatabase)) {
                        /* Create a motif cluster name with the original motif cluster number. */
                        String originalMotifClusterCode = "M" + motifOrTrackVariables[8];

                        if (!clusterCodeToNumber.containsKey(originalMotifClusterCode)) {
                            /* Keep track of the amount of motif clusters. */
                            motifClusterCodeToNumber.put(originalMotifClusterCode, motifClusterCount + 1);
                            motifClusterCount++;
                            /* Keep track of the total amount of motif and track clusters
                               (needed for coloring motifs/tracks of the same cluster in the same color). */
                            clusterCodeToNumber.put(originalMotifClusterCode, totalClusterCount + 1);
                            totalClusterCount++;
                        }

                        /* Make a motif cluster number so the cluster numbers for motifs and tracks will be different,
                           so it can be used to show each cluster in a different color. */
                        int motifClusterNumber = clusterCodeToNumber.get(originalMotifClusterCode);
                        /* Make a new motif cluster code, so the first motif cluster will be "M1". */
                        String motifClusterCode = "M" + motifClusterCodeToNumber.get(originalMotifClusterCode);

                        /* Create a new motif and add it to the collection. */
                        Motif mtf = new Motif(motifOrTrackVariables[3], candidateTargetGenes,
                                transcriptionFactors, Float.parseFloat(motifOrTrackVariables[7]),
                                motifClusterCode, motifClusterNumber, Float.parseFloat(motifOrTrackVariables[6]),
                                Integer.parseInt(motifOrTrackVariables[2]), motifOrTrackVariables[5],
                                Integer.parseInt(motifOrTrackVariables[4]), jobID);
                        motifsAndTracks.add(mtf);
                    } else if (motifOrTrackVariables[1].equals(trackRankingsDatabase)) {
                        /* Create a track cluster name with the original motif cluster number. */
                        String originalTrackClusterCode = "T" + motifOrTrackVariables[8];

                        if (!clusterCodeToNumber.containsKey(originalTrackClusterCode)) {
                            /* Keep track of the amount of track clusters. */
                            trackClusterCodeToNumber.put(originalTrackClusterCode, trackClusterCount + 1);
                            trackClusterCount++;
                            /* Keep track of the total amount of motif and track clusters
                               (needed for coloring motifs/tracks of the same cluster in the same color).
                             */
                            clusterCodeToNumber.put(originalTrackClusterCode, totalClusterCount + 1);
                            totalClusterCount++;
                        }

                        /* Get a track cluster number so the cluster numbers for motifs and tracks will be different,
                           so it can be used to show each cluster in a different color. */
                        int trackClusterNumber = clusterCodeToNumber.get(originalTrackClusterCode);
                        /* Make a new track cluster code, so the first track cluster will be "T1". */
                        String trackClusterCode = "T" + trackClusterCodeToNumber.get(originalTrackClusterCode);

                        /* Create a new track and add it to the collection. */
                        Track track = new Track(motifOrTrackVariables[3], candidateTargetGenes,
                                transcriptionFactors, Float.parseFloat(motifOrTrackVariables[7]),
                                trackClusterCode, trackClusterNumber, Float.parseFloat(motifOrTrackVariables[6]),
                                Integer.parseInt(motifOrTrackVariables[2]), motifOrTrackVariables[5],
                                Integer.parseInt(motifOrTrackVariables[4]), jobID);
                        motifsAndTracks.add(track);
                    } else {
                        throw new ServerCommunicationException("Motif or track rankingsdatabase '" + motifOrTrackVariables[1] + "' is unknown.");
                    }

                } else if (motifOrTrackVariables.length == 2) {
                    if (motifOrTrackVariables[0].equals("ERROR:")) {
                        rd.close();
                        throw new ServerCommunicationException(motifOrTrackVariables[1]);
                    }
                }
            }

            rd.close();

        } catch (IOException e) {
            throw new ServerCommunicationException(e.getMessage());
        }

        return motifsAndTracks;
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
