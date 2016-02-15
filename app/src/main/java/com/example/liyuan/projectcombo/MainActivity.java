package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liyuan.projectcombo.helper.SQLiteHandler;
import com.example.liyuan.projectcombo.helper.SessionManager;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener {

    private SQLiteHandler db;
    private SessionManager session;
    PlayBack playBackTrack;
    DisplayThread displayThread;
    Metronome metronome;
    ScoreFile scoreFile;
    Score score;

    private AudioThread[] audioThreads = new AudioThread[14];
    final int[] keys = {R.id.cnatural, R.id.csharp, R.id.dnatural, R.id.eflat, R.id.enatural,
            R.id.fnatural, R.id.fsharp, R.id.gnatural, R.id.gsharp, R.id.anatural, R.id.bflat,
            R.id.bnatural, R.id.highcnatural};
    final String[] notes = {"C", "Cs", "D", "Eb", "E", "F", "Fs", "G", "Gs", "A", "Bb", "B", "HighC"};
    final int[] numNotes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    final Note[] Notes = new Note[13];
    int[] numericNotes;
    double[] lengths;

    //Button pausePlay;
    //Button stopPlay;
    Button buttonAddLyrics;
//    Button buttonBack;
    Button btnLogout;
    Button tempoButton;
    Button timeSignatureButton;
    RadioButton radioButton;
    ImageButton UpperOctave;
    ImageButton LowerOctave;
    ImageButton keyboard;
    ImageButton recordButton;
    TextView textView;
//    TextView recordStatus;

    private HashMap<Integer, Integer> keyNoteMap;
    //// TODO: 10/4/2015 Use background of button, instead of imagebutton.
    //// TODO: 10/4/2015 Hashmap, int id as key, string name as value.

    String notesAndRest;
    String lengthOfNotesAndRest;
    DateFormat df;
    Date now;

    boolean metronomeRunning;
    boolean isPlayBack;
    boolean onRecord;
    boolean resetScore;
    boolean onHold;
    boolean onRest;
    boolean isRunning;
    boolean opened;

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

    int timeSignature;
    int noteID;
    int tempo;
    int octavefordisplay = 4;
    int lastNote;
    int key;
    int progress;

    private ListView mDrawerList2;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mDrawerList2 = (ListView)findViewById(R.id.navigationList_left);
            mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
            mActivityTitle = getTitle().toString();

            addDrawerItems2();
            setupDrawer();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            super.onCreate(savedInstanceState);

            LayoutInflater inflater = getLayoutInflater();

            View listHeaderView = inflater.inflate(R.layout.navigation_drawer_header,null,false);

            mDrawerList2.addHeaderView(listHeaderView);

            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                Log.d("ActionBar Log", "ActionBar is not Null");
                actionBar.hide();
            } else {
                Log.d("ActionBar Log", "ActionBar is Null");
            }


//            setContentView(R.layout.activity_main);
            UpperOctave = (ImageButton) findViewById(R.id.upoctave);
            LowerOctave = (ImageButton) findViewById(R.id.loweroctave);
            UpperOctave.setOnClickListener(this);
            LowerOctave.setOnClickListener(this);

            for (int i : keys) {
                keyboard = (ImageButton) findViewById(i);
                keyboard.setOnTouchListener(this);
                keyboard.setOnClickListener(this);
            }

            keyNoteMap = new HashMap<Integer, Integer>();
            for (int i = 0; i < keys.length; i++) {
                keyNoteMap.put(keys[i], numNotes[i]);
                Log.d("KeyNoteMap Log", "The Key is " + keys[i]);
                Log.d("KeyNoteMap Log", "The Note is " + numNotes[i]);
            }

            Log.d("HashMap Log", "The size of KeyNoteMap is " + keyNoteMap.size());
