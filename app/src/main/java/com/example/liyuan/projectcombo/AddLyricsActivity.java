package com.example.liyuan.projectcombo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liyuan.projectcombo.kiv.MyAdapter;
import com.facebook.login.LoginManager;

import java.text.DateFormat;
import java.util.Date;

public class AddLyricsActivity extends ActionBarActivity implements NumberPicker.OnValueChangeListener, AdapterView.OnItemSelectedListener{

    TextView scores;
    EditText lyrics;
    Button btnLogout;

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
    private String userName;
    private String userEmail;
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
    int disabledID = 3;

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

            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;

            Intent intent = getIntent();
            Score score = (Score) intent.getSerializableExtra("score");
            int[] notes = (int[]) intent.getSerializableExtra("notes");
            double[] lengths = (double[]) intent.getSerializableExtra("lengths");
            if (score != null) {
                notes = score.getScore();
                lengths = score.getLengths();
            }
            String rawScores = extractScore(notes ,lengths);
//            Log.d("rawScores", "The raw scores: " + rawScores);
//            String rawScores = intent.getStringExtra("scores");
            Log.d("rawScores", "The raw scores: " + rawScores);

            scores = (TextView) findViewById(R.id.tvScores);
            lyrics = (EditText) findViewById(R.id.edLyrics);
            scores.setFocusable(false);
//            lyrics.setFocusable(false);
//            lyrics.setText(score.getLyrics());

            scores.setMovementMethod(new ScrollingMovementMethod());
            lyrics.setMovementMethod(new ScrollingMovementMethod());

            scores.setTextSize(18);

            scores.setText(Html.fromHtml(rawScores));

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

            userEmail = intent.getStringExtra("userEmail");
            userName = intent.getStringExtra("userName");
            View listHeaderView = inflater.inflate(R.layout.navigation_drawer_header, null, false);
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
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    logout();
                }
            });
            //TO DO: Implement saveAs

            SeekBar tempoSeekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
            tempoSeekBar.setEnabled(false);

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
        int[] images = {R.drawable.createnewsong ,R.drawable.save, R.drawable.edit, R.drawable.addlyrics, R.drawable.recordlists, R.drawable.share};
        String[] tool_list = this.getResources().getStringArray(R.array.navigation_toolbox);
        MyAdapter myAdapter = new MyAdapter(this, userEmail, userName, tool_list, images, disabledID);
        mDrawerList2.setAdapter(myAdapter);
        mDrawerList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 1:
                    finish();
                    break;
                case 2:
                    save();
                    break;
                case 3:
                    editScore();
                    break;
//                case 4:
//                    addLyrics();
//                    break;
                case 5:
                    openOrNew();
                    break;
                case 6:
                    exportToPDF();
                    break;
                }
//                Toast.makeText(AddLyricsActivity.this, "position is " + position + ", id is " + id + " view id is " + view.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToPDF() {
        Intent intent = new Intent(this, CreateScoreActivity.class);
        intent.putExtra("action", 1);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
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
    public void onBackPressed() {
        finish();
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


                playBackTrack = new PlayBack(numericNotes, lengths, lastNote, tempo);
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
        String t = "||";
        if (notes != null && lengths != null && notes.length == lengths.length) {
            /*for (int i = 0; i < notes.length; i++) {
                String thisNote = Integer.toString(notes[i]);
                double thisLength = lengths[i];
                int q = (int) Math.round(thisLength / 0.25);
                if (q == 0) {
                    q = 1;
                }
                switch (q) {
                    case 1:
                        thisNote += DOUBLE_UNDERLINE;
                        break;
                    case 2:
                        thisNote += UNDERLINE;
                        break;
                    case 3:
                        thisNote += UNDERLINE;
                        thisNote += BULLET;
                        break;
                    default:
                        break;
                }
                scoreString += thisNote;
            }*/

            double totalLengths = 0.0;
            int barCount = 1;
            int lineCount = 1;

            for (int i = 0; i < notes.length; i++) {
                int n = notes[i];
                double l = lengths[i];
//            if (l < 0.15)
//                continue;
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
                for (int j = 2; j < l; j++) {
                    t += " - ";
                }

                double r = l % 1;
                if (l > 2.15 && r < 0.85)
                    t += thisKey;

                if (r >= 0.85)
                    t += " - ";
                else if (r >= 0.69)
                    t += "\u0332\u2022 ";
                else if (r >= 0.4)
                    t += "\u0332 ";
                else if (r >= 0.15)
                    t += "\u0333 ";

                totalLengths += l;
/*                if (totalLengths > barCount * 4) {
                    t += "|";
                    barCount += 1;
                    if (barCount > lineCount * 3) {
                        t += "\n ";
                        t += "|";
                        lineCount++;
                    }
                }*/
            }
            t += "||";
        }

        ////TODO:
        return t.trim();
    }

    public void openOrNew() {
        Intent intent = new Intent(this, CreateScoreActivity.class);
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
        intent.putExtra("score", score);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
    }

    private void addLyrics() {
        Intent i = new Intent(AddLyricsActivity.this,
                AddLyricsActivity.class);
        //i.putExtra("scores", Html.fromHtml(displayThread.getDisplay() + "\u2225"));
        i.putExtra("scores", displayThread.getDisplay());
        startActivity(i);
        finish();
    }

    public void editScore() {
        Intent intent = new Intent(this, EditScoreActivity.class);
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
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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

                        //Saving the sharedpreferences
                        editor.commit();

                        //Starting login activity
                        Intent intent = new Intent(AddLyricsActivity.this, welcomePage.class);
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

}
