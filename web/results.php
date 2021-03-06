<?php

include_once 'common.php';



/* Only allow POST request to this page. */
only_post_requests();

/* Only allow iRegulon client to access this page. */
only_iregulon_client($client_version);

/* Set Content-type to plain text. */
header('Content-Type: text/plain');



/* Check if a 'jobID' POST field exists and if it contains an integer. */
$jobID = retrieve_post_value('jobID', false, 'int_positive');



/* Load ChIP-seq annotation files. */
$chip_annotation_loaded = false;

$chipCollection_to_description = array();
$chipCollection_to_cluster = array();
$chipCollection_to_TF = array();

function load_chip_annotation($chip_version) {
    global $chipCollection_to_description;
    global $chipCollection_to_cluster;
    global $chipCollection_to_TF;

    if ($chip_version === 'chip_v1') {
        $chipCollection_annotation_file = 'chipCollection-v1.tsv';
        $chipCollection_cluster_file = 'chipCollection-v1.clusters';
    } else if ($chip_version === 'chip_v2') {
        $chipCollection_annotation_file = 'chipCollection-v2.tsv';
        $chipCollection_cluster_file = 'chipCollection-v2.clusters';
    } else if ($chip_version === 'chip_v3') {
        $chipCollection_annotation_file = 'chipCollection-v3.tsv';
        $chipCollection_cluster_file = 'chipCollection-v3.clusters';
    } else if ($chip_version === 'tc_v1') {
        $chipCollection_annotation_file = 'trackCollection-v1.tsv';
        $chipCollection_cluster_file = 'trackCollection-v1.clusters';
    } else {
        return false;
    }

    $chipCollection_to_description = array();
    $chipCollection_to_cluster = array();
    $chipCollection_to_TF = array();

    if ($fh = fopen($chipCollection_annotation_file, 'r')) {
        while (!feof($fh)) {
            $line = fgets($fh);
            $columns = explode("\t", $line);

            if (count($columns) === 4) {
                if ($columns[0][0] != "#") {
                    /* Get track description. */
                    $chipCollection_to_description[$columns[2]] = rtrim($columns[3]);
                }
            }
        }
    }

    if ($fh = fopen($chipCollection_cluster_file, 'r')) {
        while (!feof($fh)) {
            $line = fgets($fh);
            $columns = explode("\t", $line);

            if (count($columns) === 3) {
                if ($columns[0][0] != "#") {
                    /* Get cluster code and add 1000 so it doesn't clash with the motif cluster numbers. */
                    $chipCollection_to_cluster[$columns[0]] = $columns[1] + 1000;
                    /* Get corresponding TF. */
                    $chipCollection_to_TF[$columns[0]] = rtrim($columns[2]);
                }
            }
        }
    }

    return true;
}



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





/* Selecting the nomenclature of the job. */

/* Build query. */
$query_nomenclature = '
    SELECT
            nomenclatureCode
    FROM
            jobQueue
    WHERE
            ID = :jobID
        AND
            ip = :ip';



