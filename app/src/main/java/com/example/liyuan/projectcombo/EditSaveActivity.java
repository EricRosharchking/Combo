
package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.io.Serializable;
import java.util.Arrays;

public class EditSaveActivity extends ActionBarActivity implements Serializable, DialogInterface.OnClickListener {

    String[] names;
    String name;
    String author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editscore);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScoreFile scoreFile = (ScoreFile) getIntent().getSerializableExtra("ScoreFile2");
        names = new String[]{"1", "2", "3"};
        Context context = App.getAppContext();
        name = "New Score";
        author = "Anonymous";

        if (context != null) {

            Object[] objectArray = scoreFile.openAllFileNames().getAllFileNamesSet().toArray();
            names = Arrays.copyOf(objectArray, objectArray.length, String[].class);
        } else {
            Log.e("Error@SaveActivity38", "Context is null");
        }

        try {
            Dialog d = onCreateDialog(savedInstanceState);
            d.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = EditSaveActivity.this;
        AlertDialog.Builder builder;
        //ArrayList<String> mSelectedItems = new ArrayList();  // Where we track the selected items
        if (context != null) {
            Log.d("Log@SaveActivity56", "Context is not null");
            builder = new AlertDialog.Builder(context, R.style.MyDialog);

            LayoutInflater inflater = LayoutInflater.from(context);
            // Set the dialog title

            builder.setTitle(R.string.save)
                    .setView(inflater.inflate(R.layout.edit_dialog_save, null))
                    .setPositiveButton("Save", this)
                    .setNegativeButton("Cancel", this);

        } else {
            Log.e("CreateScoreActivity Log", "Context is Null");
            builder = null;
        }

        return builder.create();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.i("ListenerLog@158", "Button is " + which);
        if(which == -1) {
            //save
            Log.d("SaveActivity118", "Save Clicked");

            Dialog view = (Dialog) dialog;
            EditText nameField = (EditText)view.findViewById(R.id.name_Field);
//            EditText authorField = (EditText)view.findViewById(R.id.author_Field);

            CharSequence sequence1 = nameField.getText();
//            CharSequence sequence2 = authorField.getText();

            name = sequence1.toString();

//            Log.d("Log@SaveActivity131", "FileName" + sequence1.length() + sequence2.length() + name);
//            if (sequence2.length() > 0) {
//                author = sequence2.toString();
//            }

            Score score = (Score) getIntent().getSerializableExtra("Score2");
            Log.i("Log@Save136", "score is null? " + (score == null));
            score.setTitle(name);
            score.setAuthor(author);
            int[] array = score.getScore();
            Log.d("Log@Save139", "array is null? " + (array == null) + array.length);
            ScoreFile scoreFile = (ScoreFile) getIntent().getSerializableExtra("ScoreFile");
            try{
                scoreFile.save(score);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Log@SaveActivity146", "Save Clicked " + " " + name);
            Log.d("Log@SaveActivity147", "Save Clicked " + " " + author);
            finish();
        } else if (which == -2) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
