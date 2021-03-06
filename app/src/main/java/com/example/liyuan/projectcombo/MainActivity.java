package com.example.liyuan.projectcombo;

import android.graphics.Color;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.liyuan.projectcombo.kiv.MyAdapter;
import com.facebook.login.LoginManager;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    PlayBack playBackTrack;
    DisplayThread displayThread;
    Metronome metronome;
    ScoreFile scoreFile;
    Score score;

    private AudioThread[] audioThreads = new AudioThread[14];
    final int[] keys = {R.id.cnatural, R.id.csharp, R.id.dnatural, R.id.eflat, R.id.enatural,
            R.id.fnatural, R.id.fsharp, R.id.gnatural, R.id.gsharp, R.id.anatural, R.id.bflat,
            R.id.bnatural, R.id.highcnatural};
    final int[] numNotes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    final Note[] Notes = new Note[13];
    int[] numericNotes;
    double[] lengths;
    private final String CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><head><style>p{color:white;font-size:30px}</style><body style=\"background-color:#222222;\"><p style=\"font-size:30px\">";

    Button btnLogout;
    ImageButton UpperOctave;
    ImageButton LowerOctave;
    ImageButton keyboard;
    //    ImageButton recordButton;
    TextView textView;
    //    TextView recordStatus;
    WebView myWebView;

    private HashMap<Integer, Integer> keyNoteMap;
    //// TODO: 10/4/2015 Use background of button, instead of imagebutton.
    //// TODO: 10/4/2015 Hashmap, int id as key, string name as value.

    private String userEmail;
    private String userName;
    String userScoreName;
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
    boolean loggedIn;

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

    final int disabledID = 0;
    int tempo;
    int noteID;
    int timeSig;
    int octavefordisplay = 4;
    int lastNote;
    int key;
    int noteAddOn;


    private Spinner spinner;
    private SeekBar tempoSeekBar;
    private ListView mDrawerList2;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private MyAdapter myAdapter;
    private Toolbar toolbar;
    private static Menu topMenu;

//    TextView textView_countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //In onresume fetching value from sharedpreference
            SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            //Fetching the boolean value form sharedpreferences
            loggedIn = sharedPreferences.getBoolean(Config.LOGGEDIN_SHARED_PREF, false);

            if(!loggedIn){
                Intent intent = new Intent(MainActivity.this, welcomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

    /* Assinging the toolbar object ot the view
    and setting the the Action bar to our toolbar
     */
            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);
            mDrawerList2 = (ListView) findViewById(R.id.navigationList_left);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            userScoreName = (String) getIntent().getSerializableExtra("userScore");

            if (userScoreName != null) {
                mActivityTitle = userScoreName;
            } else {
                mActivityTitle = "Create new song";
            }

            getSupportActionBar().setTitle(mActivityTitle);
            addDrawerItems2();
            setupDrawer();

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            LayoutInflater inflater = getLayoutInflater();

            View listHeaderView = inflater.inflate(R.layout.navigation_drawer_header, null, false);

            if (getIntent().getSerializableExtra("userEmail") != null)
                userEmail = (String) getIntent().getSerializableExtra("userEmail");//userEmail = (String) getIntent().getSerializableExtra("userEmail");
            if (getIntent().getSerializableExtra("userName") != null)
                userName = (String) getIntent().getSerializableExtra("userName");

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            userEmail = sharedPref.getString("userEmail", userEmail);
            userName = sharedPref.getString("userName", userName);

            TextView t_name = (TextView) listHeaderView.findViewById(R.id.nav_name);// Creating Text View object from header.xml for name
            if (t_name != null)
                t_name.setText(userName);
            else
                Log.i("Log@myAdapter", "TextView t_name is null");
            TextView t_email = (TextView) listHeaderView.findViewById(R.id.nav_email);       // Creating Text View object from header.xml for email
            if (t_email != null)
                t_email.setText(userEmail);
            else
                Log.i("Log@myAdapter", "TextView t_email is null");

            mDrawerList2.addHeaderView(listHeaderView);

            View decorView = getWindow().getDecorView();
// Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
// Remember that you should never show the action bar if the
// status bar is hidden, so hide that too if necessary.


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
//                Log.d("KeyNoteMap Log", "The Key is " + keys[i]);
//                Log.d("KeyNoteMap Log", "The Note is " + numNotes[i]);
            }

