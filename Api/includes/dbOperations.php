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
/*
    function loginExist($login){
        $stmt = $this->conn->prepare("SELECT id FROM user WHERE login=?");
        $stmt->bind_param('s',$login);
        if($stmt->execute()) {
            if ($stmt->fetch()) {
                return true;
            }
        }else {
            return false;
        }
    }

    function emailExist($login){
        $stmt = $this->conn->prepare("SELECT id FROM user WHERE email=?");
        $stmt->bind_param('s',$login);
        if($stmt->execute()) {
            if ($stmt->fetch()) {
                return true;
            }
        }else {
            return false;
        }
    }
    */

    function checkUserId($login){
        $stmt = $this->conn->prepare("SELECT id FROM user WHERE login=?");
        $stmt->bind_param('s',$login);
        if($stmt->execute()) {
            $stmt->bind_result($id);
            if($stmt->fetch()){
                return $id;
            }else{
                return false;
            }
        }
    }

    function logIn($login){
        $stmt = $this->conn->prepare("SELECT pass FROM user WHERE login=?");
        $stmt->bind_param('s',$login);
        if($stmt->execute()) {
            $stmt->bind_result($password);
            if($stmt->fetch()){

                return $password;
            }else{
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

    //++++++++++++++++++++++++++++++++++++


    // DEFOTS

    function createDefot($title, $desc, $url, $user_id){
        $stmt = $this->conn->prepare("INSERT INTO defot VALUES (NULL, ?, ?, ?, now(), ?)");
        $stmt->bind_param('sssi',$title, $desc, $url, $user_id);
        if($stmt->execute())
            return true;
        return false;
    }

    function deleteDefot($id){
        $stmt = $this->conn->prepare("DELETE FROM defot WHERE id = ? ");
        $stmt->bind_param('i',$id);
        if($stmt->execute())
            return true;
        return false;
    }

    function getAllDefots() {
        $stmt = $this->conn->prepare("SELECT * FROM defot");
        $stmt->execute();
        $stmt->bind_result($id,$title,$desc,$url,$date,$user_id);

        $defots = array();

        while($stmt->fetch()){
            $defot = array();
            $defot['id'] = $id;
            $defot['title'] = $title;
            $defot['desc'] = $desc;
            $defot['url'] = $url;
            $defot['date'] = $date;
            $defot['user_id'] = $user_id;

            array_push($defots, $defot);
        }

        return $defots;
    }

    function getOneDefot($id) {
        $stmt = $this->conn->prepare("SELECT * FROM defot WHERE id = ?");
        $stmt->bind_param('i',$id);
        $stmt->execute();
        $stmt->bind_result($id,$title,$desc,$url,$date,$user_id);
        $stmt->fetch();

        $defot = array();

        $defot['id'] = $id;
        $defot['title'] = $title;
        $defot['desc'] = $desc;
        $defot['url'] = $url;
        $defot['date'] = $date;
        $defot['user_id'] = $user_id;

        return $defot;
    }

    //+++++++++++++++++++++++++++++++++++


    // COMMENTS

    function createComment($defot_id, $user_id, $content){
        $stmt = $this->conn->prepare("INSERT INTO comments VALUES (NULL, ?, ?, ?, now())");
        $stmt->bind_param('iis',$defot_id, $user_id, $content);
        if($stmt->execute())
            return true;
        return false;
    }

    function deleteComment($id){
        $stmt = $this->conn->prepare("DELETE FROM comments WHERE id = ? ");
        $stmt->bind_param('i',$id);
        if($stmt->execute())
            return true;
        return false;
    }


    function getDefotComments($defot_id) {
        $stmt = $this->conn->prepare("SELECT * FROM comments WHERE defot_id = ?");
        $stmt->bind_param('i',$defot_id);
        $stmt->execute();
        $stmt->bind_result($id,$defot_id,$user_id,$content,$date);

        $comments = array();

        while($stmt->fetch()){
            $comment = array();
            $comment['id'] = $id;
            $comment['defot_id'] = $defot_id;
            $comment['user_id'] = $user_id;
            $comment['content'] = $content;
            $comment['date'] = $date;

            array_push($comments, $comment);
        }

        return $comments;
    }

    function getOneComment($id) {
        $stmt = $this->conn->prepare("SELECT * FROM comments WHERE id = ?");
        $stmt->bind_param('i',$id);
        $stmt->execute();
        $stmt->bind_result($id,$defot_id,$user_id,$content,$date);
        $stmt->fetch();

        $comment = array();

        $comment['id'] = $id;
        $comment['defot_id'] = $defot_id;
        $comment['user_id'] = $user_id;
        $comment['content'] = $content;
        $comment['date'] = $date;

        return $comment;
    }

    //++++++++++++++++
    //RATING

    function getDefotRating($defotId){
        $stmt = $this->conn->prepare("SELECT SUM(value) FROM rating WHERE defot_id = ?");
        $stmt->bind_param('i', $defotId);
        if ($stmt->execute()) {
            $stmt->bind_result($rating);
            $stmt->fetch();

            if($rating != null) {
                return $rating;
            }else{
                return 0;
            }
        }else{
            return null;
        }
    }

    function rateDefot($defotId, $userId, $rate){
        $stmt = $this->conn->prepare("INSERT INTO rating VALUES (NULL, ?, ?, ?)");
        $stmt->bind_param('iii',$userId, $defotId, $rate);
        if($stmt->execute()){
            return true;
        }else{
            return false;
        }

    }

    function isDefotRated($defotId, $userId){
        $stmt = $this->conn->prepare("SELECT id FROM rating WHERE defot_id=? AND user_id=?");
        $stmt->bind_param('ii',$defotId, $userId);
        if($stmt->execute()){
            $stmt->bind_result($id);

            if($stmt->fetch()) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }

    function getAllDefotsRating(){
        $stmt = $this->conn->prepare("SELECT * FROM rating ORDER BY defot_id");
        $stmt->bind_result($id, $userId, $defotId, $value);
        $stmt->execute();

        $ratings = array();

        while($stmt->fetch()) {
            $rating = array();

            $rating['id'] = $id;
            $rating['userId'] = $userId;
            $rating['defotId'] = $defotId;
            $rating['value'] = $value;

            array_push($ratings, $rating);
        }

        return $ratings;
    }

}