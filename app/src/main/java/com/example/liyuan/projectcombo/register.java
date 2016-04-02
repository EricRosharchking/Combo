package com.example.liyuan.projectcombo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class register extends ActionBarActivity implements View.OnClickListener {

    public static final String REGISTER_URL = "http://cambo.atwebpages.com/android_login_api/userregister.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    private EditText edUName, edEmail, edPassword, edCPassword;
    private Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edUName = (EditText) findViewById(R.id.username);
        edEmail = (EditText) findViewById(R.id.email);
        edPassword = (EditText) findViewById(R.id.password);
        edCPassword = (EditText) findViewById(R.id.confirm_password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister.setOnClickListener(this);

        // Link to Login Screen
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        welcomePage.class);
                startActivity(i);
                finish();
            }
        });
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void registerUser(){
        final String username = edUName.getText().toString().trim();
        final String password = edPassword.getText().toString().trim();
        final String email = edEmail.getText().toString().trim();
        final String cpassword = edCPassword.getText().toString().trim();

        boolean credentials = true;
        boolean passwordmatch = true;
        boolean emailValid = true;


        if(!password.equals(cpassword)){
            Toast.makeText(getApplicationContext(),
                    "Password and confirm password do not match!", Toast.LENGTH_LONG)
                    .show();
            passwordmatch = false;
        }

        if(password.length() < 6 || cpassword.length() < 6){
            Toast.makeText(getApplicationContext(),
                    "Password needs to have at least 6 characters!", Toast.LENGTH_LONG)
                    .show();
            passwordmatch = false;
        }

        emailValid = isEmailValid(email);

        if(emailValid == false){
            Toast.makeText(getApplicationContext(),
                    "Please enter a valid email!", Toast.LENGTH_LONG)
                    .show();
            emailValid = false;
        }



        if (!username.isEmpty() && !email.isEmpty() && passwordmatch==true && emailValid==true) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(register.this,response,Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(register.this,error.toString(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(KEY_USERNAME,username);
                    params.put(KEY_PASSWORD,password);
                    params.put(KEY_EMAIL, email);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

            Intent i = new Intent(getApplicationContext(),
                    welcomePage.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Please enter your details!", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void onClick(View v) {
        if(v == btnRegister){
            registerUser();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
