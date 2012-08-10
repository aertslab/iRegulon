<?php

//parameters for the MySQLDatabase
$ini_array = parse_ini_file("configuration.ini");
$servername = $ini_array['servername'];
$username = $ini_array['username'];
$password = $ini_array['password'];
$database = $ini_array['database'];

$featureIDandTarget = $_GET["featureIDandTarget"];

list($featureID, $targetGeneName, $transcriptionFactor) = explode(":", $featureIDandTarget);

$Connection = mysql_connect($servername,$username,$password);

mysql_select_db($database, $Connection) or die(mysql_error($Connection));

//selecting the nomenclature of the job
$queryFeature = "
	SELECT
		name
	FROM
		enrichedFeature
	WHERE
		ID='$featureID'";


$resultFeature= mysql_query($queryFeature, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultFeature)){
	$feature = $row['name'];
}

$queryMinChrom = "
	SELECT
		MIN(startInclusive)
	AS
		minimum
	FROM
		candidateEnhancer
	WHERE
		featureID='$featureID'
			AND
		targetGeneName='$targetGeneName'";


$resultMinChrom = mysql_query($queryMinChrom, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultMinChrom)){
	$minimum = $row['minimum'];
}

$queryMaxChrom = "
	SELECT
		MAX(endExclusive)
	AS
		maximum
	FROM
		candidateEnhancer
	WHERE
		featureID='$featureID'
			AND
		targetGeneName='$targetGeneName'";


$resultMaxChrom = mysql_query($queryMaxChrom, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultMaxChrom)){
	$maximum = $row['maximum'];
}


$querychromosome = "
	SELECT
		chromosome
	FROM
		candidateEnhancer
	WHERE
		featureID='$featureID'
			AND
		targetGeneName='$targetGeneName'";


$resultchromosome = mysql_query($querychromosome, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultchromosome)){
	$chromosome = $row['chromosome'];
}

echo "browser position " . $chromosome . ":" . $minimum . "-" .$maximum . "\n";
echo "track name=iRegulon_" . $feature . "_" . $targetGeneName . "_" . $transcriptionFactor . " description=\"iRegulon enchancer location for transcription factor " . $transcriptionFactor . " and target gene " . $targetGeneName . "\" useScore=1" . "\n";

$queryMaxRank = "
	SELECT
		MAX(rank)
	AS
		maxRank
	FROM
		candidateEnhancer
	WHERE
		featureID='$featureID'
			AND
		targetGeneName='$targetGeneName'";


$resultMaxRank = mysql_query($queryMaxRank, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultMaxRank)){
	$maxRank = $row['maxRank'];
}

$queryMinRank = "
	SELECT
		MIN(rank)
	AS
		minRank
	FROM
		candidateEnhancer
	WHERE
		featureID='$featureID'
			AND
		targetGeneName='$targetGeneName'";


$resultMinRank = mysql_query($queryMinRank, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultMinRank)){
	$minRank = $row['minRank'];
}

$scale = 1000 / $maxRank;

$queryEverything = "
	SELECT
		*
	FROM
		candidateEnhancer
	WHERE
		featureID='$featureID'
			AND
		targetGeneName='$targetGeneName'";


$resultEverything = mysql_query($queryEverything, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));
while($row = mysql_fetch_array($resultEverything)){
	$rank = $row['rank'];
	$targetGeneName = $row['targetGeneName'];
	$chromosome = $row['chromosome'];
	$startInclusive = $row['startInclusive'];
	$endExclusive = $row['endExclusive'];
	$regionName = $row['regionName'];
	$score = ($rank - $minRank) * $scale;
	if ($minRank == $maxRank){
		$score = 1000;
	}
	echo $chromosome . "\t" . $startInclusive . "\t" . $endExclusive . "\t" . "$regionName" . "\t" . $score . "\n";
}


?>
