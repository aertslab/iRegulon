<?php
//parameters for the MySQLDatabase
$ini_array = parse_ini_file("configuration.ini");
$servername = $ini_array['servername'];
$username = $ini_array['username'];
$password = $ini_array['password'];
$database = $ini_array['database'];

//the parameters get from cytoscape plugin CisTargetView
$AUCThreshold = $_POST["AUCThreshold"];
$rankThreshold = $_POST["rankThreshold"];
$NESThreshold = $_POST["NESThreshold"];
$SpeciesNomenclature = $_POST["SpeciesNomenclature"];
$genes = $_POST["genes"];
$jobName = $_POST["jobName"];
$jobStatusCode = "1";
$minOrthologous = $_POST["minOrthologous"];
$maxMotifSimilarityFDR = $_POST["maxMotifSimilarityFDR"];
$selectedDatabase = $_POST["selectedDatabase"];
if (isset($_POST["conversionDelineation"])){
	$delineation = $_POST["conversionDelineation"];
}else{
	$delineation = NULL;
}
if (isset($_POST["conversionFractionOfOverlap"])){
	$overlap = $_POST["conversionFractionOfOverlap"];
}else{
	$overlap = NULL;
}
if (isset($_POST["conversionUpstreamRegionInBp"])){
	$upstream = $_POST["conversionUpstreamRegionInBp"];
}else{
	$upstream = NULL;
}
if (isset($_POST["conversionDownstreamRegionInBp"])){
	$downstream = $_POST["conversionDownstreamRegionInBp"];
}else{
	$downstream = NULL;
}

//sending the job into the database, and getting the jobID
$jobID = -1;

$Connection = mysql_connect($servername,$username,$password);

mysql_select_db($database, $Connection) or die(mysql_error($Connection));

$query = "
INSERT INTO 
		jobQueue (name, jobStatusCode, nomenclatureCode, rankThreshold, 
		AUCRankThresholdAsPercentage, NESThreshold, minOrthologousIdentity, maxMotifSimilarityFDR, 
		geneIDs, jobRequestTime, rankingDatabaseCode, conversionDelineation,
		conversionUpstreamRegionInBp, conversionDownstreamRegionInBp, 
		conversionFractionOfOverlap)
VALUES (
		'$jobName','$jobStatusCode','$SpeciesNomenclature','$rankThreshold',
		'$AUCThreshold', '$NESThreshold', '$minOrthologous', '$maxMotifSimilarityFDR', 
		'$genes', NOW(), '$selectedDatabase', '$delineation', '$upstream', 
		'$downstream', '$overlap')";


mysql_query($query, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));

$jobID = mysql_insert_id($Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));

mysql_close($Connection);

echo $jobID;


?>
