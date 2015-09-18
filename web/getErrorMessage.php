<?php

include_once 'common.php';



/* Only allow POST request to this page. */
only_post_requests();

/* Only allow iRegulon client to access this page. */
only_iregulon_client($client_version);



/* Check if a 'jobID' POST field exists and if it contains an integer. */
$jobID = retrieve_post_value('jobID', false, 'int');



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
$query = 'SELECT
		          errorMessage
          FROM
		          jobQueue
          WHERE
		          ID = :jobID
		      AND
		          ip = :ip';

try {
    $stmt = $dbh->prepare($query);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':jobID', $jobID, PDO::PARAM_INT);
    $stmt->bindParam(':ip', $client_ip, PDO::PARAM_STR);
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



/* Get error message from the MySQL table. */
try {
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($row === false) {
        echo("ERROR:\tJob ID doesn't exist.\n");
        exit(1);
    }
    $JobError = str_replace("\n", "\\n", $row['errorMessage']);
} catch (PDOException $e) {
    echo("ERROR:\tGetting error message failed.\n");
    exit(1);
}



if (is_null($JobError)) {
    $JobError = 'No error message.';
}



echo "JOB_ERROR:\t$JobError\n";

?>
