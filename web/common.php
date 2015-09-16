<?php

/* Get IP address of the user. */
$client_ip = $_SERVER['REMOTE_ADDR'];

/* Get iRegulon client version. */
$client_version = $_SERVER['HTTP_USER_AGENT'];




/* Only allow POST requests. */
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('HTTP/1.1 403 Forbidden');
    exit(1);
}




/* Check if the page is requested by the iRegulon client. */
if (strncmp($client_version, 'iRegulon', 8) !== 0) {
    header('HTTP/1.1 403 Forbidden');
    exit(1);
}




class Job_status_codes {
    const REQUESTED = '1';
    const RUNNING = '2';
    const FINISHED = '3';
    const ERROR = '-1';
}




function get_mysql_connection_parameters() {
    /* Path to configuration file which contain the parameters to connect to the MySQL database. */
    $ini_file = 'configuration.ini';

    if (! file_exists($ini_file)) {
        echo("ERROR:\tConfiguration file not found.\n");
        exit(1);
    };

    /* Get parameters for connecting to the MySQLDatabase. */
    $ini_array = parse_ini_file($ini_file);

    return array('servername' => $ini_array['servername'],
                 'username'   => $ini_array['username'],
                 'password'   => $ini_array['password'],
                 'database'   => $ini_array['database'],
                 'port'       => $ini_array['port']);
}




function retrieve_post_value($post_field, $can_be_null = true, $type = 'string') {
    /*
     * Do some sanity checks when retrieving values from POST fields.
     *   - Check if the POST field exist.
     *   - Check if the POST value is a string (arrays can give unexpected results).
     *   - Check if the POST value is not empty.
     *   - If one of the conditions is not met, the POST value will be set to NULL.
    */
    $post_value = ( isset($_POST["$post_field"])
    && is_string($_POST["$post_field"])
    && $_POST["$post_field"] !== ""
        ? $_POST["$post_field"] : NULL );

    /*
     * If we require the specified POST field to be present ( $can_be_null = false ),
     * error out if this is not the case.
    */
    if ($can_be_null === false && $post_value === NULL) {
        echo("ERROR:\t'$post_field' must be specified.\n");
        exit(1);
    }

    /* Don't try to convert to right type (int, float, string) but return the result directly. */
    if ($can_be_null === true && $post_value === NULL) {
        return $post_value;
    }

    /* Check if our field contains the right type (int, int_positive, float_positive or string). */
    if ($type === 'int') {
        if ($post_value != strval(intval($post_value))) {
            echo("ERROR:\t'$post_field' must be an integer value.\n");
            exit(1);
        };

        $post_value = intval($post_value);
    } elseif ($type === 'int_positive') {
        if ($post_value != strval(intval($post_value))) {
            echo("ERROR:\t'$post_field' must be an integer value.\n");
            exit(1);
        };

        $post_value = intval($post_value);

        if ($post_value < 0) {
            echo("ERROR:\t'$post_field' must be an integer value greater than or equal to 0.\n");
            exit(1);
        };
    } elseif ($type === 'float_positive') {
        if ($post_value != strval(floatval($post_value))) {
            echo("ERROR:\t'$post_field' must be a float value.\n");
            exit(1);
        };

        $post_value = floatval($post_value);

        if ($post_value < 0) {
            echo("ERROR:\t'$post_field' must be a float value greater than or equal to 0.\n");
            exit(1);
        };
    } else {
        $post_value = strval($post_value);
    }

    return $post_value;
}

?>
