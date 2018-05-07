package com.example.maniekcs1995.defotapp;

/**
 * Created by maniekcs1995 on 2018-04-21.
 */

public class Api {
    public static final String ROOT_URL = "http://192.168.0.101/Api/ApiControl/api.php?apicall=";

    public static final String CREATE_USER_URL = ROOT_URL + "createuser";
    public static final String LOGIN_EXIST_URL = ROOT_URL + "loginexist";
    public static final String EMAIL_EXIST_URL = ROOT_URL + "emailexist";
    public static final String CHECK_USER_ID_URL = ROOT_URL + "checkuserid";
    public static final String LOGIN_URL = ROOT_URL + "login";
    public static final String DELETE_USER_URL = ROOT_URL + "deleteuser&login=";
    public static final String CREATE_DEFOT_URL = ROOT_URL + "createdefot";
    public static final String DELETE_DEFOT_URL = ROOT_URL + "deletedefot&id=";
    public static final String GET_ALL_DEFOTS_URL = ROOT_URL + "getalldefots";
    public static final String GET_ONE_DEFOT_URL = ROOT_URL + "getonedefot&id=";
    public static final String CREATE_COMMENT_URL = ROOT_URL + "createcomment";
    public static final String DELETE_COMMENT_URL = ROOT_URL + "deletecomment&id=";
    public static final String GET_DEFOT_COMMENTS_URL = ROOT_URL + "getdefotcomments&defot_id=";
    public static final String GET_ONE_COMMENT_URL = ROOT_URL + "getonecomment&id=";


}
