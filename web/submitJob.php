<?php

include_once 'common.php';



/* Only allow POST request to this page. */
only_post_requests();

/* Only allow iRegulon client to access this page. */
only_iregulon_client($client_version);



/* Set jobStatus code to 'requested' status. */
$jobStatusCode = Job_status_codes::REQUESTED;



/* Get parameters from the iRegulon Cytoscape plugin which should always be set (else error out). */
$AUCThreshold = retrieve_post_value('AUCThreshold', false, 'float_positive');
$rankThreshold = retrieve_post_value('rankThreshold', false, 'int_positive');
$NESThreshold = retrieve_post_value('NESThreshold', false, 'float_positive');
$SpeciesNomenclature = retrieve_post_value('SpeciesNomenclature', false, 'int_positive');
$genes = retrieve_post_value('genes', false, 'string');
$jobName = retrieve_post_value('jobName', false, 'string');
$minOrthologous = retrieve_post_value('minOrthologous', false, 'float_positive');
$maxMotifSimilarityFDR = retrieve_post_value('maxMotifSimilarityFDR', false, 'float_positive');
$selectedMotifRankingsDatabase = retrieve_post_value('selectedMotifRankingsDatabase', false, 'string');
$selectedTrackRankingsDatabase = retrieve_post_value('selectedTrackRankingsDatabase', false, 'string');


/*
 * Get parameters from the iRegulon Cytoscape plugin which could be set,
 * depending on the selected options.
 */
$delineation = retrieve_post_value('conversionDelineation', true, 'string');
$overlap = retrieve_post_value('conversionFractionOfOverlap', true, 'float_positive');
$upstream = retrieve_post_value('conversionUpstreamRegionInBp', true, 'int_positive');
$downstream = retrieve_post_value('conversionDownstreamRegionInBp', true, 'int_positive');


/*
 * Combine ranking databases to one string separated by ";".
 */
$selectedRankingsDatabases = ';';

if ($selectedMotifRankingsDatabase !== "none" && $selectedMotifRankingsDatabase !== "unknown") {
    $selectedRankingsDatabases = $selectedRankingsDatabases . $selectedMotifRankingsDatabase . ';';
}

if ($selectedTrackRankingsDatabase !== "none" && $selectedTrackRankingsDatabase !== "unknown") {
    $selectedRankingsDatabases = $selectedRankingsDatabases . $selectedTrackRankingsDatabase . ';';
}

/* Remove extra ";" at the beginning and end of the string. */
$selectedRankingsDatabases = substr($selectedRankingsDatabases, 1, -1);



/* Get MySQL connection parameters from configuration file. */
$mysql_connection_parameters = get_mysql_connection_parameters();

/* Connect to MySQL server. */
try {
    $dbh = new PDO('mysql:host=' . $mysql_connection_parameters['servername']
                      . ';port=' . $mysql_connection_parameters['port']
                      . ';dbname=' . $mysql_connection_parameters['database'],
                   $mysql_connection_parameters['username'],
                   $mysql_connection_parameters['password'],
                   array( PDO::ATTR_PERSISTENT => false));
} catch (PDOException $e) {
    echo("ERROR:\tCan't connect to MySQL database.\n");
    exit(1);
}



$query = 'INSERT INTO jobQueue
                  (
                      name, jobStatusCode, jobRequestTime,
                      nomenclatureCode, rankingDatabaseCode, conversionDelineation,
                      conversionUpstreamRegionInBp, conversionDownstreamRegionInBp, conversionFractionOfOverlap,
                      rankThreshold, AUCRankThresholdAsPercentage, NESThreshold,
                      minOrthologousIdentity, maxMotifSimilarityFDR, geneIDs,
                      ip, user_agent
		          )
          VALUES (
                      :name, :jobStatusCode, NOW(),
                      :nomenclatureCode, :rankingDatabaseCode, :conversionDelineation,
                      :conversionUpstreamRegionInBp, :conversionDownstreamRegionInBp, :conversionFractionOfOverlap,
                      :rankThreshold, :AUCRankThresholdAsPercentage, :NESThreshold,
                      :minOrthologousIdentity, :maxMotifSimilarityFDR, :geneIDs,
                      :ip, :user_agent
                  )';

try {
    $stmt = $dbh->prepare($query);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':name', $jobName, PDO::PARAM_STR);
    $stmt->bindParam(':jobStatusCode', $jobStatusCode, PDO::PARAM_STR);
    $stmt->bindParam(':nomenclatureCode', $SpeciesNomenclature, PDO::PARAM_INT);
    $stmt->bindParam(':rankingDatabaseCode', $selectedRankingsDatabases, PDO::PARAM_STR);
    $stmt->bindParam(':conversionDelineation', $delineation, PDO::PARAM_STR);
    $stmt->bindParam(':conversionUpstreamRegionInBp', $upstream, PDO::PARAM_INT);
    $stmt->bindParam(':conversionDownstreamRegionInBp', $downstream, PDO::PARAM_INT);
    $stmt->bindParam(':conversionFractionOfOverlap', $overlap, PDO::PARAM_STR);
    $stmt->bindParam(':rankThreshold', $rankThreshold, PDO::PARAM_INT);
    $stmt->bindParam(':AUCRankThresholdAsPercentage',$AUCThreshold, PDO::PARAM_STR);
    $stmt->bindParam(':NESThreshold', $NESThreshold, PDO::PARAM_STR);
    $stmt->bindParam(':minOrthologousIdentity', $minOrthologous, PDO::PARAM_STR);
    $stmt->bindParam(':maxMotifSimilarityFDR', $maxMotifSimilarityFDR, PDO::PARAM_STR);
    $stmt->bindParam(':geneIDs', $genes, PDO::PARAM_STR);
    $stmt->bindParam(':ip', $client_ip, PDO::PARAM_STR);
    $stmt->bindParam(':user_agent', $client_version, PDO::PARAM_STR);
} catch (PDOException $e) {
    echo("ERROR:\tBinding parameters failed.\n");
    exit(1);
}



/* Execute prepared statement. */
try {
    $stmt->execute();
} catch (PDOException $e) {
    echo("ERROR:\tExecuting prepared statement failed.\n");
    exit(1);
}



/* Get job ID from the MySQL table. */
try {
    $jobID = $dbh->lastInsertId('ID'); ;
} catch (PDOException $e) {
    echo("ERROR:\tGetting job ID failed.\n");
    exit(1);
}


/* Close connection */
$dbh = null;


/* Send job ID to the client. */
echo("jobID:\t" . $jobID . "\n");

?>
