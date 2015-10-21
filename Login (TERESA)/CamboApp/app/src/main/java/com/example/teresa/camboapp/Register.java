package com.example.teresa.camboapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class Register extends AppCompatActivity {
    EditText etName, etUname, etPassword, etEmail, etConfirmPassword;
    String name, username, password, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = (EditText)findViewById(R.id.name);
        etUname = (EditText)findViewById(R.id.new_username);
        etPassword = (EditText)findViewById(R.id.new_password);
        etEmail = (EditText)findViewById(R.id.new_email);
        etConfirmPassword = (EditText)findViewById(R.id.confirm_password);
    }

    public void userReg(View v){
        name = etName.getText().toString();
        username = etUname.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        String method = "register";

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method,name,username,password,email);
        finish();
    }

}
