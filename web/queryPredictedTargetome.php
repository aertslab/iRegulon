<?php

include_once 'common.php';


/* List of database sources that are accepted to be queried. */
$accepted_database_sources = array(
    'ganesh' => 'ganesh',
    'genesigdb' => 'genesigdb',
    'msigdb' => 'msigdb');



/* Check if the needed POST fields exist and contain the right kind of data. */
$species_nomenclature_code = retrieve_post_value('SpeciesNomenclatureCode', false, 'int');
$gene_id = retrieve_post_value('GeneIdentifier', false, 'string');
$database_ids = retrieve_post_value('TargetomeDatabaseCode', false, 'string');

$database_sources = explode(",", rtrim($database_ids));

foreach ($database_sources as $database_source) {
    if (! array_key_exists($database_source, $accepted_database_sources)) {
        echo("ERROR:\tDatabase source: '$database_source'' is not supported.\n");
        exit(1);
    }
}

$database_sources_str = "'" . implode("','", $database_sources) . "'";



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





/* Build query. */
$query = "SELECT
		          geneName,
		          SUM(occurenceCount)
          FROM
		          metatargetomeMetaData AS m,
		          metatargetome AS t
          WHERE
		          m.sourceName IN (" . $database_sources_str . ")
		     AND
		          m.speciesNomenclatureCode = :speciesNomenclatureCode
		     AND
		          m.tfName = :gene_id
		     AND
		          m.ID = t.metaID
		  GROUP BY geneName";

try {
    $stmt = $dbh->prepare($query);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':speciesNomenclatureCode', $species_nomenclature_code, PDO::PARAM_INT);
    $stmt->bindParam(':gene_id', $gene_id, PDO::PARAM_STR);
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



/* Get gene names and their occurrence from the MySQL table. */
try {
    $rows = $stmt->fetchall(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    echo("ERROR:\tGetting gene names and their occurrence for predicted metatargetome failed.\n");
    exit(1);
}



/* Close connection */
$dbh = null;



echo "# Metatargetome for factor " . $gene_id . "\n";

foreach ($rows as $row) {
    $gene_name = $row['geneName'];
    $occurrence_count = $row['SUM(occurenceCount)'];
    echo "gene_occurrence:\t" . $gene_name . "\t" . $occurrence_count . "\n";
}

?>
