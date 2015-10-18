package com.example.liyuan.projectcombo;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {


    Metronome metronome;
    private AudioThread[] audioThreads = new AudioThread[14];
    boolean isRunning;
    ImageButton keyboard;
    TextView textView;
    TextView recordStatus;
    final int[] keys = {R.id.cnatural, R.id.csharp, R.id.dnatural, R.id.eflat, R.id.enatural,
            R.id.fnatural, R.id.fsharp, R.id.gnatural, R.id.gsharp, R.id.anatural, R.id.bflat,
            R.id.bnatural, R.id.highcnatural};
    final String[] notes = {"C", "Cs", "D", "Eb", "E", "F", "Fs", "G", "Gs", "A", "Bb", "B", "HighC"};
    final Note[] Notes = new Note[13];
    private HashMap<Integer, String> keyNoteMap;
    //// TODO: 10/4/2015 Use background of button, instead of imagebutton.
    //// TODO: 10/4/2015 Hashmap, int id as key, string name as value.
    boolean onRecord;
    ImageButton recordButton;
    boolean resetScore;
    boolean onHold;
    Button tempoButton;
    Button timeSignatureButton;
    ImageButton UpperOctave;
    ImageButton LowerOctave;
    int timeSignature;
    double secondsPerBeat;
    boolean onRest;
    String notesAndRest;
    String lengthOfNotesAndRest;
    double startRecordTime;
    double stopRecordTime;
    double recordTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

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

            setContentView(R.layout.activity_main);
            UpperOctave = (ImageButton) findViewById(R.id.upoctave);
            LowerOctave = (ImageButton) findViewById(R.id.loweroctave);
            UpperOctave.setOnClickListener(this);
            LowerOctave.setOnClickListener(this);

            for(int i :keys) {
                keyboard = (ImageButton) findViewById(i);
                keyboard.setOnTouchListener(this);
                keyboard.setOnClickListener(this);
            }

            keyNoteMap = new HashMap<Integer, String>();
            for(int i = 0; i < keys.length; i ++) {
                keyNoteMap.put(keys[i], notes[i]);
                Log.d("KeyNoteMap Log", "The Key is " + keys[i]);
                Log.d("KeyNoteMap Log", "The Note is " + notes[i]);
            }

            Log.d("HashMap Log", "The size of KeyNoteMap is " + keyNoteMap.size());

            for (int i = 0; i < Notes.length; i ++) {
                Notes[i] = new Note(i + 1);
                Log.d("Notes Log", "The note is " + Notes[i].toString());
            }

            AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
            String buffer = null;
            String rate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                buffer = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
                rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            }

            Log.d("AudioManager Log", "System Output Sample Rate is " + rate);
            Log.d("AudioManager Log", "System Output Frames per Buffer is " + buffer);
            try{
                Method method = audioManager.getClass().getMethod("getOutputLatency", int.class);
                Log.d("AudioManager Log", "System Latency is " + method.invoke(audioManager, AudioManager.STREAM_MUSIC));
            } catch (Exception e) {

            }

            isRunning = true;

            for (int i = 1; i < audioThreads.length; i ++) {
                audioThreads[i] = new AudioThread(Notes[i - 1]);
            }

            textView = (TextView) findViewById(R.id.main_score);
            recordStatus = (TextView) findViewById(R.id.record_status);
            onRecord = false;
            recordButton = (ImageButton) findViewById(R.id.record_button);
            recordButton.setOnClickListener(this);
            resetScore = false;
            onHold = false;
            onRest = true;
            tempoButton = (Button) findViewById(R.id.tempo);
            timeSignatureButton = (Button) findViewById(R.id.time_signature);
            timeSignature = Integer.parseInt((String) getText(R.string.time_signature));
            Log.d("TimeSignatureLog", "" + timeSignature);
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date now = new Date();
            notesAndRest = df.format(now);
            lengthOfNotesAndRest = notesAndRest;
            metronome = new Metronome();
        }
        catch (NumberFormatException e) {
            timeSignature = 60;
            timeSignatureButton.setText("60");
        }
        finally {
            secondsPerBeat = 60.0 / timeSignature;
        }
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

        if (audioThreads != null) {
            if (v.getId() == R.id.upoctave) {
                for (Note note : Notes) {
                    if (note != null) {
                        note.upOctave();
                    }
                }
            } else if (v.getId() == R.id.loweroctave) {
                for (Note note : Notes) {
                    if (note != null) {
                        note.lowerOctave();
                    }
                }
            }
        }

        if ( v.getId() == recordButton.getId()) {
//            Log.d("ButtonLog","This is the record button");
//            Log.d("Log","OnRecord is " + onRecord);
            if (onRecord == false) {
                recordButton.setImageResource(R.drawable.recbutton);
                onRecord = true;
                recordStatus.setText("Recording");
                resetScore = true;
                textView.setText(R.string.main_score);
                startRecordTime = System.currentTimeMillis();
                restStartTime = startRecordTime;
                notesAndRest = "";
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
                notesAndRest = notesAndRest + "\t" + "Rest";
                lengthOfNotesAndRest = lengthOfNotesAndRest + "\t" + rest;
                Log.d("RecordingLog", "The Record Time is " + recordTime);
                textView.setText(notesAndRest + "\n" + lengthOfNotesAndRest);
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
            if (keyNoteMap != null && keyNoteMap.get(v.getId()) != null) {
                textView.append( "Rest " + keyNoteMap.get(v.getId()) + " ");
            }
        } else {
            Log.d("Log", "This is not a button you clicked");
        }
    }

    double noteStartTime;
    double noteEndTime;
    double rest;
    double restStartTime;
    double restEndTime;
    double elapse;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v instanceof ImageButton) {

            int noteID = 1;
            switch (v.getId()) {
                case R.id.cnatural:
                    noteID = 1;                break;
                case R.id.csharp:
                    noteID = 2;                break;
                case R.id.dnatural:
                    noteID = 3;                break;
                case R.id.eflat:
                    noteID = 4;                break;
                case R.id.enatural:
                    noteID = 5;                break;
                case R.id.fnatural:
                    noteID = 6;                break;
                case R.id.fsharp:
                    noteID = 7;                break;
                case R.id.gnatural:
                    noteID = 8;                break;
                case R.id.gsharp:
                    noteID = 9;                break;
                case R.id.anatural:
                    noteID = 10;                break;
                case R.id.bflat:
                    noteID = 11;                break;
                case R.id.bnatural:
                    noteID = 12;                break;
                case R.id.highcnatural:
                    noteID = 13;                break;
                default:
                    break;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {                                     //just press the key
                if (onRecord == true) {

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
                    notesAndRest = notesAndRest + "\t" + "Rest";
                    lengthOfNotesAndRest = lengthOfNotesAndRest + "\t" + rest;
                } else {
                    //                                                                              //play the sound only
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (onRecord == true) {


                    audioThreads[noteID].stopPlaying();

                    onHold = false;
                    noteEndTime = System.currentTimeMillis();
                    restStartTime = noteEndTime;
                    elapse = noteEndTime - noteStartTime;
                    elapse /= 1000;
                    //Log.d("TouchLog", "The elapse is " + elapse);
                    noteBeats = elapse/secondsPerBeat;
                    Log.d("BeatsLog", "There are " + noteBeats + " beats in the note");
                    notesAndRest = notesAndRest + "\t" + keyNoteMap.get((v).getId());
                    lengthOfNotesAndRest = lengthOfNotesAndRest + "\t" + noteBeats;
                }
            }
        }

        Log.d("The score should be", notesAndRest);
        Log.d("The length of it is", lengthOfNotesAndRest);
        return super.onTouchEvent(event);
    }

    public void changeTempo(View view) {

        if (timeSignatureButton.getText().equals("60")) {
            timeSignatureButton.setText("90");
        } else if (timeSignatureButton.getText().equals("90")) {
            timeSignatureButton.setText("120");
        } else if (timeSignatureButton.getText().equals("120")) {
            timeSignatureButton.setText("60");
        }

        metronome.changeTempo();
        Log.d("Tempo Log", "The current Tempo is " + timeSignatureButton.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AudioThread thread: audioThreads) {
            try {
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            thread = null;
        }
    }

    public void startMetronome(View view) {
        metronome.start();
    }
}