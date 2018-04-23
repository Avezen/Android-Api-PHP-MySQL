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
        $stmt = $this->conn->prepare("INSERT INTO user VALUES (NULL, ?, ?, ?, 0, now(), 1)");
        $stmt->bind_param('sss',$login,$password, $email);
        if($stmt->execute())
            return true;
        return false;
    }

    function checkUser($login, $password){
        $stmt = $this->conn->prepare("SELECT id FROM user WHERE login=? AND pass=?");
        $stmt->bind_param('ss',$login, $password);
        if($stmt->execute()) {
            $stmt->bind_result($id);
            if ($stmt->fetch()) {
                return true;
            } else {
                return false;
            }
        }
    }

    function deleteUser($login){
        $stmt = $this->conn->prepare("DELETE FROM user WHERE login = ? ");
        $stmt->bind_param('s',$login);
        if($stmt->execute())
            return true;

        return false;
    }
}