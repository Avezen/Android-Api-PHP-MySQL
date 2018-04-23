<?php

class dbConnection
{
    private $conn;

    function __construct()
    {

    }

    function connect()
    {
        include_once dirname(__FILE__) . '/dbInfo.php';

        $this->conn = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

        if (mysqli_connect_errno()) {
            echo "Failed to connect to MySQL: " . mysqli_connect_error();
        }

        return $this->conn;
    }

}