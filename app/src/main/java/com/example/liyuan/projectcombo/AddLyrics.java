package com.example.liyuan.projectcombo;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import android.widget.Toast;

public class AddLyrics extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener, NumberPicker.OnValueChangeListener, AdapterView.OnItemSelectedListener{

    Score score;
    double[] lengths;
    Metronome metronome;
    ScoreFile scoreFile;
    PlayBack playBackTrack;
    ImageButton btBack;
    int[] numericNotes;
    private AudioThread[] audioThreads = new AudioThread[14];
    final Note[] Notes = new Note[13];
    DisplayThread displayThread;

    Button b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,
            b21,b22,b23,b24,b25,b26,b27,b28,b29,b30,b31,b32,b33,b34,b35,b36,b37,b38,b39,b40,
            b41,b42,b43,b44,b45,b46,b47,b48,b49,b50,b51,b52,b53,b54,b55,b56,b57,b58;
    TextView scores;

    private final String underline = "<sub>\u0332</sub>";
    private final String double_underline = "<sub>\u0333</sub>";
    private final String curve = "<sup>\u0361</sup>";
    private final String bullet = "&#8226\n";
    private final String dot_above = "<sub>\u0307</sub>";
    private final String dot_below = "<sub>\u0323</sub>";
    private final String sharp = "&#9839;";

    private ListView mDrawerList2;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private MyAdapter myAdapter;
    private Toolbar toolbar;
    private NumberPicker metronumberpicker;

    String notesAndRest;
    String lengthOfNotesAndRest;
    DateFormat df;
    Date now;

    boolean withMetronome;
    boolean metronomeRunning;
    boolean isPlayBack;
    boolean onRecord;
    boolean resetScore;
    boolean onHold;
    boolean onRest;
    boolean isRunning;
    boolean opened;
    boolean isOpened;

    double startRecordTime;
    double stopRecordTime;
    double recordTime;
    double secondsPerBeat;
    double noteBeats;
    double noteStartTime;
    double noteEndTime;
    double rest;
    double restStartTime;
    double restEndTime;
    double elapse;
    double beatLength;

    int tempo;
    int timeSig;
    int lastNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_lyrics);

            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);

            mDrawerList2 = (ListView) findViewById(R.id.navigationList_left);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mActivityTitle = getTitle().toString();

            addDrawerItems2();
            setupDrawer();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            super.onCreate(savedInstanceState);

            LayoutInflater inflater = getLayoutInflater();

            View listHeaderView = inflater.inflate(R.layout.navigation_drawer_header, null, false);

            mDrawerList2.addHeaderView(listHeaderView);

            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            //retrieve scores (in String) from MainActivity
            Intent intent = getIntent();
            String rawScores = intent.getStringExtra("scores");
            Log.d("rawScores", "The raw scores: " + rawScores);

            scores = (TextView) findViewById(R.id.score);
            scores.setMovementMethod(new ScrollingMovementMethod());

            scores.setText(Html.fromHtml(rawScores) + "\u2225");

            scores.setInputType(InputType.TYPE_NULL);
            if (android.os.Build.VERSION.SDK_INT >= 11)
            {
                scores.setRawInputType(InputType.TYPE_CLASS_TEXT);
                scores.setTextIsSelectable(true);
            }

            scoreFile = new ScoreFile();
            numericNotes = null;
            lengths = null;
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;
            metronomeRunning = false;
            metronome = new Metronome(60);
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
            b52 = (Button) findViewById(R.id.button52);
            b53 = (Button) findViewById(R.id.button53);
            b54 = (Button) findViewById(R.id.button54);
            b55 = (Button) findViewById(R.id.button55);
            b56 = (Button) findViewById(R.id.button56);
            b57 = (Button) findViewById(R.id.button57);
            b58 = (Button) findViewById(R.id.button58);


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
            b52.setText(Html.fromHtml("1" + sharp));
            b53.setText(Html.fromHtml("2" + sharp));
            b54.setText(Html.fromHtml("3" + sharp));
            b55.setText(Html.fromHtml("4" + sharp));
            b56.setText(Html.fromHtml("5" + sharp));
            b57.setText(Html.fromHtml("6" + sharp));
            b58.setText(Html.fromHtml("7" + sharp));


            btBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    scores.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