try {
    $stmt = $dbh->prepare($query_nomenclature);
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



/* Get nomenclature belonging to the provided job ID. */
try {
    $nomenclature_row = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($nomenclature_row === false) {
        echo("ERROR:\tNo nomenclature found.\n");
        exit(1);
    }
    $nomenclature = $nomenclature_row['nomenclatureCode'];
} catch (PDOException $e) {
    echo("ERROR:\tGetting nomenclature failed.\n");
    exit(1);
}





/* Selecting all results (features/motifs) for the provided job ID from the enrichedFeature table. */

/* Build query. */
$query_allfeatures = '
    SELECT
            enrichedFeature.ID,
            enrichedFeature.rankingsdb,
            enrichedFeature.rank,
            enrichedFeature.name,
            enrichedFeature.description,
            enrichedFeature.AUCValue,
            enrichedFeature.NEScore,
            enrichedFeature.clusterNumber,
            enrichedFeature.candidateTargetIDs,
            enrichedFeature.candidateTargetRanks
    FROM
            enrichedFeature,
            jobQueue
    WHERE
            enrichedFeature.jobID = :jobID
        AND
            enrichedFeature.jobID = jobQueue.ID
        AND
            jobQueue.ip = :ip';



try {
    $stmt = $dbh->prepare($query_allfeatures);
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





$output = "";



/* Get all row IDs from the enrichedFeature table that are associated with the provided job ID. */
try {
    while (($row_enrichedFeature = $stmt->fetch(PDO::FETCH_ASSOC)) !== false) {
        $enrichedFeatureID = $row_enrichedFeature['ID'];


        /* Build query for retrieving the feature annotation for the current enriched feature. */
        $query_featureAnnotation = '
            SELECT
                transcriptionFactorName,
                motifSimilarityFDR,
                similarMotifName,
                similarMotifDescription,
                orthologousIdentity,
                orthologousGeneName,
                orthologousSpecies
            FROM
                featureAnnotation
            WHERE
                featureID = :enrichedFeatureID';



        try {
            $stmt_featureAnnotation = $dbh->prepare($query_featureAnnotation);
        } catch (PDOException $e) {
            echo("ERROR:\tPreparing statement failed.\n");
            exit(1);
        }



        /* Bind parameters. */
        try {
            $stmt_featureAnnotation->bindParam(':enrichedFeatureID', $enrichedFeatureID, PDO::PARAM_INT);
        } catch (PDOException $e) {
            echo("ERROR:\tBinding parameters failed.\n");
            exit(1);
        }



        /* Execute prepared statement. */
        try {
            $stmt_featureAnnotation->execute();
        } catch (PDOException $e) {
            echo("ERROR:\tExecuting prepared statement failed.\n");
            exit(1);
        }



        /**
         * Define arrays for storing the feature annotation for the current enriched feature.
         * Each of those arrays will later be imploded to one ";" separated string per array.
         */
        $TF_names = array();
        $TF_motifSimilarityFDR = array();
        $TF_orthologousIdentity = array();

        $TF_similarMotifName = array();
        $TF_similarMotifDescription = array();
        $TF_orthologousGeneName = array();
        $TF_orthologousSpecies = array();


        while (($row_featureAnnotation = $stmt_featureAnnotation->fetch(PDO::FETCH_ASSOC)) !== false) {
            if (is_null($row_featureAnnotation['transcriptionFactorName']) === false) {

                $TF_names[] = $row_featureAnnotation['transcriptionFactorName'];

                $TF_motifSimilarityFDR[] = (is_null($row_featureAnnotation['motifSimilarityFDR']) === false)
                    ? $row_featureAnnotation['motifSimilarityFDR'] : "-1";

                $TF_orthologousIdentity[] = (is_null($row_featureAnnotation['orthologousIdentity']) === false)
                    ? $row_featureAnnotation['orthologousIdentity'] : "-1";

                $TF_similarMotifName[] = (is_null($row_featureAnnotation['similarMotifName']) === false)
                    ? $row_featureAnnotation['similarMotifName'] : "null";

                $TF_similarMotifDescription[] = (is_null($row_featureAnnotation['similarMotifDescription']) === false)
                    ? $row_featureAnnotation['similarMotifDescription'] : "null";

                $TF_orthologousGeneName[] = (is_null($row_featureAnnotation['orthologousGeneName']) === false)
                    ? $row_featureAnnotation['orthologousGeneName'] : "null";

                $TF_orthologousSpecies[] = (is_null($row_featureAnnotation['orthologousSpecies']) === false)
                    ? $row_featureAnnotation['orthologousSpecies'] : "null";
            }
        }

        /* Update results from ChIP-seq database with correct data from ChIP-seq annotation files. */
        if (substr($row_enrichedFeature['rankingsdb'], -7, 4) === 'chip' || substr($row_enrichedFeature['rankingsdb'], -6, 5) === '_tc_v') {
            if ($chip_annotation_loaded === false) {
                /* Load ChIP-seq database annotation only on the first encounter of the rankings database name. */
                if (substr($row_enrichedFeature['rankingsdb'], -7) === 'chip_v1') {
                    $chip_annotation_loaded = load_chip_annotation('chip_v1');
                } else if (substr($row_enrichedFeature['rankingsdb'], -7) === 'chip_v2') {
                    $chip_annotation_loaded = load_chip_annotation('chip_v2');
                } else if (substr($row_enrichedFeature['rankingsdb'], -7) === 'chip_v3') {
                    $chip_annotation_loaded = load_chip_annotation('chip_v3');
                } else if (substr($row_enrichedFeature['rankingsdb'], -6) === '_tc_v1') {
                    $chip_annotation_loaded = load_chip_annotation('tc_v1');
                }
            }

            $row_enrichedFeature['description'] = $chipCollection_to_description[$row_enrichedFeature['name']];
            $row_enrichedFeature['clusterNumber'] = $chipCollection_to_cluster[$row_enrichedFeature['name']];
            $TF_names[0] = $chipCollection_to_TF[$row_enrichedFeature['name']];
            $TF_motifSimilarityFDR[0] = '-1';
            $TF_orthologousIdentity[0] = '-1';
            $TF_similarMotifName[0] = 'null';
            $TF_similarMotifDescription[0] = 'null';
            $TF_orthologousGeneName[0] = 'null';
            $TF_orthologousSpecies[0] = 'null';
        }

        /* Combine all the retrieved info to one line and append to the output so we get a TAB-separated file. */
        $output .= $nomenclature . "\t" .
            $row_enrichedFeature['rankingsdb'] . "\t" .
            $row_enrichedFeature['rank'] . "\t" .
            $row_enrichedFeature['name'] . "\t" .
            $row_enrichedFeature['ID'] . "\t" .
            $row_enrichedFeature['description'] . "\t" .
            $row_enrichedFeature['AUCValue'] . "\t" .
            $row_enrichedFeature['NEScore'] . "\t" .
            $row_enrichedFeature['clusterNumber'] . "\t" .
            $row_enrichedFeature['candidateTargetIDs'] . "\t" .
            $row_enrichedFeature['candidateTargetRanks'] . "\t" .
            implode(";", $TF_names) . "\t" .
            implode(";", $TF_motifSimilarityFDR) . "\t" .
            implode(";", $TF_orthologousIdentity) . "\t" .
            implode(";", $TF_similarMotifName) . "\t" .
            implode(";", $TF_similarMotifDescription) . "\t" .
            implode(";", $TF_orthologousGeneName) . "\t" .
            implode(";", $TF_orthologousSpecies) . "\n";

    }
} catch (PDOException $e) {
    echo("ERROR:\tGetting all features failed.\n");
    exit(1);
}



/* Close connection */
$dbh = null;



echo $output;

?>
