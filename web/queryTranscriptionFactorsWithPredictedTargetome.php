<?php
$ini_array = parse_ini_file("configuration.ini");
$servername = $ini_array['servername'];
$username = $ini_array['username'];
$password = $ini_array['password'];
$database = $ini_array['database'];

$speciesnomenclature_code = $_POST["SpeciesNomenclatureCode"];

$connection = mysql_connect($servername, $username, $password);
mysql_select_db($database, $connection) or die(mysql_error($connection));

$query = "SELECT DISTINCT(tfName) FROM metatargetomeMetaData WHERE speciesNomenclatureCode = " . $speciesnomenclature_code;
echo "#Available factors for speciesNomemclature with code = " . $speciesnomenclature_code . "\n";
$result = mysql_query($query, $connection);
if (mysql_errno($connection)) die (mysql_error($connection));

while($row = mysql_fetch_array($result)){
	echo "ID=" . $row[0] . "\n";
}
?>