//            Log.d("HashMap Log", "The size of KeyNoteMap is " + keyNoteMap.size());
            for (int i = 0; i < Notes.length; i++) {
                Notes[i] = new Note(i + 1);
//                Log.d("Notes Log", "The note is " + Notes[i].toString());
            }

            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            String buffer = null;
            String rate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                buffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
                rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            }

//            Log.d("AudioManager Log", "System Output Sample Rate is " + rate);
//            Log.d("AudioManager Log", "System Output Frames per Buffer is " + buffer);
            try {
                Method method = audioManager.getClass().getMethod("getOutputLatency", int.class);
//                Log.d("AudioManager Log", "System Latency is " + method.invoke(audioManager, AudioManager.STREAM_MUSIC));
            } catch (Exception e) {

            }

            isRunning = true;

            for (int i = 1; i < audioThreads.length; i++) {
                audioThreads[i] = new AudioThread(Notes[i - 1]);
            }

            //webview score
            myWebView = (WebView) findViewById(R.id.web_score);

            String htmlString = CONTENT + "Here will be the notes you played.</p></body></html>";
            myWebView.setBackgroundColor(Color.TRANSPARENT);
            myWebView.loadData(htmlString, "text/html; charset=utf-8", "UTF-8");
            //textview score
            textView = (TextView) findViewById(R.id.main_score);
            textView.setMovementMethod(new ScrollingMovementMethod());
            onRecord = false;
            resetScore = false;
            onHold = false;
            onRest = true;
//            Log.d("tempoLog", "" + tempo);
            df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            now = new Date();
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;
            metronomeRunning = false;

            timeSig = 4;
            noteID = 1;
            key = 0;

            displayThread = new DisplayThread();

            tempoSeekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
            tempoSeekBar.setOnSeekBarChangeListener(this);
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logout();
                }
            });

            tempo = 60;
            spinner = (Spinner) findViewById(R.id.time_signature);
// Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.time_signature_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);
//            spinner.setOnItemSelectedListener(adapter);
            spinner.setOnItemSelectedListener(this);
            scoreFile = new ScoreFile();
            numericNotes = null;
            lengths = null;
            withMetronome = false;
            opened = false;
            beatLength = 1.0;
//count down
//            textView_countdown = (TextView) findViewById(R.id.countdown);
//            textView_countdown.setText("" + timeSig);
//            countdown = new Countdown(this);
//            animator = new ValueAnimator();
//            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                public void onAnimationUpdate(ValueAnimator animation) {
//                    textView_countdown.setText(String.valueOf(animation.getAnimatedValue()));
//                }
//            });
//            animator.setEvaluator(new TypeEvaluator<Integer>() {
//                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
//                    return Math.round(startValue + (endValue - startValue) * fraction);
//                }
//            });
//            animator.setObjectValues(timeSig, 0);
            //animator.setDuration(5000);
            //animator.start();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("userEmail", userEmail);
            editor.putString("userName", userName);
            editor.apply();
        } catch (NumberFormatException e) {
            tempo = 60;
//            tempoButton.setText("60");
        } finally {
            secondsPerBeat = 60.0 / tempo;
        }
    }


    private void addDrawerItems2() {
        int[] images = {R.drawable.createnewsong, R.drawable.save, R.drawable.edit, R.drawable.addlyrics, R.drawable.recordlists, R.drawable.share};
        String[] tool_list = this.getResources().getStringArray(R.array.navigation_toolbox);
        myAdapter = new MyAdapter(this, "", "", tool_list, images, disabledID);
        mDrawerList2.setAdapter(myAdapter);
        mDrawerList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        Intent intent = new Intent();
                        intent.putExtra("userName", userName);
                        intent.putExtra("userEmail", userEmail);
                        setResult(1, intent);
                        finish();
                        break;
                    case 2:
                        save();
                        break;
                    case 3:
                        editScore();
                        break;
                    case 4:
                        addLyrics();
                        break;
                    case 5:
                        openOrNew();
                        break;
                    case 6:
                        exportToPDF();
                        break;

                }
