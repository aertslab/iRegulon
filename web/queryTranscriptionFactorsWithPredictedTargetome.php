<?php

include_once 'common.php';



/* Only allow POST request to this page. */
only_post_requests();

/* Only allow iRegulon client to access this page. */
only_iregulon_client($client_version);

/* Set Content-type to plain text. */
header('Content-Type: text/plain');



/* Check if a 'SpeciesNomenclatureCode' POST field exists and if it contains an integer. */
$species_nomenclature_code = retrieve_post_value('SpeciesNomenclatureCode', false, 'int');



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
$query = '
    SELECT DISTINCT
            tfName
    FROM
            metatargetomeMetaData
    WHERE
            speciesNomenclatureCode = :speciesNomenclatureCode';

try {
    $stmt = $dbh->prepare($query);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':speciesNomenclatureCode', $species_nomenclature_code, PDO::PARAM_INT);
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
    $rows = $stmt->fetchall(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    echo("ERROR:\tGetting transcription factors for predicted metatargetome failed.\n");
    exit(1);
}



/* Close connection */
$dbh = null;



foreach ($rows as $row) {
    $TF_name = $row['tfName'];
    echo "TF:\t" . $TF_name . "\n";
}

?>