//                    int index = getEditSelection();// The location of the cursor
//                    deleteEditValue(index);

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
            b52.setOnClickListener(this);
            b53.setOnClickListener(this);
            b54.setOnClickListener(this);
            b55.setOnClickListener(this);
            b56.setOnClickListener(this);
            b57.setOnClickListener(this);
            b58.setOnClickListener(this);

            metronumberpicker = (NumberPicker) findViewById(R.id.metroPicker);
            metronumberpicker.setFocusable(false);
            metronumberpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            metronumberpicker.setMaxValue(120);
            metronumberpicker.setMinValue(60);
            metronumberpicker.setWrapSelectorWheel(false);

            tempo = metronumberpicker.getValue();
            Spinner spinner = (Spinner) findViewById(R.id.withmetro);
// Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.withmetro_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            scoreFile = new ScoreFile();
            numericNotes = null;
            lengths = null;
            withMetronome = true;
            opened = false;
            beatLength = 1.0;



        }catch(NumberFormatException e){
            tempo = 60;
        }finally{
            secondsPerBeat = 60.0 / tempo;
        }


    }

    private void addDrawerItems2() {
//        String[] menuArray = getResources().getStringArray(R.array.navigation_toolbox);
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray);
//        mDrawerList2.setAdapter(mAdapter);
        myAdapter = new MyAdapter(this, "midterm@fyp.com", "Cambo");
        mDrawerList2.setAdapter(myAdapter);
        mDrawerList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        save();
                        break;
                    case 2:
                        editScore();
                        break;
                    case 3:
                        addLyrics();
                        break;
                    case 4:
                        openOrNew();
                        break;
                    case 5:
