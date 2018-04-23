package com.example.maniekcs1995.defotapp;

/**
 * Created by maniekcs1995 on 2018-04-21.
 */

public class Api {
    private static final String ROOT_URL = "http://192.168.0.102/Api/ApiControl/api.php?apicall=";

    public static final String URL_CREATE_USER = ROOT_URL + "createuser";
    public static final String URL_CHECK_USER = ROOT_URL + "checkuser";
    public static final String URL_DELETE_USER = ROOT_URL + "deleteuser&login=";
}
