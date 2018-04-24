<?php

class dbOperations
{
    private $conn;

    function __construct()
    {
        require_once dirname(__FILE__) . '/dbConn.php';

        $db = new dbConnection;
        $this->conn = $db->connect();
    }

    function createUser($login, $password, $email){
        $stmt = $this->con->prepare("INSERT INTO 'user' ('login', 'pass', 'email', 'isAdmin', 'date', 'isActive') VALUES (?, ?, ?, ?, now(), ?)");
        $stmt->bind_param($login, $password, $email, 0, 1);
        if($stmt->execute())
            return true;
        return false;
    }

    function checkUser($login, $password){
        $stmt = $this->con->prepare("SELECT id FROM 'user' WHERE 'login'=? AND 'pass'=?");
        $stmt->bind_param($login, $password);
        if($stmt->execute())
            return true;
        return false;
    }

    function deleteUser($login){
        $stmt = $this->con->prepare("DELETE FROM 'user' WHERE login = ? ");
        $stmt->bind_param($login);
        if($stmt->execute())
            return true;

        return false;
    }
}