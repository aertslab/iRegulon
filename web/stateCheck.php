<?php

include_once 'common.php';



/* Only allow POST request to this page. */
only_post_requests();

/* Only allow iRegulon client to access this page. */
only_iregulon_client($client_version);



/* Check if a 'jobID' POST field exists and if it contains an integer. */
$jobID = retrieve_post_value('jobID', false, 'int_positive');



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
		          jobStatusCode
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



/* Get job ID from the MySQL table. */
try {
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $jobStatusCode = $row['jobStatusCode'];

    if (! intval($jobStatusCode)) {
        echo("ERROR:\tjob ID '$jobID' does not exist.\n");
        exit(1);
    }
} catch (PDOException $e) {
    echo("ERROR:\tGetting job status code failed.\n");
    exit(1);
}





$query = 'SELECT
                  name
          FROM
                  jobStatus
          WHERE
                  code = :jobStatusCode';

try {
    $stmt = $dbh->prepare($query);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':jobStatusCode', $jobStatusCode, PDO::PARAM_INT);
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
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($row === false) {
        echo("ERROR:\tJob ID doesn't exist.\n");
        exit(1);
    }
    $jobState = $row['name'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting job state failed.\n");
    exit(1);
}




$query = 'SELECT
                  COUNT(*)
          FROM
                  jobQueue  AS q1,
                  jobQueue  AS q2,
                  jobStatus	AS s
          WHERE
                  q1.ID = :jobID
              AND
                  q1.jobRequestTime > q2.jobRequestTime
              AND
                  q2.jobStatusCode = s.code
              AND
                  s.name = "Requested"';

try {
    $stmt = $dbh->prepare($query);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':jobID', $jobID, PDO::PARAM_INT);
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
    $row = $stmt->fetch();
    $jobsBeforeThis = $row[0];
} catch (PDOException $e) {
    echo("ERROR:\tGetting number of previous jobs failed.\n");
    exit(1);
}



/* Close connection */
$dbh = null;



echo("jobState:\t" . $jobState . "\n");
echo("jobsBeforeThis:\t" . $jobsBeforeThis . "\n");

?>
