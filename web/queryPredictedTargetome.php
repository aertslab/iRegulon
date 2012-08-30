<?php
$ini_array = parse_ini_file("configuration.ini");
$servername = $ini_array['servername'];
$username = $ini_array['username'];
$password = $ini_array['password'];
$database = $ini_array['database'];

$gene_id = $_POST["GeneIdentifier"];
$speciesnomenclature_code = $_POST["SpeciesNomenclatureCode"];
$database_ids = $_POST["TargetomeDatabaseCode"];

$connection = mysql_connect($servername, $username, $password);
mysql_select_db($database, $connection) or die(mysql_error($connection));

$query = "SELECT geneName, SUM(occurenceCount)
FROM metatargetomeMetaData AS m, metatargetome AS t
WHERE m.sourceName IN (" . $database_ids . ")
AND m.speciesNomenclatureCode = " . $speciesnomenclature_code .
"AND m.tfName = '" . $gene_id . "'AND m.metaID = t.ID
GROUP BY geneName;";

$result = mysql_query($query, $connection);
if (mysql_errno($connection)) die (mysql_error($connection));

while($row = mysql_fetch_array($result)){
	print "ID_occurenceCount=" . $row[0] . ";" . $row[1] . "\n";
}
?>