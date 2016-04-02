package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

public class CreateScoreActivity extends ActionBarActivity implements DialogInterface.OnClickListener {
    String name;
    String[] names;
    private final int EXPORT = 1;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ScoreFile scoreFile = (ScoreFile) getIntent().getSerializableExtra("ScoreFile");
        action = getIntent().getIntExtra("action", 0);
        names = new String[]{"1", "2", "3"};
        Context context = App.getAppContext();
        //scoreFile.setContext(CreateScoreActivity.this);
        if (context != null) {

            Object[] objectArray = scoreFile.openAllFileNames().getAllFileNamesSet().toArray();
            Log.d("@NewActivity34", "Object Size is " + objectArray.length);
            names = Arrays.copyOf(objectArray, objectArray.length, String[].class);
			if (names.length > 0)
                name = names[0];
        } else {
            //scoreFile = new ScoreFile(CreateScoreActivity.this);
            scoreFile = new ScoreFile();
            Log.e("Error@NewActivity38", "Context is null");
        }

        try {
            Dialog d = onCreateDialog(savedInstanceState);
            d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    CreateScoreActivity.this.finish();
                }
            });
            d.show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = CreateScoreActivity.this;
        AlertDialog.Builder builder;
        //ArrayList<String> mSelectedItems = new ArrayList();  // Where we track the selected items
        if (context != null) {
            Log.d("Dialog Log", "Context is not null");
            builder = new AlertDialog.Builder(context,R.style.MyDialog);

            // Set the dialog title

            builder.setTitle(R.string.start)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setSingleChoiceItems(names, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Log.d("Check Log", "Item selected is " + which);
                                    name = names[which];
                                    Log.e("Log@New73", "Item checked is  " + which);
                                    int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                                    name = names[selectedPosition];
                                }
                            })
                    // Set the action buttons
                    .setPositiveButton("Open", this)
                    .setNegativeButton("New", this);

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
        //name = names[which];
        if (which == -1) {
            Log.d("Log@NewActivity92", "Opening " + name);
            //dialog.
            ScoreFile scoreFile = new ScoreFile();
            try {
                ScoreStatus scorestatus = scoreFile.open(name);
                Log.i("Log@New104", "scorestatus is null? " + (scorestatus == null));
                Score score= scorestatus.getScore();
                Log.i("Log@New106", "score is null? " + (score == null));
                int[] array = score.getScore();
                Log.i("Log@New108", "array is null? " + (array == null));
                Intent intent = new Intent(this, MainActivity.class);
                if (action == EXPORT)
                    intent = new Intent(this, DisplayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("score", score);
                Log.d("Log@NewActivity102", "Starting Activity for Result");
                //startActivityForResult(intent, 1);
                intent.putExtra("userName", getIntent().getStringExtra("userName"));
                intent.putExtra("userEmail", getIntent().getStringExtra("userEmail"));
                intent.putExtra("userScore", name);
                Log.d("Log@New111", "score name to be sent "+name);
                startActivity(intent);
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                finish();
            }
        } else if (which == -2) {
            Log.d("", "New Clicked");
            if (getIntent().getIntExtra("parentActivity", 0) == 1) {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getIntExtra("parentActivity", 0) == 1) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        finish();
    }
}