//                Toast.makeText(MainActivity.this, "position is " + position + ", id is " + id + " view id is " + view.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToPDF() {
        Intent intent = new Intent(this, CreateScoreActivity.class);
        intent.putExtra("action", 1);
        intent.putExtra("ScoreFile", scoreFile);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }


    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                String title = "";
                if (userScoreName != null) {
                    title = userScoreName;
                } else {
                    title = "Create new song";
                }
                getSupportActionBar().setTitle(title);
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


    /**
     * Directing the user to add lyrics page.
     * EditScoreActivity will share the scores user entered from MainActivity
     **/
    private void addLyrics() {
        Intent i = new Intent(MainActivity.this,
                AddLyricsActivity.class);
        //i.putExtra("scores", Html.fromHtml(displayThread.getDisplay() + "\u2225"));

        i.putExtra("userEmail", userEmail);
        i.putExtra("userName", userName);
        if (!isOpened) {
            i.putExtra("scores", displayThread.getDisplay());
            i.putExtra("notes", prepareScore());
            i.putExtra("lengths", prepareLengths());
        } else {
            i.putExtra("score", score);
        }

        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        topMenu = menu;
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
                if (!onRecord) {
                    item.setIcon(R.drawable.stopbutton);
                } else {
                    item.setIcon(R.drawable.startbutton);
                }
                record();
                break;
            case R.id.playBack:
                if (!isPlayBack) {
                    playBack(null);
                    item.setIcon(R.drawable.onplay);
                    changeStopIcon();
                    changePauseIcon();
                }
                break;
            case R.id.pause:
                pausePlay(null);
                changePlayBackIcon();
                changePauseIcon();
                break;
            case R.id.stop:
                stopPlay(null);
                changePlayBackIcon();
                changeStopIcon();
                break;
            case R.id.metro:
                if (!withMetronome) {
                    withMetronome = true;
                    item.setIcon(R.drawable.metro_on);
                } else {
                    withMetronome = false;
                    item.setIcon(R.drawable.metro_off);
                }
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void record() {
        if (spinner != null) spinner.setEnabled(true);
        if (tempoSeekBar != null) tempoSeekBar.setEnabled(true);
        if (!onRecord) {
            onRecord = true;
            resetScore = true;
            textView.setText(R.string.main_score);
            startRecordTime = System.currentTimeMillis() + 1000 * timeSig * secondsPerBeat;
            restStartTime = startRecordTime;
            now = new Date();
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;

            displayThread = new DisplayThread();
            displayThread.setTimeSignature(timeSig);
            displayThread.setTempo(tempo);
            displayThread.update(key, 0.0);
            startMetronome(null);

            long delay = (long) (timeSig * 1000 * secondsPerBeat);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            onRecord = false;
            resetScore = false;
            stopRecordTime = System.currentTimeMillis();
            recordTime = stopRecordTime - startRecordTime;
            restEndTime = stopRecordTime;
            rest = (restEndTime - restStartTime) / 1000 / secondsPerBeat;
            rest = autoCorrectLength(rest);
            notesAndRest = notesAndRest + " " + "0";
            lengthOfNotesAndRest = lengthOfNotesAndRest + " " + rest;
//            Log.d("RecordingLog", "The Record Time is " + recordTime);
//                textView.setText(df.format(now) + " " + notesAndRest + "\n" + df.format(now) + " " + lengthOfNotesAndRest);


//            if (displayThread != null) {
//                displayThread.stopThread();
//                Log.d("MainActivityDisplayLog", "The state of displayThread is " + displayThread.getState().toString());
//                    textView.setText(displayThread.getArchived());
            displayThread.update(rest);
//            textView.setText(Html.fromHtml(displayThread.getDisplay() + "\u2225"));//ending pause in html
//                Log.d("MainActivityDisplayLog", "The archived is " + displayThread.getArchived());
//            }


            if (metronome != null && metronomeRunning) {
                metronome.stop();
                metronomeRunning = false;
            }
        }
        opened = false;
        String str = displayThread.getDisplay();
        str += "</p></body></html>";
        str = CONTENT + str;
        myWebView.loadData(str, "text/html; charset=utf-8", "UTF-8");
    }


    public double autoCorrectLength(double actualLength) {
        double result = (double) (int) (actualLength / 1);
        actualLength -= result;
        if (actualLength >= 0.9)
            actualLength = 1.0;
        else if (actualLength >= 0.65)
            actualLength = 0.75;
        else if (actualLength >= 0.4)
            actualLength = 0.5;
        else if (actualLength >= 0.2)
            actualLength = 0.25;
        else
            actualLength = 0.0;
        result += actualLength;
        return result;
    }

    public void onClick(View v) {
//        Log.d("ButtonLog", "the button you clicked is " + v.getId());
//        TextView keyBoardOctave1 = (TextView) findViewById(R.id.keyboardOctave);


        if (audioThreads != null) {
            if (v.getId() == R.id.upoctave) {
                upOctave();
            } else if (v.getId() == R.id.loweroctave) {
                lowerOctave();
            }
        }

        if (v instanceof ImageButton) {
//            Log.d("what", String.valueOf(textView.getText()) + "* compared to *"+ getText(R.string.main_score));

            if (onRecord) {
                if (resetScore) {
                    textView.setText("");
                    resetScore = false;
                }
//                Log.d("KeyNoteMap Log", "KeyNoteMap State is " + (keyNoteMap == null));
//                Log.d("KeyNoteMap Log", "KeyNoteMap.get State is " + keyNoteMap.containsKey(v.getId()));
                        /*if (keyNoteMap != null && keyNoteMap.get(v.getId()) != null) {
                            textView.append("0 " + keyNoteMap.get(v.getId()) + " ");
                        }*/
            }

        } else {
            Log.d("Log", "This is not a button you clicked");
        }

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        noteAddOn = 1;
        if (v instanceof ImageButton) {

            if (System.currentTimeMillis() < startRecordTime)
                return super.onTouchEvent(event);

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

            if (event.getAction() == MotionEvent.ACTION_DOWN) {                                     //just press the key

//                Log.d("Log@Main484", noteID + "AudioThread State is " + audioThreads[noteID].getState().toString());
                //Play Part Start
                audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
//                Log.d("Log@MainActivity490", "AudioThread State is not NEW: " + audioThreads[noteID].getState().toString());
                audioThreads[noteID].start();

                //Play Part End

                onHold = true;
                if (onRecord) {

                    noteStartTime = System.currentTimeMillis();                                                    //start count the time
                    rest = (noteStartTime - restStartTime) / 1000 / secondsPerBeat;
                    rest = autoCorrectLength(rest);
                    ////TODO: auto-correct, get it done
//                    Log.d("RestLog", "Rest is " + rest + " beats long");
                    if (rest >= 0) {
                        notesAndRest = notesAndRest + " " + "0";
                        lengthOfNotesAndRest = lengthOfNotesAndRest + " " + rest;

                        key = noteID;
                        key = noteID * noteAddOn;
                        if (displayThread != null) { //&& displayThread.getState() != Thread.State.TERMINATED) {
                            displayThread.update(key, rest);
                        }
                        restEndTime = noteStartTime;
                    }
                    String str = displayThread.getDisplay();
                    str += "</p></body></html>";
                    str = CONTENT + str;
                    myWebView.loadData(str, "text/html; charset=utf-8", "UTF-8");

//                    if (Notes[noteID - 1] != null) {
//                        Log.d("Note Log", "The frequency of the note is " + Notes[noteID - 1].toString());
//                    }
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {

                audioThreads[noteID].stopPlaying();
                if (onRecord == true) {

                    onHold = false;
                    noteEndTime = System.currentTimeMillis();
                    elapse = noteEndTime - noteStartTime;
                    elapse /= 1000;
                    //Log.d("TouchLog", "The elapse is " + elapse);
                    noteBeats = elapse / secondsPerBeat;
                    noteBeats = autoCorrectLength(noteBeats);
//                    Log.d("BeatsLog", "There are " + noteBeats + " beats in the note");
                    if (noteBeats > 0) {
                        notesAndRest = notesAndRest + " " + keyNoteMap.get((v).getId()) * noteAddOn;
                        lengthOfNotesAndRest = lengthOfNotesAndRest + " " + noteBeats;
                        restStartTime = noteEndTime;
                        key = 0;
                        if (displayThread != null) { //&& displayThread.getState() != Thread.State.TERMINATED) {
                            displayThread.update(key, noteBeats);
                        }
                    }
                }
            }
        }

//        Log.d("The score should be", notesAndRest);
//        Log.d("The length of it is", lengthOfNotesAndRest);
        String str = displayThread.getDisplay();
        str += "</p></body></html>";
        str = CONTENT + str;
        if (onRecord) {
            myWebView.loadData(str, "text/html; charset=utf-8", "UTF-8");
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getSerializableExtra("score") != null) {
            //open(getIntent());
            Score thisScore = (Score) getIntent().getSerializableExtra("score");
//            Log.d("Log@617", "Score is null? " + (thisScore == null));
            if (thisScore != null && thisScore.getScore() != null) {
//                Log.d("Log@Main619", "Score is " + thisScore.getScore().length);
                String htmlData = CONTENT + extractScore(thisScore.getScore(), thisScore.getLengths(), thisScore.getTimeSignature()) + "</p></body></html>";

                myWebView.loadData(htmlData, "text/html; charset=utf-8", "UTF-8");
                score = thisScore;
                tempo = score.getTempo();
                ((TextView) findViewById(R.id.seekbarvalue)).setText(String.valueOf(tempo));
                opened = true;
                isOpened = true;
                if (tempoSeekBar != null) tempoSeekBar.setEnabled(false);
                if (spinner != null) {
                    spinner.setEnabled(false);
                    switch (score.getTimeSignature()) {
                        case "4/4":
                            spinner.setSelection(0);
                            timeSig = 4;
                            break;
                        case "3/4":
                            timeSig = 3;
                            spinner.setSelection(1);
                            break;
                        default:
                            break;
                    }
                }
                myAdapter.setDisabledID(-1);
            }

        } else {
            Log.e("onResumeLog@Main555", "Score is null");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == 1) {
            userEmail = intent.getStringExtra("userEmail");
            userName = intent.getStringExtra("userName");
        }
        super.onActivityResult(requestCode, resultCode, intent);
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

        } finally {
            super.onDestroy();
        }
    }


    public void startMetronome(View view) {
        if (!metronomeRunning) {
            if (metronome != null) {
                metronome.changeTimeSignature(timeSig);
                metronome.setWithMetronome(withMetronome);
                metronome.start();
            } else {
                metronome = new Metronome(tempo);
                metronome.changeTimeSignature(timeSig);
                metronome.setWithMetronome(withMetronome);
                metronome.start();
            }
            metronomeRunning = true;

        } else {
            metronome.stop();
            metronomeRunning = false;
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


    private String extractScore(int[] notes, double[] lengths, String timeSignature) {
        String t = "||";
        if (notes != null && lengths != null && notes.length == lengths.length) {
            switch (timeSignature) {
                case "3/4":
                    timeSig = 3;
                    break;
                case "4/4":
                    timeSig = 4;
                    break;
            }
            double totalLengths = 0.0;
            int barCount = 1;
            int lineCount = 1;

//            Log.i("Log@1257", "notes are " + Arrays.toString(notes));
//            Log.i("Log@Main1258", "lengths are " + Arrays.toString(lengths));

            for (int i = 0; i < notes.length; i++) {
                int n = notes[i];
                double l = lengths[i];
                String thisKey = " 1";
                switch (n) {
                    case 0:
                        thisKey = " 0";
                        break;
                    case -3:
                    case 3:
                    case 42:
                        thisKey = " 2";
                        break;
                    case -4:
                    case 4:
                    case 56:
                    case -5:
                    case 5:
                    case 70:
                        thisKey = " 3";
                        break;
                    case -6:
                    case 6:
                    case 84:
                    case -7:
                    case 7:
                    case 98:
                        thisKey = " 4";
                        break;
                    case -8:
                    case 8:
                    case 112:
                    case -9:
                    case 9:
                    case 126:
                        thisKey = " 5";
                        break;
                    case -10:
                    case 10:
                    case 140:
                        thisKey = " 6";
                        break;
                    case -11:
                    case 11:
                    case 154:
                    case -12:
                    case 12:
                    case 168:
                        thisKey = " 7";
                        break;
                    default:
                        break;
                }
//            Log.d("Log@DisplayActivity133", thisKey);
                if (n < 0) {
                    thisKey += "\u0323";
                } else if (n > 13) {
                    thisKey += "\u0307";
                } else {
                    thisKey += "";
                }
                t += thisKey;

                if (l == 0) {
                    t = t.substring(0, t.length() - 1);
                } else {
                    totalLengths += l;
                    if (totalLengths > barCount * timeSig) {
                        while (totalLengths > barCount * timeSig) {
                            double firstHalf = barCount * timeSig - (totalLengths - l);
                            int count = (int) (firstHalf / 0.25);
                            int x = count / 4;
                            int y = count % 4;

                            switch (y) {
                                case 1:
                                    t += "<sub>\u0333</sub> ";
                                    break;
                                case 2:
                                    t += "<sub>\u0332</sub> ";
                                    break;
                                case 3:
                                    t += "<sub>\u0332</sub> \u2022 ";
                                    break;
                                default:
                                    break;
                            }

                            for (int j = 1; j < firstHalf; j++) {
                                t += " - ";
                            }

                            barCount++;
                            t += " | ";
                            t += thisKey;
                            l -= firstHalf;
                        }
                        double secondHalf = totalLengths - timeSig * barCount;
                        int count = (int) (secondHalf / 0.25);
                        int x = count / 4;
                        int y = count % 4;

                        switch (y) {
                            case 1:
                                t += "<sub>\u0333</sub> ";
                                break;
                            case 2:
                                t += "<sub>\u0332</sub> ";
                                break;
                            case 3:
                                t += "<sub>\u0332</sub> \u2022 ";
                                break;
                            default:
                                break;
                        }

                        for (int j = 1; j < secondHalf; j++) {
                            t += " - ";
                        }
                    }
                    int count = (int) (l / 0.25);
                    int x = count / 4;
                    int y = count % 4;

                    switch (y) {
                        case 1:
                            t += "<sub>\u0333</sub> ";
                            break;
                        case 2:
                            t += "<sub>\u0332</sub> ";
                            break;
                        case 3:
                            t += "<sub>\u0332</sub> \u2022 ";
                            break;
                        default:
                            break;
                    }

                    for (int j = 1; j < l; j++) {
                        t += " - ";
                    }

                    if (totalLengths == barCount * timeSig) {
                        barCount++;
                        t += " | ";
                    }
                }
            }
            t += " ||";
        }

        return t.trim();
    }


    public void playBack(View view) {
        //// TODO: 10/18/2015 HashMap Key must be unique. . .
        //Log.d("PlayBack Log", "PlayBack Entered");
        //Log.d("PlayBack Log", "Length of notesAndRest is " + notesAndRest.length() + "\t" + "Length of lengths is " + lengthOfNotesAndRest.length());

        //Log.d("PlayBack Log@601", "Length of split is" + notesAndRest.split(" ").length + " And Length is " + lengthOfNotesAndRest.split(" ").length);

        if (playBackTrack == null || playBackTrack.getState() == Thread.State.NEW || playBackTrack.getState() == Thread.State.TERMINATED) {
//            for (int i = 0; i < notesAndRest.split(" ").length; i++) {
//                Log.d("PlayBack Log", "The Notes or Rest is " + notesAndRest.split(" ")[i]);
//            }
            //// TODO: 10/18/2015 Trim the two Strings before split
//            for (int i = 0; i < lengthOfNotesAndRest.split(" ").length; i++) {
//                Log.d("PlayBack Log", "The Notes or Rest is " + lengthOfNotesAndRest.split(" ")[i]);
//            }

//            Log.i("Log@Main735", "score is null? " + (score == null));
//            Log.i("Log@Main1215", "opened is " + opened);
            if (opened) {
                numericNotes = score.getScore();
                lengths = score.getLengths();
            } else {
                numericNotes = prepareScore();
                lengths = prepareLengths();
            }

//            if (numericNotes != null && lengths != null) {
//                Log.d("Log@Main705", " " + numericNotes.length + " " + lengths.length);
//            }
            if (numericNotes.length == lengths.length) {

                playBackTrack = new PlayBack(numericNotes, lengths, lastNote, tempo);
//                Log.d("PlayBack Log", "PlayBack initialised");
                playBackTrack.start();

            }
        }
    }


    public void pausePlay(View view) {
        if (playBackTrack != null) {
            playBackTrack.pausePlaying();
            lastNote = playBackTrack.getJ();
            try {
                playBackTrack.join();
                changePlayBackIcon();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Log.i("Log@Main431", "pausePlay clicked" + lastNote);
            isPlayBack = false;
        }
    }


    public void stopPlay(View view) {
        if (playBackTrack != null) {
            playBackTrack.stopPlaying();
            lastNote = 0;
            try {
                playBackTrack.join();
                changePlayBackIcon();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Log.i("Log@Main431", "pausePlay clicked" + lastNote);
            isPlayBack = false;
        }
    }


    public void changePauseIcon() {
        if (isPlayBack) {
            topMenu.findItem(R.id.pause).setIcon(R.drawable.pause_onclick);
        } else {
            topMenu.findItem(R.id.pause).setIcon(R.drawable.pause);
        }
    }


    public void changeStopIcon() {
        if (isPlayBack) {
            topMenu.findItem(R.id.stop).setIcon(R.drawable.stop_onclick);
        } else {
            topMenu.findItem(R.id.stop).setIcon(R.drawable.stop);
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && !opened) {
            tempo = progress + 60;
//            Log.d("SeekBar Log", "The seekBar value is " + tempo);
            ((TextView) findViewById(R.id.seekbarvalue)).setText(String.valueOf(tempo));

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!opened) {
            if (metronome != null)
                metronome.changeTempo(tempo);
            secondsPerBeat = 60.0 / tempo;
        }
    }


    public void openOrNew() {
        Intent intent = new Intent(this, CreateScoreActivity.class);
        intent.putExtra("ScoreFile", scoreFile);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }


    private void save() {
        Intent intent = new Intent(this, SaveActivity.class);
        score = new Score();
        numericNotes = prepareScore();
        lengths = prepareLengths();
//        Log.i("Log@Main805", "numericNotes is null? " + (numericNotes == null));
//        Log.i("Log@Main806", "lengths is null?" + (lengths == null));
        score.setScore(numericNotes, lengths);
        switch (timeSig) {
            case 3:
                score.setTimeSignatureFT();
                break;
            case 4:
                score.setTimeSignatureFF();
                break;
        }
        score.setTempo(tempo);
        score.setAuthor(userName);
        intent.putExtra("score", score);
        intent.putExtra("ScoreFile", scoreFile);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }


    public void editScore() {
        Intent intent = new Intent(this, EditScoreActivity.class);
        if (!isOpened) {
            intent.putExtra("notes", prepareScore());
            intent.putExtra("lengths", prepareLengths());
        } else {
            intent.putExtra("score", score);
        }
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("userName", userName);
        intent.putExtra("userScore", userScoreName);
        startActivity(intent);
    }


    private void deleteAll() {
        Log.i("Log@Main835", "Delete Status is " + scoreFile.deleteAll());
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
            }
            displayThread.setOctave(octavefordisplay);

        }
    }


    public void onBackPressed() {
        logout();
    }

    private void logout() {


        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to logout?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //logout from fb
                        LoginManager.getInstance().logOut();
//                        Intent in = new Intent(MainActivity.this, welcomePage.class);
//                        startActivity(in);
//                        finish();


                        //Getting out sharedpreferences
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                        SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.EMAIL_SHARED_PREF, "");
                        editor.putString(Config.UNAME_SHARED_PREF, "");

                        //Clear the shared preference
                        SharedPreferences p = getPreferences(Context.MODE_PRIVATE);
                        p.edit().clear().commit();

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(MainActivity.this, welcomePage.class);
                        startActivity(intent);
                        finish();
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

    public static void changePlayBackIcon() {
        MenuItem item = topMenu.findItem(R.id.playBack);
        item.setIcon(R.drawable.playback);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(MainActivity.this, "position is " + position + ", id is " + id + " view id is " + view.getId(), Toast.LENGTH_LONG).show();
        switch (position) {
            case 0:
                timeSig = 4;
                break;
            case 1:
                timeSig = 3;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
