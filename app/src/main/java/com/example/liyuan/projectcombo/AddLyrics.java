package com.example.liyuan.projectcombo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class AddLyrics extends ActionBarActivity implements OnClickListener, OnTouchListener{
    private boolean isEdit = false, isEdit1 = false;
    private String mUpper = "upper", mLower = "lower";
    private int w, mWindowWidth;
    Score score;
    String notesAndRest;
    String lengthOfNotesAndRest;
    double[] lengths;
    ScoreFile scoreFile;
    ImageButton btBack;
    int key;
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
    final int[] buttons = {R.id.button, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
            R.id.button10, R.id.button11, R.id.button12, R.id.button13,
            R.id.button14, R.id.button15, R.id.button16};
    Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,
            b21,b22,b23,b24,b25,b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36,b37,b38,b39,b40,
            b41,b42,b43,b44,b45,b46,b47,b48,b49,b50,b51;
    TextView scores;
    private final String underline = "<sub>\u0332</sub>";
    private final String double_underline = "<sub>\u0333</sub>";
    private final String curve = "<sup>\u0361</sup>";
    private final String bullet = "&#8226\n";
    private final String dot_above = "<sub>\u0307</sub>";
    private final String dot_below = "<sub>\u0323</sub>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            setContentView(R.layout.activity_add_lyrics);

            //retrieve scores (in String) from MainActivity
            Intent intent = getIntent();
            String rawScores = intent.getStringExtra("scores");
            Log.d("rawScores", "The raw scores: " + rawScores);

            scores = (TextView) findViewById(R.id.score);
            scores.setMovementMethod(new ScrollingMovementMethod());

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

            btBack = (ImageButton) findViewById(R.id.back);

            b1 = (Button) findViewById(R.id.button);
            b2 = (Button) findViewById(R.id.button2);
            b3 = (Button) findViewById(R.id.button3);
            b4 = (Button) findViewById(R.id.button4);
            b5 = (Button) findViewById(R.id.button5);
            b6 = (Button) findViewById(R.id.button6);
            b7 = (Button) findViewById(R.id.button7);
            b8 = (Button) findViewById(R.id.button8);
            b9 = (Button) findViewById(R.id.button9);
            b10 = (Button) findViewById(R.id.button10);
            b11 = (Button) findViewById(R.id.button11);
            b12 = (Button) findViewById(R.id.button12);
            b13 = (Button) findViewById(R.id.button13);
            b14 = (Button) findViewById(R.id.button14);
            b15 = (Button) findViewById(R.id.button15);
            b16 = (Button) findViewById(R.id.button16);
            b17 = (Button) findViewById(R.id.button17);
            b18 = (Button) findViewById(R.id.button18);
            b19 = (Button) findViewById(R.id.button19);
            b20 = (Button) findViewById(R.id.button20);
            b21 = (Button) findViewById(R.id.button21);
            b22 = (Button) findViewById(R.id.button22);
            b23 = (Button) findViewById(R.id.button23);
            b24 = (Button) findViewById(R.id.button24);
            b25 = (Button) findViewById(R.id.button25);
            b26 = (Button) findViewById(R.id.button26);
            b27 = (Button) findViewById(R.id.button27);
            b28 = (Button) findViewById(R.id.button28);
            b29 = (Button) findViewById(R.id.button29);
            b30 = (Button) findViewById(R.id.button30);
            b31 = (Button) findViewById(R.id.button31);
            b32 = (Button) findViewById(R.id.button32);
            b33 = (Button) findViewById(R.id.button33);
            b34 = (Button) findViewById(R.id.button34);
            b35 = (Button) findViewById(R.id.button35);
            b36 = (Button) findViewById(R.id.button36);
            b37 = (Button) findViewById(R.id.button37);
            b38 = (Button) findViewById(R.id.button38);
            b39 = (Button) findViewById(R.id.button39);
            b40 = (Button) findViewById(R.id.button40);
            b41 = (Button) findViewById(R.id.button41);
            b42 = (Button) findViewById(R.id.button42);
            b43 = (Button) findViewById(R.id.button43);
            b44 = (Button) findViewById(R.id.button44);
            b45 = (Button) findViewById(R.id.button45);
            b46 = (Button) findViewById(R.id.button46);
            b47 = (Button) findViewById(R.id.button47);
            b48 = (Button) findViewById(R.id.button48);
            b49 = (Button) findViewById(R.id.button49);
            b50 = (Button) findViewById(R.id.button50);
            b51 = (Button) findViewById(R.id.button51);


            b1.setText(Html.fromHtml("1" + underline));
            b2.setText(Html.fromHtml("1" + double_underline));
            b3.setText(Html.fromHtml("1" + curve));
            b4.setText(Html.fromHtml("1" + bullet));
            b5.setText(Html.fromHtml("1" + dot_above));
            b6.setText(Html.fromHtml("1" + dot_below));
            b7.setText(Html.fromHtml("2" + underline));
            b8.setText(Html.fromHtml("2" + double_underline));
            b9.setText(Html.fromHtml("2" + curve));
            b10.setText(Html.fromHtml("2" + bullet));
            b11.setText(Html.fromHtml("2" + dot_above));
            b12.setText(Html.fromHtml("2" + dot_below));
            b13.setText(Html.fromHtml("3" + underline));
            b14.setText(Html.fromHtml("3" + double_underline));
            b15.setText(Html.fromHtml("3" + curve));
            b16.setText(Html.fromHtml("3" + bullet));
            b17.setText(Html.fromHtml("3" + dot_above));
            b18.setText(Html.fromHtml("3" + dot_below));
            b19.setText(Html.fromHtml("4" + underline));
            b20.setText(Html.fromHtml("4" + double_underline));
            b21.setText(Html.fromHtml("4" + curve));
            b22.setText(Html.fromHtml("4" + bullet));
            b23.setText(Html.fromHtml("4" + dot_above));
            b24.setText(Html.fromHtml("4" + dot_below));
            b25.setText(Html.fromHtml("5" + underline));
            b26.setText(Html.fromHtml("5" + double_underline));
            b27.setText(Html.fromHtml("5" + curve));
            b28.setText(Html.fromHtml("5" + bullet));
            b29.setText(Html.fromHtml("5" + dot_above));
            b30.setText(Html.fromHtml("5" + dot_below));
            b31.setText(Html.fromHtml("6" + underline));
            b32.setText(Html.fromHtml("6" + double_underline));
            b33.setText(Html.fromHtml("6" + curve));
            b34.setText(Html.fromHtml("6" + bullet));
            b35.setText(Html.fromHtml("6" + dot_above));
            b36.setText(Html.fromHtml("6" + dot_below));
            b37.setText(Html.fromHtml("7" + underline));
            b38.setText(Html.fromHtml("7" + double_underline));
            b39.setText(Html.fromHtml("7" + curve));
            b40.setText(Html.fromHtml("7" + bullet));
            b41.setText(Html.fromHtml("7" + dot_above));
            b42.setText(Html.fromHtml("7" + dot_below));
            b43.setText("1");
            b44.setText("2");
            b45.setText("3");
            b46.setText("4");
            b47.setText("5");
            b48.setText("6");
            b49.setText("7");
            b50.setText("0");
            b51.setText("-");


            btBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    scores.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    int index = getEditSelection();// The location of the cursor
                    deleteEditValue(index);
                }
            });


            b1.setOnClickListener(this);
            b2.setOnClickListener(this);
            b3.setOnClickListener(this);
            b4.setOnClickListener(this);
            b5.setOnClickListener(this);
            b6.setOnClickListener(this);
            b7.setOnClickListener(this);
            b8.setOnClickListener(this);
            b9.setOnClickListener(this);
            b10.setOnClickListener(this);
            b11.setOnClickListener(this);
            b12.setOnClickListener(this);
            b13.setOnClickListener(this);
            b14.setOnClickListener(this);
            b15.setOnClickListener(this);
            b16.setOnClickListener(this);
            b17.setOnClickListener(this);
            b18.setOnClickListener(this);
            b19.setOnClickListener(this);
            b20.setOnClickListener(this);
            b21.setOnClickListener(this);
            b22.setOnClickListener(this);
            b23.setOnClickListener(this);
            b24.setOnClickListener(this);
            b25.setOnClickListener(this);
            b26.setOnClickListener(this);
            b27.setOnClickListener(this);
            b28.setOnClickListener(this);
            b29.setOnClickListener(this);
            b30.setOnClickListener(this);
            b31.setOnClickListener(this);
            b32.setOnClickListener(this);
            b33.setOnClickListener(this);
            b34.setOnClickListener(this);
            b35.setOnClickListener(this);
            b36.setOnClickListener(this);
            b37.setOnClickListener(this);
            b38.setOnClickListener(this);
            b39.setOnClickListener(this);
            b40.setOnClickListener(this);
            b41.setOnClickListener(this);
            b42.setOnClickListener(this);
            b43.setOnClickListener(this);
            b44.setOnClickListener(this);
            b45.setOnClickListener(this);
            b46.setOnClickListener(this);
            b47.setOnClickListener(this);
            b48.setOnClickListener(this);
            b49.setOnClickListener(this);
            b50.setOnClickListener(this);
            b51.setOnClickListener(this);
        }catch(NumberFormatException e){
            timeSignature = 60;
            timeSignatureButton.setText("60");
        }finally{
            secondsPerBeat = 60.0 / timeSignature;
        }


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == scores) {
            hideDefaultKeyboard();
        }

        return true;

