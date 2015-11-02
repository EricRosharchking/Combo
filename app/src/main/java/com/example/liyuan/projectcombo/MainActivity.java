package com.example.liyuan.projectcombo;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
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
    Button btnLogout;

    int key;

    Button buttonBack;
    Metronome metronome;
    Button tempoButton;
    Button timeSignatureButton;
    ImageButton UpperOctave;
    ImageButton LowerOctave;
    ImageButton keyboard;
    ImageButton recordButton;
    TextView textView;
    TextView recordStatus;
    private AudioThread[] audioThreads = new AudioThread[14];
    boolean isRunning;
    final int[] keys = {R.id.cnatural, R.id.csharp, R.id.dnatural, R.id.eflat, R.id.enatural,
            R.id.fnatural, R.id.fsharp, R.id.gnatural, R.id.gsharp, R.id.anatural, R.id.bflat,
            R.id.bnatural, R.id.highcnatural};
    final String[] notes = {"C", "Cs", "D", "Eb", "E", "F", "Fs", "G", "Gs", "A", "Bb", "B", "HighC"};
    final int[] numNotes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    final Note[] Notes = new Note[13];
    private HashMap<Integer, Integer> keyNoteMap;
    //// TODO: 10/4/2015 Use background of button, instead of imagebutton.
    //// TODO: 10/4/2015 Hashmap, int id as key, string name as value.
    boolean onRecord;
    boolean resetScore;
    boolean onHold;
    int timeSignature;
    double secondsPerBeat;
    boolean onRest;
    String notesAndRest;
    String lengthOfNotesAndRest;
    double startRecordTime;
    double stopRecordTime;
    double recordTime;
    DateFormat df;
    Date now;
    int octavefordisplay = 4;
    DisplayThread displayThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

/*

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
*/


            setContentView(R.layout.activity_main);
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
            recordStatus = (TextView) findViewById(R.id.record_status);
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

            displayThread = new DisplayThread();

            SeekBar tempoSeekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
            tempoSeekBar.setOnSeekBarChangeListener(this);
            buttonBack = (Button) findViewById(R.id.buttonBack);

            btnLogout = (Button) findViewById(R.id.btnLogout);
            // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());

            // session manager
            session = new SessionManager(getApplicationContext());

            if (!session.isLoggedIn()) {
                logoutUser();
            }
            // Logout button click event
            if (btnLogout != null) {
                btnLogout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        logoutUser();
                    }
                });
            }

            buttonBack.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),
                            UserMainPage.class);
                    startActivity(i);
                    finish();
                }
            });
        } catch (NumberFormatException e) {
            timeSignature = 60;
            timeSignatureButton.setText("60");
        } finally {
            secondsPerBeat = 60.0 / timeSignature;
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, UserMainPage.class);
        startActivity(intent);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    double noteBeats;

    public void onClick(View v) {
//        Log.d("ButtonLog", "the button you clicked is " + v.getId());
//        TextView keyBoardOctave1 = (TextView) findViewById(R.id.keyboardOctave);
        String keyboardNameDisplay = "";
        TextView keyBoardOctave2 = (TextView) findViewById(R.id.keyboardOctave2);
        if (audioThreads != null) {
            if (v.getId() == R.id.upoctave) {
                if (octavefordisplay <= 4) {

                    octavefordisplay = octavefordisplay + 1;
                    keyBoardOctave2.setText("C"+String.valueOf(octavefordisplay));
                    for (Note note : Notes) {
                        if (note != null) {
                            note.upOctave();
                        }
//                        keyboardNameDisplay = keyboardNameDisplay + " " + note.toString();
//                        keyBoardOctave1.setText(keyboardNameDisplay);
                    }
                }
            } else if (v.getId() == R.id.loweroctave) {
                if (octavefordisplay >= 4) {
                    octavefordisplay = octavefordisplay - 1;
                    keyBoardOctave2.setText("C"+String.valueOf(octavefordisplay));
                    for (Note note : Notes) {
                        if (note != null) {
                            note.lowerOctave();
                        }
//                        keyboardNameDisplay = keyboardNameDisplay + " " + note.toString();
//                        keyBoardOctave1.setText(keyboardNameDisplay);
                    }
                }
            }
        }

        if (v.getId() == recordButton.getId()) {
//            Log.d("ButtonLog","This is the record button");
//            Log.d("Log","OnRecord is " + onRecord);
            if (onRecord == false) {
                recordButton.setImageResource(R.drawable.stopbutton);
                onRecord = true;
                recordStatus.setText("Recording");
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
                displayThread.start();
            } else {
                recordButton.setImageResource(R.drawable.startbutton);
                onRecord = false;
                recordStatus.setText("Click to start");
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
                    textView.setText(displayThread.getArchived());
                    Log.d("MainActivityDisplayLog", "The archived is " + displayThread.getArchived());
                }
            }
        }

        if (v instanceof ImageButton && onRecord == true) {
//            Log.d("what", String.valueOf(textView.getText()) + "* compared to *"+ getText(R.string.main_score));
            if (resetScore == true) {
                textView.setText("");
                resetScore = false;
            }
            Log.d("KeyNoteMap Log", "KeyNoteMap State is " + (keyNoteMap == null));
            Log.d("KeyNoteMap Log", "KeyNoteMap.get State is " + keyNoteMap.containsKey(v.getId()));
            /*if (keyNoteMap != null && keyNoteMap.get(v.getId()) != null) {
                textView.append("0 " + keyNoteMap.get(v.getId()) + " ");
            }*/
        } else {
            Log.d("Log", "This is not a button you clicked");
        }
