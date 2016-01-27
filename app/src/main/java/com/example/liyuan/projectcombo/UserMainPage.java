package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.example.liyuan.projectcombo.helper.SQLiteHandler;
import com.example.liyuan.projectcombo.helper.SessionManager;

public class UserMainPage extends ActionBarActivity {

    private SQLiteHandler db;
    private SessionManager session;
    Button btnLogout;
    Button createNewSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);
        createNewSong = (Button)findViewById(R.id.createNewSong);
        createNewSong.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnLogout = (Button) findViewById(R.id.btnLogout);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

//        if (!session.isLoggedIn()) {
//            logoutUser();
//        }
        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);
        db.deleteUsers();
        AlertDialog.Builder ald = new AlertDialog.Builder(UserMainPage.this);
        ald.setTitle("WARNING");
        ald.setMessage("Are you sure you want to log out as current user?");
        ald.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent i = new Intent(getApplicationContext(),welcomePage.class);
                startActivity(i);
                finish();
            }
        });
        ald.setNegativeButton("Cancel", null);
        ald.show();
    }
    public void onBackPressed() {
        AlertDialog.Builder ald = new AlertDialog.Builder(UserMainPage.this);
        ald.setTitle("WARNING");
        ald.setMessage("Are you sure you want to log out as current user?");
        ald.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent i = new Intent(getApplicationContext(),welcomePage.class);
                startActivity(i);
                finish();
            }
        });
        ald.setNegativeButton("Cancel", null);
        ald.show();
    }

}
