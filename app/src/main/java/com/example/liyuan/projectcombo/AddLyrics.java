package com.example.liyuan.projectcombo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddLyrics extends ActionBarActivity {

    TextView tvScores;
    EditText etLyrics;
    Button btnSaveLyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lyrics);

        tvScores = (TextView) findViewById(R.id.textViewScores);
        etLyrics = (EditText) findViewById(R.id.editTextLyrics);
        btnSaveLyrics = (Button) findViewById(R.id.buttonSaveLyrics);

        Intent intent = getIntent();

        String scores = intent.getStringExtra("scores");
        tvScores.setText(scores);

    }

}