//            TextView keyBoardOctave = (TextView) findViewById(R.id.keyboardOctave);
//            String keyboardNameDisplay = "";
            for (int i = 0; i < Notes.length; i++) {
                Notes[i] = new Note(i + 1);
                Log.d("Notes Log", "The note is " + Notes[i].toString());
//                if (i == 0) {
//                    keyboardNameDisplay = Notes[i].toString();
//                } else {
//                    keyboardNameDisplay = keyboardNameDisplay + " " + Notes[i].toString();
//                    Log.d("Notes Log", "The note is " + Notes[i].toString());
//                    keyBoardOctave.setText(keyboardNameDisplay);
//                }
            }

            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            String buffer = null;
            String rate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                buffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
                rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            }

            Log.d("AudioManager Log", "System Output Sample Rate is " + rate);
            Log.d("AudioManager Log", "System Output Frames per Buffer is " + buffer);
            try {
                Method method = audioManager.getClass().getMethod("getOutputLatency", int.class);
                Log.d("AudioManager Log", "System Latency is " + method.invoke(audioManager, AudioManager.STREAM_MUSIC));
            } catch (Exception e) {

            }

            isRunning = true;

            for (int i = 1; i < audioThreads.length; i++) {
                audioThreads[i] = new AudioThread(Notes[i - 1]);
            }

            textView = (TextView) findViewById(R.id.main_score);
            textView.setMovementMethod(new ScrollingMovementMethod());
//            recordStatus = (TextView) findViewById(R.id.record_status);
            onRecord = false;
            recordButton = (ImageButton) findViewById(R.id.record_button);
            recordButton.setOnClickListener(this);
            resetScore = false;
            onHold = false;
            onRest = true;
//            tempoButton = (Button) findViewById(R.id.tempo);
//            timeSignatureButton = (Button) findViewById(R.id.time_signature);
            timeSignature = Integer.parseInt((String) getText(R.string.time_signature));
            Log.d("TimeSignatureLog", "" + timeSignature);
            df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            now = new Date();
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;
            metronomeRunning = false;
            metronome = new Metronome();

            tempo = 4;
            noteID = 1;

            displayThread = new DisplayThread();

//            SeekBar tempoSeekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
//            tempoSeekBar.setOnSeekBarChangeListener(this);
//            buttonBack = (Button) findViewById(R.id.buttonBack);
//            buttonAddLyrics = (Button) findViewById(R.id.buttonAddLyrics);
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logout();
                }
            });
//
//            buttonBack.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View view) {
//                    Intent i = new Intent(getApplicationContext(),
//                            UserMainPage.class);
//                    startActivity(i);
//                    finish();
//                }
//            });

//            buttonAddLyrics.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    addLyrics();
//                }
//            });

