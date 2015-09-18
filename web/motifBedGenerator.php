<?php

include_once 'common.php';



/* Check if a 'featureIDandTarget' GET filed exists. */
$featureID_and_target_get = retrieve_get_value('featureIDandTarget', true, 'string');

if ($featureID_and_target_get !== NULL) {
    /* Check if a 'featureIDandTarget' GET field exists and if it contains an string. */
    $featureID_and_target = retrieve_get_value('featureIDandTarget', false, 'string');
} else {
    /* Check if a 'featureIDandTarget' POST field exists and if it contains an string. */
    $featureID_and_target = retrieve_post_value('featureIDandTarget', false, 'string');
}

$featureID_and_target_array = explode(":", $featureID_and_target);

if (count($featureID_and_target_array) !== 3) {
    echo("ERROR:\tInvalid featureIDandTarget value.\n");
    exit(1);
}

list($featureID, $target_gene_name, $transcription_factor) = $featureID_and_target_array;
$transcription_factors_array = explode(",", $transcription_factor);



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





/* Selecting the feature. */

/* Build query. */
$query_feature = '
    SELECT
            name
    FROM
            enrichedFeature
    WHERE
            ID = :featureID';



try {
    $stmt = $dbh->prepare($query_feature);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
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



/* Get feature. */
try {
    $feature_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($feature_row === false) {
        echo("ERROR:\tNo feature found.\n");
        exit(1);
    }
    $feature = $feature_row['name'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting feature failed.\n");
    exit(1);
}





/* Get minimum chromosomal position to the provided feature. */

/* Build query. */
$query_min_chrom_pos = '
    SELECT
            MIN(startInclusive)
    AS
            minimum
    FROM
            candidateEnhancer
    WHERE
            featureID = :featureID
        AND
            targetGeneName = :targetGeneName';



try {
    $stmt = $dbh->prepare($query_min_chrom_pos);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
    $stmt->bindParam(':targetGeneName', $target_gene_name, PDO::PARAM_STR);
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



/* Get minimum chromosomal position. */
try {
    $min_chrom_pos_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($min_chrom_pos_row === false) {
        echo("ERROR:\tNo minimum chromosomal position found.\n");
        exit(1);
    }
    $min_chrom_pos = $min_chrom_pos_row['minimum'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting minimum chromosomal position failed.\n");
    exit(1);
}





/* Get maximum chromosomal position to the provided feature. */

/* Build query. */
$query_max_chrom_pos = '
    SELECT
            MAX(endExclusive)
    AS
            maximum
    FROM
            candidateEnhancer
    WHERE
            featureID = :featureID
        AND
            targetGeneName = :targetGeneName';



try {
    $stmt = $dbh->prepare($query_max_chrom_pos);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
    $stmt->bindParam(':targetGeneName', $target_gene_name, PDO::PARAM_STR);
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



/* Get maximum chromosomal position. */
try {
    $max_chrom_pos_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($max_chrom_pos_row === false) {
        echo("ERROR:\tNo maximum chromosomal position found.\n");
        exit(1);
    }
    $max_chrom_pos = $max_chrom_pos_row['maximum'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting maximum chromosomal position failed.\n");
    exit(1);
}





/* Get chromosome for the requested feature. */

/* Build query. */
$query_chromosome = '
    SELECT
            chromosome
    FROM
            candidateEnhancer
    WHERE
            featureID = :featureID
        AND
            targetGeneName = :targetGeneName';



try {
    $stmt = $dbh->prepare($query_chromosome);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
    $stmt->bindParam(':targetGeneName', $target_gene_name, PDO::PARAM_STR);
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



/* Get chromosome. */
try {
    $chromosome_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($chromosome_row === false) {
        echo("ERROR:\tNo chromosome found.\n");
        exit(1);
    }
    $chromosome = $chromosome_row['chromosome'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting chromosome failed.\n");
    exit(1);
}





/* Get minimum rank for the requested feature. */

/* Build query. */
$query_min_rank = '
    SELECT
            MIN(rank)
    AS
            minRank
    FROM
            candidateEnhancer
    WHERE
            featureID = :featureID';



try {
    $stmt = $dbh->prepare($query_min_rank);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
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



/* Get minimum rank. */
try {
    $min_rank_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($min_rank_row === false) {
        echo("ERROR:\tNo minimum rank found.\n");
        exit(1);
    }
    $min_rank = $min_rank_row['minRank'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting minimum rank failed.\n");
    exit(1);
}





/* Get maximum rank for the requested feature. */

/* Build query. */
$query_max_rank = '
    SELECT
            MAX(rank)
    AS
            maxRank
    FROM
            candidateEnhancer
    WHERE
            featureID = :featureID';



try {
    $stmt = $dbh->prepare($query_max_rank);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
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



/* Get maximum rank. */
try {
    $max_rank_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($max_rank_row === false) {
        echo("ERROR:\tNo maximum rank found.\n");
        exit(1);
    }
    $max_rank = $max_rank_row['maxRank'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting maximum rank failed.\n");
    exit(1);
}





/* Get all candidate enhancer info for the requested feature. */

/* Build query. */
$query_candidate_enhancer = '
    SELECT
            *
    FROM
            candidateEnhancer
    WHERE
            featureID = :featureID';



try {
    $stmt = $dbh->prepare($query_candidate_enhancer);
} catch (PDOException $e) {
    echo("ERROR:\tPreparing statement failed.\n");
    exit(1);
}



/* Bind parameters. */
try {
    $stmt->bindParam(':featureID', $featureID, PDO::PARAM_STR);
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



$scale = 1000 / $max_rank;

echo 'browser position ' . $chromosome . ':' . $min_chrom_pos . '-' .$max_chrom_pos . "\n";

if (count($transcription_factors_array) < 4) {
    echo 'track name=iRegulon_' . $feature .
         ' description="iRegulon enhancer location for motif ' . $feature . ' and transcription factors: ' .
         implode(', ', $transcription_factors_array) .
         '" useScore=1' . "\n";
} else {
    echo 'track name=iRegulon_' . $feature .
         ' description="iRegulon enhancer location for motif ' . $feature . '" useScore=1' . "\n";
}



/* Get candidate enhancers. */
try {
    while (($candidate_enhancer_row = $stmt->fetch(PDO::FETCH_ASSOC)) !== false) {
        $rank = $candidate_enhancer_row['rank'];
        $target_gene_name = $candidate_enhancer_row['targetGeneName'];
        $chromosome = $candidate_enhancer_row['chromosome'];
        $start_pos_inclusive = $candidate_enhancer_row['startInclusive'];
        $end_pos_exclusive = $candidate_enhancer_row['endExclusive'];
        $region_name = $candidate_enhancer_row['regionName'];
        $score = 1000 - (($rank - $min_rank) * $scale);

        if ($min_rank == $max_rank) {
            $score = 1000;
        }

        echo $chromosome . "\t" . $start_pos_inclusive . "\t" . $end_pos_exclusive . "\t" . "$region_name" . "\t" . $score . "\n";
    }
} catch (PDOException $e) {
    echo("ERROR:\tGetting candidate enhancer failed.\n");
    exit(1);
}



/* Close connection */
$dbh = null;

?>