//                    exportToPDF();
                        break;
                }
                Toast.makeText(AddLyrics.this, "position is " + position + ", id is " + id + " view id is " + view.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Log@Main398", "position is " + position + " and id is " + id);
        switch (position) {
            case 1:
                metronome.setWithMetronome(true);
                break;
            case 2:
                metronome.setWithMetronome(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(scores.getWindowToken(), 0);

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
            case R.id.button52:
                t = "" + Html.fromHtml("1"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button53:
                t = "" + Html.fromHtml("2"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button54:
                t = "" + Html.fromHtml("3"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button55:
                t = "" + Html.fromHtml("4"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button56:
                t = "" + Html.fromHtml("5"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button57:
                t = "" + Html.fromHtml("6"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button58:
                t = "" + Html.fromHtml("7"+sharp);
                if (index <0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

        }

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
            Score thisScore = (Score) getIntent().getSerializableExtra("Score2");
            Log.d("Log@617", "Score is null? " + (thisScore == null));
            if (thisScore != null && thisScore.getScore() != null) {
                Log.d("Log@Main619", "Score is " + thisScore.getScore().length);
                scores.setText(extractScore(thisScore.getScore(), thisScore.getLengths()));
                score = thisScore;
                opened = true;
                isOpened = true;
            }
        } else {
            Log.e("onResumeLog@Main555", "Score is null");
        }
    }

    @Override
    protected void onDestroy() {
        for (AudioThread thread : audioThreads) {
            try {
                if (thread != null) {
                    thread.join();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            metronome.stop();
        } catch (Exception e) {

        }finally {
            super.onDestroy();
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        tempo = newVal;
        if (metronome != null)
            metronome.changeTempo(tempo);
        secondsPerBeat = 60 / tempo;
    }

    public void startMetronome(View view) {
        if (!metronomeRunning) {
            if (metronome != null) {
                metronome.setWithMetronome(withMetronome);
                metronome.start();
            } else {
                metronome = new Metronome(metronumberpicker.getValue());
                metronome.setWithMetronome(withMetronome);
                metronome.start();
            }
            metronomeRunning = true;
//            ImageButton imageButton = (ImageButton) findViewById(R.id.metronomeButton);
//            imageButton.setImageResource(R.drawable.stopmetro);
        } else {
            metronome.stop();
            metronomeRunning = false;
//            ImageButton imageButton = (ImageButton) findViewById(R.id.metronomeButton);
//            imageButton.setImageResource(R.drawable.metronome);
        }

    }

    public void playBack(View view) {
//// TODO: 10/18/2015 HashMap Key must be unique. . .
        //Log.d("PlayBack Log", "PlayBack Entered");
        //Log.d("PlayBack Log", "Length of notesAndRest is " + notesAndRest.length() + "\t" + "Length of lengths is " + lengthOfNotesAndRest.length());

        //notesAndRest = notesAndRest.trim();
        //lengthOfNotesAndRest = lengthOfNotesAndRest.trim();

        Log.d("PlayBack Log@601", "Length of split is" + notesAndRest.split(" ").length + " And Length is " + lengthOfNotesAndRest.split(" ").length);

        if (playBackTrack == null || playBackTrack.getState() == Thread.State.NEW || playBackTrack.getState() == Thread.State.TERMINATED) {
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


                //写一下吧write到audioTrack里面 stream

            /*            numericNotes = new int[4];
                        numericNotes[0] = 1;
                        numericNotes[1] = 3;
                        numericNotes[2] = 0;
                        numericNotes[3] = 5;

                        scoreLength = new String[4];
                        scoreLength[0] = "1.000";
                        scoreLength[1] = "1.000";
                        scoreLength[2] = "1.000";
                        scoreLength[3] = "1.000";*/


                playBackTrack = new PlayBack(numericNotes, lengths, lastNote);
                Log.d("PlayBack Log", "PlayBack initialised");
                playBackTrack.start();

                    /*try {
                        Log.i("Log@Main803", "imageButton id: " + R.id.playBack_Button + " " + R.drawable.playing);
                        playBack.join();
                        //imageButton.setImageResource(R.drawable.playbackbutton);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
            }
        }
    }

    private int[] prepareScore() {
        int[] array;
        if (!notesAndRest.isEmpty()) {
            String[] scoreNotes = notesAndRest.trim().split(" ");
            array = new int[scoreNotes.length];
            for (int i = 0; i < scoreNotes.length; i++) {
                array[i] = Integer.parseInt(scoreNotes[i]);
            }
        } else {
            array = new int[]{1, 3, 5};
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
            array = new double[]{1.0, 1.0, 1.0};
        }
        return array;
    }

    private String extractScore(int[] notes, double[] lengths) {
        String scoreString = "";
        if (notes != null && lengths != null && notes.length == lengths.length) {
            for (int i = 0; i < notes.length; i++) {
                String thisNote = Integer.toString(notes[i]);
                double thisLength = lengths[i];
                int q = (int) Math.round(thisLength / 0.25);
                if (q == 0) {
                    q = 1;
                }
                switch (q) {
                    case 1:
                        thisNote += double_underline;
                        break;
                    case 2:
                        thisNote += underline;
                        break;
                    case 3:
                        thisNote += underline;
                        thisNote += bullet;
                        break;
                    default:
                        break;
                }
                scoreString += thisNote;
            }
        }

        ////TODO:
        return Html.fromHtml(scoreString.trim()).toString();
    }

    public void openOrNew() {
        Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
    }

    private void save() {
        Intent intent = new Intent(this, SaveActivity.class);
        score = new Score();
        numericNotes = prepareScore();
        lengths = prepareLengths();
        Log.i("Log@Main805", "numericNotes is null? " + (numericNotes == null));
        Log.i("Log@Main806", "lengths is null?" + (lengths == null));
        score.setScore(numericNotes, lengths);
        intent.putExtra("Score", score);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
    }

    private void addLyrics() {
        Intent i = new Intent(AddLyrics.this,
                AddLyrics.class);
        //i.putExtra("scores", Html.fromHtml(displayThread.getDisplay() + "\u2225"));
        i.putExtra("scores", displayThread.getDisplay());
        startActivity(i);
        finish();
    }

    public void editScore() {
        Intent intent = new Intent(this, DisplayActivity.class);
        if (!isOpened) {
            intent.putExtra("notes", prepareScore());
            intent.putExtra("lengths", prepareLengths());
        } else {
            intent.putExtra("score", score);
        }
        startActivity(intent);
    }

    private void deleteAll() {
        Log.i("Log@Main835", "Delete Status is " + scoreFile.deleteAll());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (id) {
            case R.id.record_button_item:
                // User chose the "Settings" item, show the app settings UI...
//                Intent intent = new Intent(MainActivity.this, register.class);
//                startActivity(intent);
                record();
                break;
            case R.id.playBack:
                playBack(null);
                break;
            case R.id.pause:
                pausePlay(null);
                break;
            case R.id.stop:
                stopPlay(null);
                break;
            case R.id.metro:
                withMetronome = true;
                startMetronome(null);
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void pausePlay(View view) {
        if (playBackTrack != null) {
            playBackTrack.pausePlaying();
            lastNote = playBackTrack.getJ();
            if (lastNote == playBackTrack.getSize() - 1) {
                lastNote = playBackTrack.getLast();
            }
            try {
                playBackTrack.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("Log@Main431", "pausePlay clicked" + lastNote);
        }
    }

    public void stopPlay(View view) {
        if (playBackTrack != null) {
            playBackTrack.stopPlaying();
            lastNote = playBackTrack.getLast();
        }
        try {
            playBackTrack.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("Log@Main431", "pausePlay clicked" + lastNote);
    }

    private int getTimeSignature() {
        if (new Integer(timeSig) == null) {
            timeSig = 4;
        }
        return timeSig;
    }

    public void record() {
        if (!onRecord) {
            //recordButton.setImageResource(R.drawable.stopbutton);
            onRecord = true;
//                recordStatus.setText("Recording");
            resetScore = true;
            scores.setText(R.string.main_score);
            startRecordTime = System.currentTimeMillis() + 1000 * timeSig * secondsPerBeat;
            restStartTime = startRecordTime;
            now = new Date();
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;


            if (displayThread.getState() != Thread.State.NEW) {
                displayThread = new DisplayThread();
            }
            displayThread.setTimeSignature(getTimeSignature());
            displayThread.setTempo(tempo);
            startMetronome(null);
            long delay = (long)(timeSig * 1000 * secondsPerBeat);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            displayThread.start();
        } else {
            //recordButton.setImageResource(R.drawable.startbutton);
            onRecord = false;
//                recordStatus.setText("Click to start");
            //textView.setText((String)getText(R.string.main_score));
            resetScore = false;
            stopRecordTime = System.currentTimeMillis();
            recordTime = stopRecordTime - startRecordTime;
            restEndTime = stopRecordTime;
            rest = (restEndTime - restStartTime) / 1000 / secondsPerBeat;
            notesAndRest = notesAndRest + " " + "0";
            lengthOfNotesAndRest = lengthOfNotesAndRest + " " + rest;
            Log.d("RecordingLog", "The Record Time is " + recordTime);
//                textView.setText(df.format(now) + " " + notesAndRest + "\n" + df.format(now) + " " + lengthOfNotesAndRest);


            if (displayThread != null) {
                displayThread.stopThread();
//                Log.d("MainActivityDisplayLog", "The state of displayThread is " + displayThread.getState().toString());
//                    textView.setText(displayThread.getArchived());
                scores.setText(Html.fromHtml(displayThread.getDisplay() + "\u2225"));//ending pause in html
//                Log.d("MainActivityDisplayLog", "The archived is " + displayThread.getArchived());
            }
            if (metronome != null && metronomeRunning) {
                metronome.stop();
            }
        }
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

    class MyAdapter extends BaseAdapter {
        private String att_email;
        private String att_name;
        private Context context;
        String[] tool_list;
        int[] images = {R.drawable.save, R.drawable.edit, R.drawable.add, R.drawable.recordlists, R.drawable.share};

        public MyAdapter(Context context, String email, String name) {
            this.context = context;
            this.att_name = name;
            this.att_email = email;
            tool_list = context.getResources().getStringArray(R.array.navigation_toolbox);
        }

        @Override
        public int getCount() {

            return tool_list.length;
        }

        @Override
        public Object getItem(int i) {
            return tool_list[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View row = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
                row = inflater.inflate(R.layout.custom_row, viewGroup, false);
            } else {
                row = view;
            }
            TextView titleTextView2 = (TextView) row.findViewById(R.id.textView);
            ImageView titleImageView2 = (ImageView) row.findViewById(R.id.imageView);
            TextView t_name = (TextView) row.findViewById(R.id.nav_name);// Creating Text View object from header.xml for name
            if (t_name != null) {
                t_name.setText("Cambo");
//
            }
//        t_name.setText(att_name);
            TextView t_email = (TextView) row.findViewById(R.id.nav_email);       // Creating Text View object from header.xml for email
//        t_email.setText(att_email);
            titleTextView2.setText(tool_list[i]);
            titleImageView2.setImageResource(images[i]);
            return row;
        }
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
