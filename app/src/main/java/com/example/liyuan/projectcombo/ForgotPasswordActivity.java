package com.example.liyuan.projectcombo;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
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
import com.example.liyuan.projectcombo.R;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;

public class ForgotPasswordActivity extends Activity implements View.OnClickListener {

    public static final String SENDEMAIL_URL = "http://cambo.atwebpages.com/android_login_api/sendemail.php";
    public static final String KEY_EMAIL = "email";
    EditText edSendEmail;
    Button btnSendEmail, btnLogin;
    String email, password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);
        edSendEmail = (EditText) findViewById(R.id.sendemail);

        btnSendEmail.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btnLogin);

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

    public void sendEmail() {

        String subject = "Cambo Recovery Password".toString().trim();
        String message = "Greetings! Recently you have requested to recover your password for Cambo. " +
                "Your password is " + password + ". Please try to login and enjoy our app! Thank you!".toString().trim();

        //Creating SendMail object
        SendMail sm = new SendMail(ForgotPasswordActivity.this, email, subject, message);
        sm.execute();

    }

    public void getPassword() {
        boolean emailValid = true;

        email = edSendEmail.getText().toString().trim();
        emailValid = isEmailValid(email);

        if (emailValid == false) {
            Toast.makeText(getApplicationContext(),
                    "Please enter a valid email!", Toast.LENGTH_LONG)
                    .show();
            emailValid = false;
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, SENDEMAIL_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("success")) {
                                password = response.trim().substring(7);
                                sendEmail();

                            } else {
                                String error = "The email you entered is not registered with Cambo. Please try again.";
                                Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ForgotPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(KEY_EMAIL, email);
                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

//            Intent i = new Intent(getApplicationContext(),
//                    welcomePage.class);
//            startActivity(i);
//            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSendEmail) {
            getPassword();
        }
    }

    public void onBackPressed() {
        finish();
    }


}
