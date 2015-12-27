package com.example.liyuan.projectcombo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddLyrics extends ActionBarActivity {

    TableLayout tbLayout;
    TextView tvScores1;
    EditText etLyrics1;
    Button btnSaveLyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lyrics);

        tvScores1 = (TextView) findViewById(R.id.textViewScores1);
        etLyrics1 = (EditText) findViewById(R.id.editTextLyrics1);
        btnSaveLyrics = (Button) findViewById(R.id.buttonSaveLyrics);

        //retrieve scores (in String) from MainActivity
        Intent intent = getIntent();
        String rawScores = intent.getStringExtra("scores");

		
        //String scores = rawScores.replace("_", " ");

        //tvScores1.setText(scores);

        int rawScoresLength = rawScores.length();

        tbLayout = (TableLayout) findViewById(R.id.tableLayout);

        tbLayout.removeAllViews();

        TableRow tbRow = new TableRow(this);

        //for illustration purpose
        TextView tv = new TextView(this);

        for(int i=0; i<rawScoresLength; i++){
            //create new textview for each char in rawScores
            TextView tvScores = new TextView(this);
            char c = rawScores.charAt(i);
            //set to char
            tvScores.setText(c + "");
            Log.d("c", "the score at position c is: " + c);
            tbRow.addView(tvScores);
        }

        TableRow tbRow1 = new TableRow(this);
        tbLayout.addView(tbRow,
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

        for(int i=0; i<rawScoresLength; i++){
            EditText etLyrics = new EditText(this);
            etLyrics.setText(" ");
            tbRow1.addView(etLyrics);
        }
        tbLayout.addView(tbRow1,
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));

        Button btSave = new Button(this);
        btSave.setBackgroundColor(484848);
        btSave.setTextColor(Color.WHITE);
        TableRow tbRow2 = new TableRow(this);
        tbRow2.addView(btSave);
        tbLayout.addView(tbRow2,
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,
                                TableLayout.LayoutParams.WRAP_CONTENT));



        /*
        String[] toDisplay = split(scores);
        for(int i=0; i<toDisplay.length; i++){
            tvScores1.setText(toDisplay[i]);
        }*/


        /*//save as pdf to internal storage
        btnSaveLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileOutputStream out = null;

                try{
                    String data = tvScores1.getText().toString().trim();

                    String filename = "Song";

                    out = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);

                    //data is your string or byte block or whatever
                    out.write(data.getBytes(Charset.forName("UTF-8")));

                    out.close();
                } catch(FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });*/
		
        String scores = rawScores.replace("_", " ");

        tvScores1.setText(scores);

        /*
        String[] toDisplay = split(scores);
        for(int i=0; i<toDisplay.length; i++){
            tvScores1.setText(toDisplay[i]);
        }*/


        //save as pdf to internal storage
        btnSaveLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FileOutputStream out = null;

                try{
                    String data = tvScores1.getText().toString().trim();

                    String filename = "Song";

                    out = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);

                    //data is your string or byte block or whatever
                    out.write(data.getBytes(Charset.forName("UTF-8")));

                    out.close();
                } catch(FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    //split the String to String[] for use to split into multiple arrays
    //split the String to String[] to ensure alignment with the lyrics text box
    //(i.e. one lyrics tb under one score)
    private static String[] split(String string)
    {
        char[] chars = string.toCharArray();
        String[] strings = new String[chars.length];
        for (int i = 0; i < chars.length; i++)
        {
            strings[i] = String.valueOf(chars[i]);
        }
        return strings;
    }

    //split the String[] into multiple arrays to ensure that one array fits into one score text view
    //(i.e. one score line of size X is in textviewscores1)
    public static <T extends Object> List<T[]> splitArray(T[] array, int max){

        int x = array.length / max;
        int r = (array.length % max); // remainder

        int lower = 0;
        int upper = 0;

        List<T[]> list = new ArrayList<T[]>();

        int i=0;

        for(i=0; i<x; i++){

            upper += max;

            list.add(Arrays.copyOfRange(array, lower, upper));

            lower = upper;
        }

        if(r > 0){

            list.add(Arrays.copyOfRange(array, lower, (lower + r)));

        }

        return list;
    }

    //add table dynamically
    public void init(){
        TableLayout tbLayout = (TableLayout) findViewById(R.id.tableLayout);
        TableRow tbRow = new TableRow(this);
        TextView tvScores = new TextView(this);
        tbRow.addView(tvScores);
        TableRow tbRow1 = new TableRow(this);
        EditText etLyrics = new EditText(this);
        tbRow1.addView(etLyrics);
        //Button btSave = new Button(this);
    }

    //split the String to String[] for use to split into multiple arrays
    //split the String to String[] to ensure alignment with the lyrics text box
    //(i.e. one lyrics tb under one score)
    private static String[] split(String string)
    {
        char[] chars = string.toCharArray();
        String[] strings = new String[chars.length];
        for (int i = 0; i < chars.length; i++)
        {
            strings[i] = String.valueOf(chars[i]);
        }
        return strings;
    }

    //split the String[] into multiple arrays to ensure that one array fits into one score text view
    //(i.e. one score line of size X is in textviewscores1)
    public static <T extends Object> List<T[]> splitArray(T[] array, int max){

        int x = array.length / max;
        int r = (array.length % max); // remainder

        int lower = 0;
        int upper = 0;

        List<T[]> list = new ArrayList<T[]>();

        int i=0;

        for(i=0; i<x; i++){

            upper += max;

            list.add(Arrays.copyOfRange(array, lower, upper));

            lower = upper;
        }

        if(r > 0){

            list.add(Arrays.copyOfRange(array, lower, (lower + r)));

        }

        return list;
    }

    /*//add table dynamically
    public void init(){
        TableLayout tbLayout = (TableLayout) findViewById(R.id.tableLayout);
        TableRow tbRow = new TableRow(this);
        TextView tvScores = new TextView(this);
        tbRow.addView(tvScores);
        TableRow tbRow1 = new TableRow(this);
        EditText etLyrics = new EditText(this);
        tbRow1.addView(etLyrics);
        Button btSave = new Button(this);
        TableRow tbRow2 = new TableRow(this);
        tbRow2.addView(btSave);
    }*/

}