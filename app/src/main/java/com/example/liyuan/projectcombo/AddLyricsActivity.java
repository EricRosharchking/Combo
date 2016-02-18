package com.example.liyuan.projectcombo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.liyuan.projectcombo.R;

public class AddLyricsActivity extends ActionBarActivity {

    TextView scores;
    EditText lyrics;
    Button save;
    Score score;
    String notesAndRest;
    String lengthOfNotesAndRest;
    double[] lengths;
    ScoreFile scoreFile;
    int[] numericNotes;
    Metronome metronome;
    boolean metronomeRunning;
    Button timeSignatureButton;
    int timeSignature;
    double secondsPerBeat;
    private AudioThread[] audioThreads = new AudioThread[14];
    final Note[] Notes = new Note[13];
    boolean opened;
    DisplayThread displayThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);

            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            setContentView(R.layout.activity_add_lyrics2);

            Intent intent = getIntent();
            String rawScores = intent.getStringExtra("scoresForAddLyrics");
            Log.d("rawScores", "The raw scores: " + rawScores);

            scores = (TextView) findViewById(R.id.tvScores);
            lyrics = (EditText) findViewById(R.id.edLyrics);

            scores.setMovementMethod(new ScrollingMovementMethod());
            lyrics.setMovementMethod(new ScrollingMovementMethod());

            scores.setTextSize(18);

            scores.setText(Html.fromHtml(rawScores) + "\u2225");

            scoreFile = new ScoreFile();
            numericNotes = null;
            lengths = null;
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;
            metronomeRunning = false;
            metronome = new Metronome();
            opened = false;

            for (int i = 1; i < audioThreads.length; i++) {
                audioThreads[i] = new AudioThread(Notes[i - 1]);
            }

            displayThread = new DisplayThread();


            //TO DO: Implement save, update metro and playback codes

        }catch(NumberFormatException e){

        }finally{
            secondsPerBeat = 60.0 / timeSignature;
        }




    }

}
