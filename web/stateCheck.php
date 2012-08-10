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

$query = "
SELECT
		jobStatusCode
FROM
		jobQueue
WHERE
		ID='$jobID'";


$result = mysql_query($query, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));

$jobState = "ERROR";
$jobStatusCode = -1;
while($row = mysql_fetch_array($result)){
	$jobStatusCode = $row['jobStatusCode'];
}


$query = "
SELECT
	name
FROM
	jobStatus
WHERE
	code='$jobStatusCode'";


$result = mysql_query($query, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));

while($row = mysql_fetch_array($result)){
	$jobState = $row[0];
}

$query = "
SELECT 
	COUNT(*) 
FROM 
	jobQueue 
		AS q1, jobQueue 
		AS q2, jobStatus 
		AS s
WHERE 
	q1.ID = '$jobID' 
		AND 
	q1.jobRequestTime > q2.jobRequestTime 
		AND 
	q2.jobStatusCode = s.code 
		AND 
	s.name = 'Requested'";

//$query = "CALL numberOfJobsInQueueBefore('$jobID')";


$result = mysql_query($query, $Connection);

$jobsBeforeThis = -13;
while($row = mysql_fetch_array($result)){
	$jobsBeforeThis = $row[0];
}

mysql_close($Connection);

echo $jobState . "\t" . $jobsBeforeThis;

?>