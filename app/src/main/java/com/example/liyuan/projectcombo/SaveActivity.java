package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.Arrays;

public class SaveActivity extends ActionBarActivity {
    String[] names;
    String name;
    String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScoreFile scoreFile = (ScoreFile) getIntent().getSerializableExtra("ScoreFile");
        names = new String[]{"1", "2", "3"};
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
        Context context = SaveActivity.this;
        AlertDialog.Builder builder;
        //ArrayList<String> mSelectedItems = new ArrayList();  // Where we track the selected items
        if (context != null) {
            Log.d("Dialog Log", "Context is not null");
            builder = new AlertDialog.Builder(context);

            LayoutInflater inflater = LayoutInflater.from(context);
            // Set the dialog title

            builder.setTitle(R.string.save)
                    .setView(inflater.inflate(R.layout.activity_save, null))
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    // Set the action buttons
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog

                            name = ((TextView) findViewById(R.id.filename)).getText().toString();
                            author = ((TextView) findViewById(R.id.author)).getText().toString();
                            Score score = new Score();
                            score.setTitle(name);
                            score.setAuthor(author);
                            ScoreFile scoreFile = (ScoreFile) getIntent().getSerializableExtra("ScoreFile");
                            try{
                                scoreFile.save(score);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d("SaveDialog Log", "Save Clicked " + id + " " + name);
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

/*
    public class SaveDialogFragment extends DialogFragment {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            // Get the layout inflater
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.layout_save, null))
                    // Add action buttons
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SaveDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }
*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}