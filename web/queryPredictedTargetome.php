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

$databases = explode(",", rtrim($database_ids));
$database_str = "'" . implode("','", $databases) . "'";

$query = "SELECT geneName, SUM(occurenceCount) FROM metatargetomeMetaData AS m, metatargetome AS t WHERE m.sourceName IN (" . $database_str . ") AND m.speciesNomenclatureCode = " . $speciesnomenclature_code . " AND m.tfName = '" . $gene_id . "' AND m.ID = t.metaID GROUP BY geneName";

$result = mysql_query($query, $connection);
if (mysql_errno($connection)) die (mysql_error($connection));

echo "#Metatargetome for factor " . $gene_id . "\n";
while($row = mysql_fetch_array($result)){
	echo "ID_occurenceCount=" . $row[0] . ";" . $row[1] . "\n";
}
?>