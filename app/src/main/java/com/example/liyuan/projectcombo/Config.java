package com.example.liyuan.projectcombo;

public class Config {

    //URL to our login.php file
    public static final String LOGIN_URL = "http://cambo.atwebpages.com/android_login_api/userlogin.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String EMAIL_SHARED_PREF = "email";

    //This would be used to store the password of current logged in user
    public static final String PASSWORD_SHARED_PREF = "password";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    public static final String EMAIL ="cambonumberone@gmail.com";
    public static final String PASSWORD ="TheBestTeam";
}
