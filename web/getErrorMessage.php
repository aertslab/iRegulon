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
	errorMessage
FROM
	jobQueue
WHERE
	ID='$jobID'";


$result = mysql_query($query, $Connection);
if (mysql_errno($Connection)) die (mysql_error($Connection));

$output = "No error Message";
while($row = mysql_fetch_array($result)){
	$output = $row[0];
	//echo "a error";
	//print_r($row);
}


echo $output;



?>