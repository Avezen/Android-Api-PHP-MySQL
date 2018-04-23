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


        case 'createuser':
            isTheseParametersAvailable(array('login', 'pass', 'email'));

            $db = new DbOperations();

            $result = $db->createUser(
                $_POST['login'],
                $_POST['pass'],
                $_POST['email']
            );


            if($result){
                $response['error'] = false;

                $response['message'] = 'User registered successfully';

            }else{
                $response['error'] = true;

                $response['message'] = 'Some error occurred please try again';
            }
            break;


        case 'checkuser':
            isTheseParametersAvailable(array('login', 'pass'));

            $db = new dbOperations();
            $response['error'] = false;
            $response['message'] = 'Request successfully completed';
            $response['doUserExist'] = $db->checkUser(
                $_POST['login'],
                $_POST['pass']
            );
            break;


        case 'deleteuser':
            if(isset($_GET['login'])){

                $db = new dbOperations();

                if($db->deleteUser($_GET['login'])){
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
    }

}else{

    $response['error'] = true;
    $response['message'] = 'Invalid API Call';
}

//displaying the response in json structure
echo json_encode($response);