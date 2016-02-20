package com.example.liyuan.projectcombo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

public class welcomePage extends ActionBarActivity implements View.OnClickListener {

    private String userEmail;
    private Button btnLogin, btnRegister;
    private EditText edEmail, edPassword;
    private CheckBox cbRememberMe;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    //boolean variable to check user is logged in or not
    //initially it is false
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userEmail = "";
        setContentView(R.layout.activity_welcome_page);
        edEmail = (EditText) findViewById(R.id.email);
        edPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        cbRememberMe = (CheckBox) findViewById(R.id.rememberMe);
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


    @Override
    protected void onResume() {
        super.onResume();
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
                        if(response.trim().equals("success")){
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