//        key = noteID;
//        key = noteID * noteAddOn;
//        if (displayThread != null && displayThread.getState() != Thread.State.TERMINATED) {
//            displayThread.update(key);
//        }
    }

    //hide the default keyboard
    private void hideDefaultKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    // Delete the character at a specified position
    public void deleteEditValue(int index) {
        scores.getEditableText().delete(index - 1, index);
    }
    // Gets the current position of the cursor
    public int getEditSelection() {
        return scores.getSelectionStart();
    }

    // Access to the contents of the text box
    public String getEditTextViewString() {
        return scores.getText().toString();
    }

    @Override
    public void onClick(View v) {
        String t = "";
        int index = getEditSelection();
        switch(v.getId()){
            case R.id.button51:
                t = "-";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button50:
                t = "0";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button:
                t = ""+Html.fromHtml("1"+underline);
                // int index = getEditSelection();// The location of the cursor
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button2:
                t = ""+Html.fromHtml("1"+double_underline);
                //int index = getEditSelection();// The location of the cursor
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button3:
                t = ""+Html.fromHtml("1"+curve);
                //int index = getEditSelection();// The location of the cursor
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button4:
                t = ""+Html.fromHtml("1"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button5:
                t = ""+Html.fromHtml("1"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button6:
                t = ""+Html.fromHtml("1"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button7:
                t = ""+Html.fromHtml("2"+underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button8:
                t = ""+Html.fromHtml("2"+double_underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button9:
                t = ""+Html.fromHtml("2"+curve);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button10:
                t = ""+Html.fromHtml("2"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button11:
                t = ""+Html.fromHtml("2"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button12:
                t = ""+Html.fromHtml("2"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button13:
                t = ""+Html.fromHtml("3"+underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button14:
                t = ""+Html.fromHtml("3"+double_underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button15:
                t = ""+Html.fromHtml("3"+curve);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button16:
                t = ""+Html.fromHtml("3"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button17:
                t = ""+Html.fromHtml("3"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button18:
                t = ""+Html.fromHtml("3"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button19:
                t = ""+Html.fromHtml("4"+underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button20:
                t = ""+Html.fromHtml("4"+double_underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button21:
                t = ""+Html.fromHtml("4"+curve);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button22:
                t = ""+Html.fromHtml("4"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button23:
                t = ""+Html.fromHtml("4"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button24:
                t = ""+Html.fromHtml("4"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button25:
                t = ""+Html.fromHtml("5"+underline);
                // int index = getEditSelection();// The location of the cursor
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button26:
                t = ""+Html.fromHtml("5"+double_underline);
                //int index = getEditSelection();// The location of the cursor
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button27:
                t = ""+Html.fromHtml("5"+curve);
                //int index = getEditSelection();// The location of the cursor
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button28:
                t = ""+Html.fromHtml("5"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button29:
                t = ""+Html.fromHtml("5"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button30:
                t = ""+Html.fromHtml("5"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button31:
                t = ""+Html.fromHtml("6"+underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button32:
                t = ""+Html.fromHtml("6"+double_underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button33:
                t = ""+Html.fromHtml("6"+curve);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button34:
                t = ""+Html.fromHtml("6"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button35:
                t = ""+Html.fromHtml("6"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button36:
                t = ""+Html.fromHtml("5"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button37:
                t = ""+Html.fromHtml("7"+underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button38:
                t = ""+Html.fromHtml("7"+double_underline);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button39:
                t = ""+Html.fromHtml("7"+curve);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button40:
                t = ""+Html.fromHtml("7"+bullet);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button41:
                t = ""+Html.fromHtml("7"+dot_above);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button42:
                t = ""+Html.fromHtml("7"+dot_below);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button43:
                t = "1";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button44:
                t = "2";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button45:
                t = "3";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button46:
                t = "4";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button47:
                t = "5";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button48:
                t = "6";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button49:
                t = "7";
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
        }

    }

    private String extractScore(int[] notes, double[] lengths) {
        String scoreString = "Score2";
        if (notes != null && lengths!= null && notes.length == lengths.length) {
            for (int i = 0; i < notes.length; i++) {
                String thisNote = notes[i] + " ";
                scoreString += thisNote;
            }
        }
        return scoreString;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getSerializableExtra("Score2") != null) {
            //open(getIntent());
            Score thisScore = (Score)getIntent().getSerializableExtra("Score2");
            Log.d("Log@617", "Score is null? " + (thisScore == null));
            if (thisScore != null && thisScore.getScore() != null) {
                Log.d("Log@Main619", "Score is " + thisScore.getScore().length);
                //textView.setText(extractScore(thisScore.getScore(), thisScore.getLengths()));
                scores.setText(extractScore(thisScore.getScore(), thisScore.getLengths()));
            }

            score = thisScore;
            opened = true;
        } else {
            Log.e("onResumeLog@Main789", "Score is null");
        }
    }

    @Override
    protected void onDestroy() {
        for (AudioThread thread : audioThreads) {
            try {
                if(thread != null) {
                    thread.join();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        metronome.stop();
        super.onDestroy();
    }

    public void startMetronome(View view) {
        if (!metronomeRunning) {
            if (metronome != null) {
                metronome.start();
            } else {
                metronome = new Metronome();
                metronome.start();
            }
            metronomeRunning = true;
            ImageButton imageButton = (ImageButton) findViewById(R.id.metronomeButton);
            imageButton.setImageResource(R.drawable.stopmetro);
        } else {
            metronome.stop();
            metronomeRunning = false;
            ImageButton imageButton = (ImageButton) findViewById(R.id.metronomeButton);
            imageButton.setImageResource(R.drawable.metronome);
        }

    }

    public void playBack(View view) {

        Log.d("PlayBack Log@601", "Length of split is" + notesAndRest.split(" ").length + " And Length is " + lengthOfNotesAndRest.split(" ").length);

        for (int i = 0; i < notesAndRest.split(" ").length; i++) {
            Log.d("PlayBack Log", "The Notes or Rest is " + notesAndRest.split(" ")[i]);
        }
        //// TODO: 10/18/2015 Trim the two Strings before split
        for (int i = 0; i < lengthOfNotesAndRest.split(" ").length; i++) {
            Log.d("PlayBack Log", "The Notes or Rest is " + lengthOfNotesAndRest.split(" ")[i]);
        }

        numericNotes = prepareScore();
        lengths = prepareLengths();

        Log.i("Log@Main735", "score is null? " + (score == null));
        if (opened) {
            numericNotes = score.getScore();
            lengths = score.getLengths();
            opened = false;
        }

        if (numericNotes != null && lengths != null) {
            Log.d("Log@Main705", " " + numericNotes.length + " " + lengths.length);
        }
        if (numericNotes.length == lengths.length) {

<<<<<<< HEAD
            PlayBack playBack = new PlayBack(numericNotes, lengths, 0);
=======
            PlayBack playBack = new PlayBack(numericNotes, lengths,0);
>>>>>>> TJTJNEW_branch
            Log.d("PlayBack Log", "PlayBack initialised");
            playBack.start();

            try {
                //Log.i("Log@Main803", "imageButton id: " + R.id.playBack_Button + " " + R.drawable.playing);
                playBack.join();
                //imageButton.setImageResource(R.drawable.playbackbutton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int[] prepareScore(){
        int [] array;
        if (!notesAndRest.isEmpty()) {
            String[] scoreNotes = notesAndRest.trim().split(" ");
            array = new int[scoreNotes.length];
            for (int i = 0; i < scoreNotes.length; i++) {
                array[i] = Integer.parseInt(scoreNotes[i]);
            }
        } else {
            array = new int[] {1, 3, 5};
        }
        return array;
    }

    private double[] prepareLengths() {
        double[] array;
        if (!lengthOfNotesAndRest.isEmpty()) {
            String[] scoreLength = lengthOfNotesAndRest.trim().split(" ");
            array = new double[scoreLength.length];
            for (int i = 0; i < scoreLength.length; i++) {
                array[i] = Double.parseDouble(scoreLength[i]);
            }
        } else {
            array = new double[] {1.0, 1.0, 1.0};
        }
        return array;
    }

    public void openOrNew() {
        Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("ScoreFile2", scoreFile);
        startActivity(intent);
    }

    private void save() {
        Intent intent = new Intent(this, EditSaveActivity.class);
        score = new Score();
        numericNotes = prepareScore();
        lengths = prepareLengths();
        Log.i("Log@Main805", "numericNotes is null? " + (numericNotes == null));
        Log.i("Log@Main806", "lengths is null?" + (lengths == null));
        score.setScore(numericNotes, lengths);
        intent.putExtra("Score2", score);
        intent.putExtra("ScoreFile2", scoreFile);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings){

        }else if (id == R.id.saveSong){
            save();
        }else if (id == R.id.openHistory){
            openOrNew();

        }
        return super.onOptionsItemSelected(item);
    }

    //split the String to String[] for use to split into multiple arrays
    //split the String to String[] to ensure alignment with the lyrics text box
    //(i.e. one lyrics tb under one score)
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

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

}