//Number Picker
            NumberPicker metronumberpicker = (NumberPicker)findViewById(R.id.metroPicker);
            metronumberpicker.setMaxValue(120);
            metronumberpicker.setMinValue(60);
            metronumberpicker.setWrapSelectorWheel(false);

            scoreFile = new ScoreFile();
            numericNotes = null;
            lengths = null;

            opened = false;

        } catch (NumberFormatException e) {
            timeSignature = 60;
            timeSignatureButton.setText("60");
        } finally {
            secondsPerBeat = 60.0 / timeSignature;
        }
    }

    private void addDrawerItems2() {
//        String[] menuArray = getResources().getStringArray(R.array.navigation_toolbox);
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuArray);
//        mDrawerList2.setAdapter(mAdapter);
        myAdapter = new MyAdapter(this,"midterm@fyp.com","Cambo");
        mDrawerList2.setAdapter(myAdapter);
        mDrawerList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "hello its me", Toast.LENGTH_SHORT).show();
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        // Activate the navigation drawer toggle
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    /**
     * Directing the user to add lyrics page.
     * AddLyrics will share the scores user entered from MainActivity
     **/
    private void addLyrics() {
        Intent i = new Intent(MainActivity.this,
                AddLyrics.class);
        //i.putExtra("scores", Html.fromHtml(displayThread.getDisplay() + "\u2225"));
        i.putExtra("scores", displayThread.getDisplay());
        startActivity(i);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //old version
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//
//        } else if (id == R.id.saveSong) {
//            save();
//        } else if (id == R.id.openHistory) {
//            openOrNew();
//        } else if (id == R.id.addLyrics){
//            addLyrics();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public void onClick(View v) {
//        Log.d("ButtonLog", "the button you clicked is " + v.getId());
//        TextView keyBoardOctave1 = (TextView) findViewById(R.id.keyboardOctave);
        String keyboardNameDisplay = "";


        if (audioThreads != null) {
            if (v.getId() == R.id.upoctave) {
                upOctave();
            } else if (v.getId() == R.id.loweroctave) {
                lowerOctave();
            }
        }

        if (v.getId() == recordButton.getId()) {
//            Log.d("ButtonLog","This is the record button");
//            Log.d("Log","OnRecord is " + onRecord);
            if (!onRecord) {
                recordButton.setImageResource(R.drawable.stopbutton);
                onRecord = true;
//                recordStatus.setText("Recording");
                resetScore = true;
                textView.setText(R.string.main_score);
                startRecordTime = System.currentTimeMillis();
                restStartTime = startRecordTime;
                now = new Date();
                notesAndRest = "";
                lengthOfNotesAndRest = notesAndRest;


                if (displayThread.getState() != Thread.State.NEW) {
                    displayThread = new DisplayThread();
                }
                displayThread.setTimeSignature(getTempo());
                displayThread.start();
            } else {
                recordButton.setImageResource(R.drawable.startbutton);
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
                    Log.d("MainActivityDisplayLog", "The state of displayThread is " + displayThread.getState().toString());
//                    textView.setText(displayThread.getArchived());
                    textView.setText(Html.fromHtml(displayThread.getDisplay() + "\u2225"));//ending pause in html
                    Log.d("MainActivityDisplayLog", "The archived is " + displayThread.getArchived());
                }
            }
        }

        if (v instanceof ImageButton) {
//            Log.d("what", String.valueOf(textView.getText()) + "* compared to *"+ getText(R.string.main_score));

            //Play Part
//            if (audioThreads[noteID].getState() == Thread.State.NEW) {
//                audioThreads[noteID].start();
//                Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
//            } else {
//                audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
//                Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
//                audioThreads[noteID].start();
//            }
            //Play Part End

            if (onRecord) {
                if (resetScore) {
                    textView.setText("");
                    resetScore = false;
                }
                Log.d("KeyNoteMap Log", "KeyNoteMap State is " + (keyNoteMap == null));
                Log.d("KeyNoteMap Log", "KeyNoteMap.get State is " + keyNoteMap.containsKey(v.getId()));
                        /*if (keyNoteMap != null && keyNoteMap.get(v.getId()) != null) {
                            textView.append("0 " + keyNoteMap.get(v.getId()) + " ");
                        }*/
            }

        } else {
            Log.d("Log", "This is not a button you clicked");
        }


