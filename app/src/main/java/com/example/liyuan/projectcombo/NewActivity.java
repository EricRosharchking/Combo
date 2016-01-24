package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class NewActivity extends ActionBarActivity {
String name;
String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScoreFile scoreFile = (ScoreFile) getIntent().getSerializableExtra("ScoreFile");
        names = new String[]{"1", "2", "3"};
        scoreFile.setContext(NewActivity.this);
        if (scoreFile.getContext() != null) {

            Object[] objectArray = scoreFile.openAllFileNames().getAllFileNamesSet().toArray();
            names = Arrays.copyOf(objectArray, objectArray.length, String[].class);
        }


        try {
            Dialog d = onCreateDialog(savedInstanceState);
            d.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = NewActivity.this;
        AlertDialog.Builder builder;
        //ArrayList<String> mSelectedItems = new ArrayList();  // Where we track the selected items
        if (context != null) {
            Log.d("Dialog Log", "Context is not null");
             builder = new AlertDialog.Builder(context);

            // Set the dialog title

            builder.setTitle(R.string.start)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setSingleChoiceItems(names, 1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("Check Log", "Item selected is " + which);
                                    name = names [which];
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog

                            Log.d("Open Log", "Open Clicked");
                        }
                    })
                    .setNegativeButton("New", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });

        } else {
            Log.e("NewActivity Log", "Context is Null");
            builder = null;
        }

        return builder.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
