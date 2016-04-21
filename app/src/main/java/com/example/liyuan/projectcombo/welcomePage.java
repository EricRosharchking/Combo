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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.liyuan.projectcombo.helper.SessionManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class welcomePage extends ActionBarActivity implements View.OnClickListener {

    private String userName;
    private String userEmail;
    private Button btnLogin, btnRegister, btnForgot;
    private EditText edEmail, edPassword, edSendEmail;
    TextView tv;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;

    Profile profile = null;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        session = new SessionManager(getApplicationContext());
        userEmail = "";
        userName = "";
        setContentView(R.layout.activity_welcome_page);
        edEmail = (EditText) findViewById(R.id.email);
        edPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnForgot = (Button) findViewById(R.id.btnForgot);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
        loginButton.setReadPermissions(permissions);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
//                        Log.d("LOGIN_SUCCESS", "Success");
                        AccessToken accessToken = loginResult.getAccessToken();
                        profile = Profile.getCurrentProfile();

                        // Facebook Email address
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
//                                        Log.v("LoginActivity Response ", response.toString());

                                        try {
                                            userName = object.getString("name");
                                            userEmail = object.getString("email");

                                            Intent intent = new Intent(welcomePage.this, MainActivity.class);
                                            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                            userEmail = sharedPref.getString("userEmail", userEmail);
                                            userName = sharedPref.getString("userName", userName);

//                                            Log.v("FB NAME Shared login: ", " " + userName);
//                                            Log.v("FB EMAIL Shared login: ", " " + userEmail);

                                            if (userEmail != null)
                                                intent.putExtra("userEmail", userEmail);
                                            if (userName != null && !userName.isEmpty())
                                                intent.putExtra("userName", userName);

                                            displayWelcomeMessage(profile);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(), "Name " + userName, Toast.LENGTH_LONG).show();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();


                        finish();
                    }

                    @Override
                    public void onCancel() {
//                        Log.d("LOGIN_CANCEL", "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
//                        Log.d("LOGIN_ERROR", "Error");
                    }
                });

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayWelcomeMessage(newProfile);
            }
        };

        mProfileTracker.startTracking();

        //Adding click listener
        btnLogin.setOnClickListener(this);

        // Link to Register Screen
        btnRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        register.class);
                startActivity(i);
            }
        });

        //Link to forgot password screen
        btnForgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }

    private void displayWelcomeMessage(Profile profile){
        if(profile!=null){
            userName = profile.getName();
            userEmail = "";
            Toast.makeText(welcomePage.this, "Welcome " + userName, Toast.LENGTH_LONG).show();
            //text.setText("Welcome " + profile.getName());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProfileTracker.stopTracking();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
        Profile profile = Profile.getCurrentProfile();

        if(profile!=null){
            //We will start the Profile Activity
            Intent intent = new Intent(welcomePage.this, MainActivity.class);
            String userName = profile.getName();
            intent.putExtra(userName, "userName");
            startActivity(intent);
        }

        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start Main Activity
            Intent intent = new Intent(welcomePage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

                            //Starting profile activity
                            userEmail = email;
                            userName = response.trim().substring(7);

                            //Adding values to editor
                            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
                            editor.putString(Config.EMAIL_SHARED_PREF, userEmail);
                            editor.putString(Config.UNAME_SHARED_PREF, userName);

                            //Saving values to editor
                            editor.commit();

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
        Intent intent = new Intent(welcomePage.this, MainActivity.class);
        if (userEmail != null)
            intent.putExtra("userEmail", userEmail);
        if (userName != null)
            intent.putExtra("userName", userName);
        startActivity(intent);
        Toast.makeText(welcomePage.this, "Welcome " + userName, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        //Calling the login function
        if(v == btnLogin){
            login();
        }
    }
}