<?php

require_once '../includes/dbOperations.php';

function isTheseParametersAvailable($params){
    $available = true;
    $missingparams = "";

    foreach($params as $param){
        if(!isset($_POST[$param]) || strlen($_POST[$param])<=0){
            $available = false;
            $missingparams = $missingparams . ", " . $param;
        }
    }

    if(!$available){
        $response = array();
        $response['error'] = true;
        $response['message'] = 'Parameters ' . substr($missingparams, 1, strlen($missingparams)) . ' missing';

        echo json_encode($response);

        die();
    }
}


$response = array();


if(isset($_GET['apicall'])){

    switch($_GET['apicall']){

        //USERS

        case 'createuser':
            isTheseParametersAvailable(array('login', 'pass', 'email'));

            $db = new DbOperations();

            $result = $db->createUser(
                $_POST['login'],
                $_POST['pass'],
                $_POST['email']
            );

            if($result){
                $response['apicall'] = 'createuser';
                $response['error'] = false;
                $response['message'] = 'Rejestracja przebiegła pomyślnie';
            }else{
                $response['error'] = true;
                $response['message'] = 'Podany login lub adres email jest już zajęty';
            }
            break;


        case 'loginexist':
            if(isset($_GET['login'])){

                $db = new dbOperations();

                if($db->loginExist($_GET['login'])){
                    $response['apicall'] = 'loginexist';
                    $response['error'] = false;
                    $response['message'] = 'Istnieje konto z podanym loginem';
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Coś poszło nie tak, spróbuj ponownie później';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Podaj login';
            }
            break;

        case 'emailexist':
            if(isset($_GET['email'])){

                $db = new dbOperations();

                if($db->emailExist($_GET['email'])){
                    $response['apicall'] = 'emailexist';
                    $response['error'] = false;
                    $response['message'] = 'Istnieje konto z podanym emailem';
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Coś poszło nie tak, spróbuj ponownie później';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Podaj email';
            }
            break;


        case 'checkuserid':
            if(isset($_GET['login'])) {


                $db = new dbOperations();
                $response['apicall'] = 'checkuserid';
                $response['error'] = false;
                $response['message'] = 'Request successfully completed';
                $response['userId'] = $db->checkUserId(
                    $_GET['login']
                );
            }else{
                $response['error'] = true;
                $response['message'] = 'Podaj login';
            }
            break;

        case 'login':
            isTheseParametersAvailable(array('login'));

            $db = new dbOperations();
            $response['apicall'] = 'login';
            $response['error'] = false;
            $response['message'] = 'Request successfully completed';
            $response['hash']= $db->logIn($_POST['login']);
            break;


        case 'deleteuser':
            if(isset($_GET['login'])){

                $db = new dbOperations();

                if($db->deleteUser($_GET['login'])){
                    $response['apicall'] = 'deleteuser';
                    $response['error'] = false;
                    $response['message'] = 'User deleted successfully';
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Nothing to delete, provide an id please';
            }
            break;


            //+++++++++++++++++++++++++++++++++++++
            //DEFOTS

        case 'createdefot':
            isTheseParametersAvailable(array('title', 'desc', 'url','user_id'));

            $db = new DbOperations();

            $result = $db->createDefot(
                $_POST['title'],
                $_POST['desc'],
                $_POST['url'],
                $_POST['user_id']
            );

            if($result){
                $response['apicall'] = 'createdefot';
                $response['error'] = false;
                $response['message'] = 'Defot added succesfuly';
            }else{
                $response['apicall'] = 'createdefot';
                $response['error'] = true;
                $response['message'] = 'Some error occurred please try again';
            }
            break;

        case 'deletedefot':
            if(isset($_GET['id'])){

                $db = new dbOperations();

                if($db->deleteDefot($_GET['id'])){
                    $response['apicall'] = 'deletedefot';
                    $response['error'] = false;
                    $response['message'] = 'Defot deleted succesfuly';
                }else{
                    $response['apicall'] = 'deletedefot';
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['apicall'] = 'deletedefot';
                $response['error'] = true;
                $response['message'] = 'Please give me defot id you want to delete';
            }
            break;


        case 'getalldefots':
            $db = new dbOperations();

            if($db->getAllDefots()){
                $response['apicall'] = 'getalldefots';
                $response['error'] = false;
                $response['message'] = 'I found you something';
                $response['defots'] = $db->getAllDefots();
            }else{
                $response['error'] = true;
                $response['message'] = 'Some error occurred please try again';
            }
            break;

        case 'getonedefot':
            if(isset($_GET['id'])){

                $db = new dbOperations();

                if($db->getOneDefot($_GET['id'])){
                    $response['apicall'] = 'getonedefot';
                    $response['error'] = false;
                    $response['message'] = 'I found you something';
                    $response['defot'] = $db->getOneDefot($_GET['id']);
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Please give me defot id you want to get';
            }
            break;


        //+++++++++++++++++++++++++++++++++++++
        //COMMENTS

        case 'createcomment':
            isTheseParametersAvailable(array('defot_id', 'user_id', 'content'));

            $db = new DbOperations();

            $result = $db->createComment(
                $_POST['defot_id'],
                $_POST['user_id'],
                $_POST['content']
            );

            if($result){
                $response['apicall'] = 'createcomment';
                $response['error'] = false;
                $response['message'] = 'Comment added succesfuly';
            }else{
                $response['error'] = true;
                $response['message'] = 'Some error occurred please try again';
            }
            break;

        case 'deletecomment':
            if(isset($_GET['id'])){

                $db = new dbOperations();

                if($db->deleteComment($_GET['id'])){
                    $response['apicall'] = 'deletecomment';
                    $response['error'] = false;
                    $response['message'] = 'Comment deleted succesfuly';
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Please give me comment id you want to delete';
            }
            break;


        case 'getdefotcomments':
            if(isset($_GET['defot_id'])){

                $db = new dbOperations();

                if($db->getDefotComments($_GET['defot_id'])){
                    $response['apicall'] = 'getdefotcomments';
                    $response['error'] = false;
                    $response['message'] = "There are all comments of defot (id: ".$_GET['defot_id'].")";
                    $response['comments'] = $db->getDefotComments($_GET['defot_id']);
                }else{
                    $response['apicall'] = 'getdefotcomments';
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['apicall'] = 'getdefotcomments';
                $response['error'] = true;
                $response['message'] = 'Please give me comment id you want to delete';
            }
            break;

        case 'getonecomment':
            if(isset($_GET['id'])){

                $db = new dbOperations();

                if($db->getOneComment($_GET['id'])){
                    $response['apicall'] = 'getonecomment';
                    $response['error'] = false;
                    $response['message'] = 'I found you something';
                    $response['comment'] = $db->getOneComment($_GET['id']);
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Please give me defot id you want to get';
            }
            break;


         //++++++++++++++++++++++++++++++++
        // RATING

        case 'getdefotrating':
            if(isset($_GET['defot_id'])){

                $db = new dbOperations();

                if($db->getDefotRating($_GET['defot_id'])){
                    $response['apicall'] = 'getdefotrating';
                    $response['error'] = false;
                    $response['message'] = 'I found you something';
                    $response['rating'] = $db->getDefotRating($_GET['defot_id']);
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'Please give me defot id you want to get';
            }
            break;

        case 'ratedefot':
            isTheseParametersAvailable(array('defot_id', 'user_id', 'value'));
            $db = new DbOperations();

            $result = $db->rateDefot(
                $_POST['defot_id'],
                $_POST['user_id'],
                $_POST['value']
            );

            if($result){
                $response['apicall'] = 'ratedefot';
                $response['error'] = false;
                $response['message'] = 'Defot rated succesfuly';
            }else{
                $response['error'] = true;
                $response['message'] = 'Some error occurred please try again';
            }
            break;

        case 'isdefotrated':
            if(isset($_GET['defot_id'], $_GET['user_id'])) {
                $db = new dbOperations();

                if ($db->isDefotRated($_GET['defot_id'], $_GET['user_id'])) {
                    $response['apicall'] = 'isdefotrated';
                    $response['error'] = false;
                    $response['isRated'] = $db->isDefotRated($_GET['defot_id'], $_GET['user_id']);
                } else {
                    $response['error'] = true;
                    $response['message'] = 'Some error occurred please try again';
                    $response['isRated'] = $db->isDefotRated($_GET['defot_id'], $_GET['user_id']);
                }
            }else{
                $response['error'] = true;
                $response['message'] = 'No parameters';
            }
            break;

        case 'getalldefotsrating':
            $db = new dbOperations();

            if($db->getAllDefotsRating()){
                $response['apicall'] = 'getalldefotsrating';
                $response['error'] = false;
                $response['message'] = 'I found you something';
                $response['rating'] = $db->getAllDefotsRating();
            }else{
                $response['apicall'] = 'getalldefotsrating';
                $response['error'] = true;
                $response['message'] = 'Some error occurred please try again';
            }
            break;
    }

}else{

    $response['error'] = true;
    $response['message'] = 'Invalid API Call';
}

//displaying the response in json structure
echo json_encode($response);