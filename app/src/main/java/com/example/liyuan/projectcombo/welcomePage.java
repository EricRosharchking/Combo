package com.example.liyuan.projectcombo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.tv.TvInputService;
import android.service.textservice.SpellCheckerService;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class welcomePage extends ActionBarActivity implements View.OnClickListener {

    private String userEmail, userName;
    private Button btnLogin, btnRegister;
    private EditText edEmail, edPassword;
    TextView tv;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    private CallbackManager callbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        userEmail = "";
        userName = "";
        setContentView(R.layout.activity_welcome_page);
        edEmail = (EditText) findViewById(R.id.email);
        edPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        tv = (TextView) findViewById(R.id.textView2);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("LOGIN_SUCCESS", "Success");
                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();

                        //tv.setText("You are logged in as " + profile.getName());
                        Intent intent = new Intent(welcomePage.this, UserMainPage.class);

                        startActivity(intent);
                        displayWelcomeMessage(profile);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("LOGIN_CANCEL", "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LOGIN_ERROR", "Error");
                    }
                });

        //loginButton.registerCallback(callbackManager, mCallBack);

//        AccessTokenTracker tracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//
//            }
//        };
//
//        ProfileTracker profileTracker = new ProfileTracker() {
//           @Override
//            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile){
//
//            }
//        };

//        tracker.startTracking();
//        profileTracker.startTracking();

//        Intent i = new Intent(this, welcomePage.class);
//        startActivityForResult(i, 2000);

        //Adding click listener
        btnLogin.setOnClickListener(this);

        // Link to Register Screen
        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        register.class);
                startActivity(i);
                finish();
            }
        });
    }

//    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
//        @Override
//        public void onSuccess(LoginResult loginResult) {
//            Log.d("LOGIN_SUCCESS", "Success");
//            AccessToken accessToken = loginResult.getAccessToken();
//            Profile profile = Profile.getCurrentProfile();
//            displayWelcomeMessage(profile);
//            tv.setText("You are logged in as " + profile.getName());
//            Intent intent = new Intent(welcomePage.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        @Override
//        public void onCancel() {
//            Log.d("LOGIN_CANCEL", "Cancel");
//        }
//
//        @Override
//        public void onError(FacebookException error) {
//            Log.d("LOGIN_ERROR", "Error");
//        }
//    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

//        if(requestCode == 2000){
//            Intent in = new Intent(welcomePage.this, MainActivity.class);
//            startActivity(in);
//            finish();
//        }
    }

    private void displayWelcomeMessage(Profile profile){
        if(profile!=null){
            Toast.makeText(welcomePage.this, "Welcome" + profile.getName(), Toast.LENGTH_LONG).show();
            //text.setText("Welcome " + profile.getName());
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
////        if(mTokenTracker != null){
//            mTokenTracker.stopTracking();
////        }
////        if(mProfileTracker!=null){
//            mProfileTracker.stopTracking();
////        }
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();

        if(profile!=null){
            //We will start the Profile Activity
            Intent intent = new Intent(welcomePage.this, MainActivity.class);
            startActivity(intent);
        }

        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(welcomePage.this, MainActivity.class);
            startActivity(intent);
        }
    }

    /**
     * function to verify login details in mysql db
     *
     * */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        welcomePage.super.onBackPressed();
                    }
                }).create().show();
    }

    private void login(){
        //Getting values from edit texts
        final String email = edEmail.getText().toString().trim();
        final String password = edPassword.getText().toString().trim();

        if(email.equals("") || password.equals("")){
            Toast.makeText(welcomePage.this, "Invalid username or password", Toast.LENGTH_LONG).show();
        }

        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //If we are getting success from server
                        if(response.contains("success")){
                            //Creating a shared preference
                            SharedPreferences sharedPreferences = welcomePage.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                            //Creating editor to store values to shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.EMAIL_SHARED_PREF, email);

                            //Saving values to editor
                            editor.commit();

                            //Starting profile activity
                            userEmail = email;
                            userName = response.substring(7);
                            openProfile();

                        }else{
                            //If the server response is not success
                            //Displaying an error message on toast
                            Toast.makeText(welcomePage.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void openProfile(){
        Intent intent = new Intent(welcomePage.this, UserMainPage.class);
        if (userEmail != null)
            intent.putExtra("userEmail", userEmail);
        if (userName != null)
            intent.putExtra("userEmail",userEmail);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),
                "Welcome! Now you can create your song!", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onClick(View v) {
        //Calling the login function
        if(v == btnLogin){
            login();
        }
    }

}
//working login