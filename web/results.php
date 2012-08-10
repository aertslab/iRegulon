<?php

//parameters for the MySQLDatabase
$ini_array = parse_ini_file("configuration.ini");
$servername = $ini_array['servername'];
$username = $ini_array['username'];
$password = $ini_array['password'];
$database = $ini_array['database'];

//possible statusses: WAITING, RUNNING, FINISHED, FAILED, ERROR;

$jobID = $_POST["jobID"];

$Connection = mysql_connect($servername,$username,$password);

mysql_select_db($database, $Connection) or die(mysql_error($Connection));

//selecting all id of all features (motifs)
$query = "
SELECT
	ID
FROM
	enrichedFeature
WHERE
	jobID='$jobID'";


$result = mysql_query($query, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));

//selecting the nomenclature of the job
$querynomenclature = "
	SELECT
		nomenclatureCode
	FROM
		jobQueue
	WHERE
		ID='$jobID'";


$resultnomenclature = mysql_query($querynomenclature, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultnomenclature)){
	$nomenclature = $row['nomenclatureCode'];
}

$output = "";
while($row = mysql_fetch_array($result)){
	$enrichedFeatureID = $row['ID'];
	
	//selecting the motif
	$queryenrichedFeature = "
		SELECT
			*
		FROM
			enrichedFeature
		WHERE
			ID='$enrichedFeatureID'";
	$resultenrichedFeature = mysql_query($queryenrichedFeature, $Connection);
	if (mysql_errno($Connection)) die (mysql_error($Connection));
	while($rowenrichedFeature = mysql_fetch_array($resultenrichedFeature)){
		if (strlen($output) != 0){
			$output = $output . "\n";
		}
		$output = $output . $nomenclature . "\t" . $rowenrichedFeature['rank'] . "\t" . 
				$rowenrichedFeature['name'] . "\t" .
				$rowenrichedFeature['ID'] . "\t" .
				$rowenrichedFeature['description'] . "\t" . 
				$rowenrichedFeature['AUCValue'] . "\t" .
				$rowenrichedFeature['NEScore'] . "\t" .
				$rowenrichedFeature['clusterNumber'] . "\t" .
				$rowenrichedFeature['candidateTargetIDs'] . "\t" .
				$rowenrichedFeature['candidateTargetRanks'];
		//selecting the transcription factors
		$queryfeatureAnnotation = "
			SELECT
				*
			FROM
				featureAnnotation
			WHERE
				featureID='$enrichedFeatureID'";
		$resultfeatureAnnotation = mysql_query($queryfeatureAnnotation, $Connection);
		if (mysql_errno($Connection)) die (mysql_error($Connection));
		$TFnames = "";
		$TFmotifSimilarityFDR = "";
		$TForthologousIdentity = "";
		
		$TFsimilarMotifName = "";
		$TFsimilarMotifDescription = "";
		$TForthologousGeneName = "";
		$TForthologousSpecies = "";
		$TFsimilarMotifName = "";
		$TFsimilarMotifDescription = "";
		$TForthologousGeneName = "";
		$TForthologousSpecies = "";
		while($rowfeatureAnnotation = mysql_fetch_array($resultfeatureAnnotation)){
			if (strlen($TFnames) != 0){
				$TFnames = $TFnames . ";";
				$TFmotifSimilarityFDR = $TFmotifSimilarityFDR . ";";
				$TForthologousIdentity = $TForthologousIdentity . ";";
				
				$TFsimilarMotifName = $TFsimilarMotifName . ";";
				$TFsimilarMotifDescription = $TFsimilarMotifDescription . ";";
				$TForthologousGeneName = $TForthologousGeneName . ";";
				$TForthologousSpecies = $TForthologousSpecies . ";";
			}
			if (is_null($rowfeatureAnnotation['transcriptionFactorName'])){
				
			}else{
				$TFnames = $TFnames . $rowfeatureAnnotation['transcriptionFactorName'];
				if (is_null($rowfeatureAnnotation['motifSimilarityFDR'])){
					$TFmotifSimilarityFDR = $TFmotifSimilarityFDR . "-1";
				}else{
					$TFmotifSimilarityFDR = $TFmotifSimilarityFDR . $rowfeatureAnnotation['motifSimilarityFDR'];
				}
				if (is_null($rowfeatureAnnotation['orthologousIdentity'])){
					$TForthologousIdentity = $TForthologousIdentity . "-1";
				}else{
					$TForthologousIdentity = $TForthologousIdentity . $rowfeatureAnnotation['orthologousIdentity'];
				}
				if (is_null($rowfeatureAnnotation['similarMotifName'])){
					$TFsimilarMotifName = $TFsimilarMotifName . "null";
				}else{
					$TFsimilarMotifName = $TFsimilarMotifName . $rowfeatureAnnotation['similarMotifName'];
				}
				if (is_null($rowfeatureAnnotation['similarMotifDescription'])){
					$TFsimilarMotifDescription = $TFsimilarMotifDescription . "null";
				}else{
					$TFsimilarMotifDescription = $TFsimilarMotifDescription . $rowfeatureAnnotation['similarMotifDescription'];
				}
				if (is_null($rowfeatureAnnotation['orthologousGeneName'])){
					$TForthologousGeneName = $TForthologousGeneName . "null";
				}else{
					$TForthologousGeneName = $TForthologousGeneName . $rowfeatureAnnotation['orthologousGeneName'];
				}
				if (is_null($rowfeatureAnnotation['orthologousSpecies'])){
					$TForthologousSpecies = $TForthologousSpecies . "null";
				}else{
					$TForthologousSpecies = $TForthologousSpecies . $rowfeatureAnnotation['orthologousSpecies'];
				}
				
			}
		}
		$output = $output . "\t" . $TFnames . "\t" . $TFmotifSimilarityFDR . "\t" 
				. $TForthologousIdentity . "\t" . $TFsimilarMotifName . "\t"
				. $TFsimilarMotifDescription . "\t" . $TForthologousGeneName . "\t"
				. $TForthologousSpecies;
	}
}

echo $output;



?>