/*
        if(v.getId() == R.id.time_signature) {

        }*/
    }


    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.tempo3:
                if (checked)
                    tempo = 3;
                break;
            case R.id.tempo4:
                if (checked)
                    tempo = 4;
                break;
        }
        if (metronome != null) {
            metronome.changeTimeSignature(tempo);
        } else {
            metronome = new Metronome();
            metronome.changeTimeSignature(tempo);
        }
        /*
        if (displayThread != null) {
            displayThread.setTimeSignature(tempo);
        } else {
            displayThread = new DisplayThread();
            displayThread.setTimeSignature(tempo);
        }*/
    }

    int noteAddOn;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        noteAddOn = 1;
        if (v instanceof ImageButton) {

            switch (octavefordisplay) {
                case 3:
                    noteAddOn = -1;
                    break;
                case 5:
                    noteAddOn = 14;
                    break;
                default:
                    noteAddOn = 1;
                    break;
            }

            switch (v.getId()) {
                case R.id.cnatural:
                    noteID = 1;
                    break;
                case R.id.csharp:
                    noteID = 2;
                    break;
                case R.id.dnatural:
                    noteID = 3;
                    break;
                case R.id.eflat:
                    noteID = 4;
                    break;
                case R.id.enatural:
                    noteID = 5;
                    break;
                case R.id.fnatural:
                    noteID = 6;
                    break;
                case R.id.fsharp:
                    noteID = 7;
                    break;
                case R.id.gnatural:
                    noteID = 8;
                    break;
                case R.id.gsharp:
                    noteID = 9;
                    break;
                case R.id.anatural:
                    noteID = 10;
                    break;
                case R.id.bflat:
                    noteID = 11;
                    break;
                case R.id.bnatural:
                    noteID = 12;
                    break;
                case R.id.highcnatural:
                    noteID = 13;
                    break;
                default:
                    noteID = 13;
                    break;
            }

//            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                Log.d("Log@Main485", "Action_Outside");
//                swipable = true;
//                audioThreads[noteID].stopPlaying();
//            }

//            if (event.getAction() == MotionEvent.ACTION_MOVE && swipable == true) {
//
//                //Play and Move
//                swipable = false;
//                audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
//                Log.d("Log@MainActivity490", "AudioThread State is not NEW: " + audioThreads[noteID].getState().toString());
//                audioThreads[noteID].start();
//            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {                                     //just press the key

                Log.d("Log@Main484", noteID + "AudioThread State is " + audioThreads[noteID].getState().toString());
                //Play Part Start
//                if (audioThreads[noteID].getState() != Thread.State.NEW) {
                    audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
                    Log.d("Log@MainActivity490", "AudioThread State is not NEW: " + audioThreads[noteID].getState().toString());
                    audioThreads[noteID].start();
//                } else {
//                    Log.d("Log@MainActivity487", "AudioThread State is: " + audioThreads[noteID].getState().toString());
//                    audioThreads[noteID].start();
//                }

                //Play Part End

                onHold = true;
                if (onRecord == true) {


                    key = noteID;
                    key = noteID * noteAddOn;
                    if (displayThread != null && displayThread.getState() != Thread.State.TERMINATED) {
                        displayThread.update(key);
                    }

                    if (Notes[noteID - 1] != null) {
                        Log.d("Note Log", "The frequency of the note is " + Notes[noteID - 1].toString());
                    }

                    noteStartTime = System.currentTimeMillis();
                    restEndTime = noteStartTime;                                                    //start count the time
                    rest = (restEndTime - restStartTime) / 1000 / secondsPerBeat;
                    Log.d("RestLog", "Rest is " + rest + " beats long");
                    notesAndRest = notesAndRest + " " + "0";
                    lengthOfNotesAndRest = lengthOfNotesAndRest + " " + rest;
                } else {
                    //                                                                              //play the sound only

                    if (Notes[noteID - 1] != null) {
                        Log.d("Note Log", "The frequency of the note is " + Notes[noteID - 1].toString());
                    }
                    //Play Part Start
//                    if (audioThreads[noteID].getState() == Thread.State.NEW) {
//                        audioThreads[noteID].start();
//                        Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
//                    } else {
//                        audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
//                        Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
//                        audioThreads[noteID].start();
//                    }
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {

                audioThreads[noteID].stopPlaying();
//                if (audioThreads[noteID] != null) {
//                    audioThreads[noteID] = null;
//                }
                if (onRecord == true) {

                    key = 0;
                    if (displayThread != null && displayThread.getState() != Thread.State.TERMINATED) {
                        displayThread.update(key);
                    }

//                    audioThreads[noteID].stopPlaying();

                    onHold = false;
                    noteEndTime = System.currentTimeMillis();
                    restStartTime = noteEndTime;
                    elapse = noteEndTime - noteStartTime;
                    elapse /= 1000;
                    //Log.d("TouchLog", "The elapse is " + elapse);
                    noteBeats = elapse / secondsPerBeat;
                    Log.d("BeatsLog", "There are " + noteBeats + " beats in the note");
                    //notesAndRest = notesAndRest + " " + keyNoteMap.get((v).getId());
                    notesAndRest = notesAndRest + " " + keyNoteMap.get((v).getId()) * noteAddOn;
                    lengthOfNotesAndRest = lengthOfNotesAndRest + " " + noteBeats;
                }
            }
        }

        Log.d("The score should be", notesAndRest);
        Log.d("The length of it is", lengthOfNotesAndRest);
//        textView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );//中间加横线
//        textView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );//底部加横线
        textView.setText(Html.fromHtml(displayThread.getDisplay() + "\u2225"));//ending pause
//        textView.setText(displayThread.getDisplay());
        return super.onTouchEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getSerializableExtra("Score") != null) {
            //open(getIntent());
            Score thisScore = (Score)getIntent().getSerializableExtra("Score");
            Log.d("Log@617", "Score is null? " + (thisScore == null));
            if (thisScore != null && thisScore.getScore() != null) {
                Log.d("Log@Main619", "Score is " + thisScore.getScore().length);
                textView.setText(extractScore(thisScore.getScore(), thisScore.getLengths()));
            }

            score = thisScore;
            opened = true;
        } else {
            Log.e("onResumeLog@Main555", "Score is null");
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


    private int[] prepareScore() {
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


    private String extractScore(int[] notes, double[] lengths) {
        String scoreString = "Score";
        if (notes != null && lengths!= null && notes.length == lengths.length) {
            for (int i = 0; i < notes.length; i++) {
                String thisNote = notes[i] + " ";
                scoreString += thisNote;
            }
        }
        return scoreString;
    }


    public void playBack(View view) {
        //// TODO: 10/18/2015 HashMap Key must be unique. . .
        //Log.d("PlayBack Log", "PlayBack Entered");
        //Log.d("PlayBack Log", "Length of notesAndRest is " + notesAndRest.length() + "\t" + "Length of lengths is " + lengthOfNotesAndRest.length());

        //notesAndRest = notesAndRest.trim();
        //lengthOfNotesAndRest = lengthOfNotesAndRest.trim();

        Log.d("PlayBack Log@601", "Length of split is" + notesAndRest.split(" ").length + " And Length is " + lengthOfNotesAndRest.split(" ").length);


        if (playBackTrack.getState() == Thread.State.NEW || playBackTrack.getState() == Thread.State.TERMINATED) {
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
	
	
	public void pausePlay(View view) {
        if (playBackTrack != null) {
            playBackTrack.pausePlaying();
            lastNote = playBackTrack.getJ();
            if (lastNote == playBackTrack.getSize() - 1) {
                lastNote = playBackTrack.getLast();
            }
        }
        try {
            playBackTrack.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("Log@Main431", "pausePlay clicked" + lastNote);
    }

	
    public void stopPlay(View view) {
       if (playBackTrack != null) {
           playBackTrack.stopPlaying();
           lastNote = playBackTrack.getLast();
       }
        try {
            playBackTrack.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("Log@Main431", "pausePlay clicked" + lastNote);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.progress = progress + 60;
            Log.d("SeekBar Log", "The progress is " + this.progress);
//            seekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
//            TextView seekBarValue = (TextView) findViewById(R.id.seekbarvalue);
//            seekBarValue.setText(String.valueOf(this.progress));
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        metronome.changeTempo(this.progress);
    }


    private int getTempo() {
        if (new Integer(tempo) == null) {
            tempo = 4;
        }
        return tempo;
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


    private void deleteAll() {
        //Log.i("Log@Main835", "Delete Status is " + scoreFile.deleteAll());
    }


    private void upOctave() {
        TextView keyBoardOctave2 = (TextView) findViewById(R.id.keyboardOctave2);
        if (octavefordisplay <= 4) {
            octavefordisplay = octavefordisplay + 1;
            keyBoardOctave2.setText("C" + String.valueOf(octavefordisplay));
            for (Note note : Notes) {
                if (note != null) {
                    note.upOctave();
                }
//                        keyboardNameDisplay = keyboardNameDisplay + " " + note.toString();
//                        keyBoardOctave1.setText(keyboardNameDisplay);
            }
            displayThread.setOctave(octavefordisplay);
        }
    }


    private void lowerOctave() {
        TextView keyBoardOctave2 = (TextView) findViewById(R.id.keyboardOctave2);
        if (octavefordisplay >= 4) {
            octavefordisplay = octavefordisplay - 1;
            keyBoardOctave2.setText("C" + String.valueOf(octavefordisplay));
            for (Note note : Notes) {
                if (note != null) {
                    note.lowerOctave();
                }
//                        keyboardNameDisplay = keyboardNameDisplay + " " + note.toString();
//                        keyBoardOctave1.setText(keyboardNameDisplay);
            }
            displayThread.setOctave(octavefordisplay);

        }
    }


//    protected void open (Intent data) {
//        Log.d("OnResultLog", "On Activity Result Entered");
//        try {
//            Score score = (Score) data.getSerializableExtra("Score");
//            displayThread.setArchived(score.getScore());
//            for (String s: score.getScore().trim().split("_")) {
//                if (!s.isEmpty()) {
//                    s += " ";
//                    notesAndRest += s;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            notesAndRest = "1 0 2 0 3";
//        }
//        lengthOfNotesAndRest = "1 1 1 1 1";
//
//        Log.d("OnResult", "Notes and rest are " + notesAndRest);
//    }
private void logout(){
    //Creating an alert dialog to confirm logout
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
    alertDialogBuilder.setMessage("Are you sure you want to logout?");
    alertDialogBuilder.setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    //Getting out sharedpreferences
                    SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME,Context.MODE_PRIVATE);
                    //Getting editor
                    SharedPreferences.Editor editor = preferences.edit();

                    //Puting the value false for loggedin
                    editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                    //Putting blank value to email
                    editor.putString(Config.EMAIL_SHARED_PREF, "");

                    //Saving the sharedpreferences
                    editor.commit();

                    //Starting login activity
                    Intent intent = new Intent(MainActivity.this, welcomePage.class);
                    startActivity(intent);
                }
            });

    alertDialogBuilder.setNegativeButton("No",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });

    //Showing the alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();

}
}

class MyAdapter extends BaseAdapter {
    private String att_email;
    private String att_name;
    private Context context;
    String[] tool_list;
    int[] images = {R.drawable.save,R.drawable.edit,R.drawable.add,R.drawable.recordlists,R.drawable.share};

    public MyAdapter(Context context, String email, String name ){
        this.context = context;
        this.att_name = name;
        this.att_email = email;
        tool_list=context.getResources().getStringArray(R.array.navigation_toolbox);
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
        if(view==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            row = inflater.inflate(R.layout.custom_row,viewGroup,false);
        }else{
            row = view;
        }
        TextView titleTextView2=(TextView)row.findViewById(R.id.textView);
        ImageView titleImageView2 = (ImageView)row.findViewById(R.id.imageView);
        TextView t_name = (TextView)row.findViewById(R.id.nav_name);// Creating Text View object from header.xml for name
        if(t_name!=null){
                    t_name.setText("Cambo");
//
        }
//        t_name.setText(att_name);
        TextView t_email = (TextView)row.findViewById(R.id.nav_email);       // Creating Text View object from header.xml for email
//        t_email.setText(att_email);
        titleTextView2.setText(tool_list[i]);
        titleImageView2.setImageResource(images[i]);
        return row;
    }
}