/*
        if(v.getId() == R.id.time_signature) {

        }*/
    }

    double noteStartTime;
    double noteEndTime;
    double rest;
    double restStartTime;
    double restEndTime;
    double elapse;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ImageButton) {

            int noteID = 1;
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
                    break;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {                                     //just press the key
                if (onRecord == true) {


                    key = noteID;
                    if (displayThread != null && displayThread.getState() != Thread.State.TERMINATED) {
                        displayThread.update(key);
                    }

                    if (Notes[noteID - 1] != null) {
                        Log.d("Note Log", "The frequency of the note is " + Notes[noteID - 1].toString());
                    }
                    //Play Part Start
                    if (audioThreads[noteID].getState() == Thread.State.NEW) {
                        audioThreads[noteID].start();
                        Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
                    } else {
                        audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
                        Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
                        audioThreads[noteID].start();
                    }
                    //Play Part End

                    onHold = true;
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
                    if (audioThreads[noteID].getState() == Thread.State.NEW) {
                        audioThreads[noteID].start();
                        Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
                    } else {
                        audioThreads[noteID] = new AudioThread(Notes[noteID - 1]);
                        Log.d("AudioThreads Log", "AudioThread State is: " + audioThreads[noteID].getState().toString());
                        audioThreads[noteID].start();
                    }
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {

                audioThreads[noteID].stopPlaying();
                if (onRecord == true) {

                    key = 0;
                    if (displayThread != null && displayThread.getState() != Thread.State.TERMINATED) {
                        displayThread.update(key);
                    }

                    audioThreads[noteID].stopPlaying();

                    onHold = false;
                    noteEndTime = System.currentTimeMillis();
                    restStartTime = noteEndTime;
                    elapse = noteEndTime - noteStartTime;
                    elapse /= 1000;
                    //Log.d("TouchLog", "The elapse is " + elapse);
                    noteBeats = elapse / secondsPerBeat;
                    Log.d("BeatsLog", "There are " + noteBeats + " beats in the note");
                    notesAndRest = notesAndRest + " " + keyNoteMap.get((v).getId());
                    lengthOfNotesAndRest = lengthOfNotesAndRest + " " + noteBeats;
                }
            }
        }

        Log.d("The score should be", notesAndRest);
        Log.d("The length of it is", lengthOfNotesAndRest);
        textView.setText(displayThread.getDisplay());
        return super.onTouchEvent(event);
    }

    int progress;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AudioThread thread : audioThreads) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            thread = null;
        }
    }

    boolean metronomeRunning;

    public void startMetronome(View view) {
        if (metronomeRunning == false) {
            metronome = new Metronome();
            metronome.start();
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
        //// TODO: 10/18/2015 HashMap Key must be unique. . .
        Log.d("PlayBack Log", "PlayBack Entered");
        Log.d("PlayBack Log", "Length of notesAndRest is " + notesAndRest.length() + "\t" + "Length of lengths is " + lengthOfNotesAndRest.length());

        notesAndRest = notesAndRest.trim();
        lengthOfNotesAndRest = lengthOfNotesAndRest.trim();

        Log.d("PlayBack Log", "Length of split is" + notesAndRest.split(" ").length + " And Length is " + lengthOfNotesAndRest.split(" ").length);
        for (int i = 0; i < notesAndRest.split(" ").length; i++) {
            Log.d("PlayBack Log", "The Notes or Rest is " + notesAndRest.split(" ")[i]);
        }
        //// TODO: 10/18/2015 Trim the two Strings before split
        for (int i = 0; i < lengthOfNotesAndRest.split(" ").length; i++) {
            Log.d("PlayBack Log", "The Notes or Rest is " + lengthOfNotesAndRest.split(" ")[i]);
        }

        if (notesAndRest.length() != 0 && notesAndRest.split(" ").length == lengthOfNotesAndRest.split(" ").length) {
            String[] scoreNotes = notesAndRest.split(" ");
            String[] scoreLength = lengthOfNotesAndRest.split(" ");
            int[] numericNotes = new int[scoreNotes.length];
            for (int i = 0; i < scoreNotes.length; i++) {
                numericNotes[i] = Integer.parseInt(scoreNotes[i]);
            }
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


            PlayBack playBack = new PlayBack(numericNotes, scoreLength);
            Log.d("PlayBack Log", "PlayBack initialised");
            playBack.start();
            try {
                playBack.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            this.progress = progress + 60;
            Log.d("SeekBar Log", "The progress is " + this.progress);
            seekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
            TextView seekBarValue = (TextView) findViewById(R.id.seekbarvalue);
            seekBarValue.setText(String.valueOf(this.progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        metronome.changeTempo(this.progress);
    }
}