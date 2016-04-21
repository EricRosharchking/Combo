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
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liyuan.projectcombo.kiv.MyAdapter;
import com.facebook.login.LoginManager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EditScoreActivity extends ActionBarActivity implements OnClickListener, View.OnTouchListener, NumberPicker.OnValueChangeListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    Score score;
    double[] lengths;
    Metronome metronome;
    ScoreFile scoreFile;
    PlayBack playBackTrack;
    ImageButton backspace;
    int[] numericNotes;
    private AudioThread[] audioThreads = new AudioThread[14];
    final Note[] Notes = new Note[13];
    DisplayThread displayThread;
    MyAdapter myAdapter;

    Button btnLogout;
    Button b11, b12, b13, b14, b15, b16, b17, b18, b19, b21, b22, b23, b24, b25, b26, b27, b28, b29, bbutton0, b31, b32, b33, b34, b35, b36, b37, b38, b39, buttonbar, b41, b42, b43, b44, b45, b46, b47, b48, b49, buttonhython, b51, b52, b53, b54, b55, b56, b57, b58, b59, buttonsharp, b61, b62, b63, b64, b65, b66, b67, b68, b69, buttondot, b71, b72, b73, b74, b75, b76, b77, b78, b79, buttonspace;
    TextView scores;
    private Spinner spinner;
    private SeekBar tempoSeekBar;

    private final String underline = "<sub>\u0332</sub>";
    private final String underlinenosub = "\u0332";
    private final String double_underline = "<sub>\u0333</sub>";
    private final String double_underlinenosub = "\u0333";
    private final String bullet = "&#8226\n";
    private final String dot_above = "<sup>\u0307</sup>";
    private final String dot_below = "<sub>\u0323</sub>";
    private String userName;
    private String userEmail;

    private ListView mDrawerList2;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private Toolbar toolbar;

    List<Integer> noteList;
    List<Double> lengthList;
    String notesAndRest;
    String lengthOfNotesAndRest;
    String userScoreName;

    boolean withMetronome;
    boolean metronomeRunning;
    boolean opened;
    boolean isOpened;

    double secondsPerBeat;
    double beatLength;

    int tempo;
    int timeSig;
    int lastNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_editscorewithnewkeyboard);

            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            setSupportActionBar(toolbar);

            mDrawerList2 = (ListView) findViewById(R.id.navigationList_left);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

            userScoreName = (String) getIntent().getSerializableExtra("userScore");

            if (userScoreName != null) {
                mActivityTitle = userScoreName;
            } else {
                mActivityTitle = getTitle().toString();
            }

            addDrawerItems2();
            setupDrawer();

            getSupportActionBar().setTitle(mActivityTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
//            super.onCreate(savedInstanceState);

            LayoutInflater inflater = getLayoutInflater();

            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            //retrieve scores (in String) from MainActivity
            Intent intent = getIntent();
            Score score = (Score) intent.getSerializableExtra("score");
            numericNotes = (int[]) intent.getSerializableExtra("notes");
            lengths = (double[]) intent.getSerializableExtra("lengths");
            tempo = 60;
            String timeSignature = "4/4";
            if (score != null) {
                numericNotes = score.getScore();
                lengths = score.getLengths();
                timeSignature = score.getTimeSignature();
                tempo = score.getTempo();
            }
            ((TextView) findViewById(R.id.seekbarvalue)).setText(String.valueOf(tempo));

            String rawScores = extractScore(numericNotes, lengths, timeSignature);
//            Log.d("rawScores", "The raw scores: " + rawScores);

            scores = (TextView) findViewById(R.id.score);
            scores.setMovementMethod(new ScrollingMovementMethod());

            scores.setText(Html.fromHtml(rawScores));

//            Log.d("EditLog", "" + getString(R.string.example).length() + "lalala" + getString(R.string.example).substring(3, 8));

            scores.setInputType(InputType.TYPE_NULL);
            scores.setFocusable(false);

            if (android.os.Build.VERSION.SDK_INT >= 11) {
                scores.setRawInputType(InputType.TYPE_CLASS_TEXT);
                scores.setTextIsSelectable(true);
            }

            userEmail = intent.getStringExtra("userEmail");
            userName = intent.getStringExtra("userName");
//            Log.d("UserName", userName);
//            Log.d("userEmail", userEmail);
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

            scoreFile = new ScoreFile();
            notesAndRest = "";
            lengthOfNotesAndRest = notesAndRest;
            metronomeRunning = false;
            metronome = new Metronome(60);
            opened = false;

            noteList = new ArrayList<Integer>();
            lengthList = new ArrayList<Double>();

            for (int i = 1; i < audioThreads.length; i++) {
                audioThreads[i] = new AudioThread(Notes[i - 1]);
            }

            displayThread = new DisplayThread();

            b11 = (Button) findViewById(R.id.button11);
            b12 = (Button) findViewById(R.id.button12);
            b13 = (Button) findViewById(R.id.button13);
            b14 = (Button) findViewById(R.id.button14);
            b15 = (Button) findViewById(R.id.button15);
            b16 = (Button) findViewById(R.id.button16);
            b17 = (Button) findViewById(R.id.button17);
            b18 = (Button) findViewById(R.id.button18);
            b19 = (Button) findViewById(R.id.button19);
            backspace = (ImageButton) findViewById(R.id.buttonbackspace);

            b21 = (Button) findViewById(R.id.button21);
            b22 = (Button) findViewById(R.id.button22);
            b23 = (Button) findViewById(R.id.button23);
            b24 = (Button) findViewById(R.id.button24);
            b25 = (Button) findViewById(R.id.button25);
            b26 = (Button) findViewById(R.id.button26);
            b27 = (Button) findViewById(R.id.button27);
            b28 = (Button) findViewById(R.id.button28);
            b29 = (Button) findViewById(R.id.button29);
            bbutton0 = (Button) findViewById(R.id.button0);

            b31 = (Button) findViewById(R.id.button31);
            b32 = (Button) findViewById(R.id.button32);
            b33 = (Button) findViewById(R.id.button33);
            b34 = (Button) findViewById(R.id.button34);
            b35 = (Button) findViewById(R.id.button35);
            b36 = (Button) findViewById(R.id.button36);
            b37 = (Button) findViewById(R.id.button37);
            b38 = (Button) findViewById(R.id.button38);
            b39 = (Button) findViewById(R.id.button39);
            buttonbar = (Button) findViewById(R.id.buttonbar);

            b41 = (Button) findViewById(R.id.button41);
            b42 = (Button) findViewById(R.id.button42);
            b43 = (Button) findViewById(R.id.button43);
            b44 = (Button) findViewById(R.id.button44);
            b45 = (Button) findViewById(R.id.button45);
            b46 = (Button) findViewById(R.id.button46);
            b47 = (Button) findViewById(R.id.button47);
            b48 = (Button) findViewById(R.id.button48);
            b49 = (Button) findViewById(R.id.button49);
            buttonhython = (Button) findViewById(R.id.buttonhython);

            b51 = (Button) findViewById(R.id.button51);
            b52 = (Button) findViewById(R.id.button52);
            b53 = (Button) findViewById(R.id.button53);
            b54 = (Button) findViewById(R.id.button54);
            b55 = (Button) findViewById(R.id.button55);
            b56 = (Button) findViewById(R.id.button56);
            b57 = (Button) findViewById(R.id.button57);
            b58 = (Button) findViewById(R.id.button58);
            b59 = (Button) findViewById(R.id.button59);
            buttonsharp = (Button) findViewById(R.id.buttonsharp);

            b61 = (Button) findViewById(R.id.button61);
            b62 = (Button) findViewById(R.id.button62);
            b63 = (Button) findViewById(R.id.button63);
            b64 = (Button) findViewById(R.id.button64);
            b65 = (Button) findViewById(R.id.button65);
            b66 = (Button) findViewById(R.id.button66);
            b67 = (Button) findViewById(R.id.button67);
            b68 = (Button) findViewById(R.id.button68);
            b69 = (Button) findViewById(R.id.button69);
            buttondot = (Button) findViewById(R.id.buttondot);

            b71 = (Button) findViewById(R.id.button71);
            b72 = (Button) findViewById(R.id.button72);
            b73 = (Button) findViewById(R.id.button73);
            b74 = (Button) findViewById(R.id.button74);
            b75 = (Button) findViewById(R.id.button75);
            b76 = (Button) findViewById(R.id.button76);
            b77 = (Button) findViewById(R.id.button77);
            b78 = (Button) findViewById(R.id.button78);
            b79 = (Button) findViewById(R.id.button79);
            buttonspace = (Button) findViewById(R.id.buttonspace);

            b11.setText(Html.fromHtml("1"));
            b12.setText(Html.fromHtml("1" + underline));
            b13.setText(Html.fromHtml("1" + double_underline));
            b14.setText(Html.fromHtml("1" + dot_above));
            b15.setText(Html.fromHtml("1" + dot_above + underlinenosub));
            b16.setText(Html.fromHtml("1" + dot_above + double_underlinenosub));
            b17.setText(Html.fromHtml("1" + dot_below));
            b18.setText(Html.fromHtml("1" + dot_below + underlinenosub));
            b19.setText(Html.fromHtml("1" + dot_below + double_underlinenosub));

            b21.setText(Html.fromHtml("2"));
            b22.setText(Html.fromHtml("2" + underline));
            b23.setText(Html.fromHtml("2" + double_underline));
            b24.setText(Html.fromHtml("2" + dot_above));
            b25.setText(Html.fromHtml("2" + dot_above + underlinenosub));
            b26.setText(Html.fromHtml("2" + dot_above + double_underlinenosub));
            b27.setText(Html.fromHtml("2" + dot_below));
            b28.setText(Html.fromHtml("2" + dot_below + underlinenosub));
            b29.setText(Html.fromHtml("2" + dot_below + double_underlinenosub));
            bbutton0.setText(Html.fromHtml(" 0 "));

            b31.setText(Html.fromHtml("3"));
            b32.setText(Html.fromHtml("3" + underline));
            b33.setText(Html.fromHtml("3" + double_underline));
            b34.setText(Html.fromHtml("3" + dot_above));
            b35.setText(Html.fromHtml("3" + dot_above + underlinenosub));
            b36.setText(Html.fromHtml("3" + dot_above + double_underlinenosub));
            b37.setText(Html.fromHtml("3" + dot_below));
            b38.setText(Html.fromHtml("3" + dot_below + underlinenosub));
            b39.setText(Html.fromHtml("3" + dot_below + double_underlinenosub));
            buttonbar.setText(Html.fromHtml(" | "));

            b41.setText(Html.fromHtml("4"));
            b42.setText(Html.fromHtml("4" + underline));
            b43.setText(Html.fromHtml("4" + double_underline));
            b44.setText(Html.fromHtml("4" + dot_above));
            b45.setText(Html.fromHtml("4" + dot_above + underlinenosub));
            b46.setText(Html.fromHtml("4" + dot_above + double_underlinenosub));
            b47.setText(Html.fromHtml("4" + dot_below));
            b48.setText(Html.fromHtml("4" + dot_below + underlinenosub));
            b49.setText(Html.fromHtml("4" + dot_below + double_underlinenosub));
            buttonhython.setText(Html.fromHtml(" - "));

            b51.setText(Html.fromHtml("5"));
            b52.setText(Html.fromHtml("5" + underline));
            b53.setText(Html.fromHtml("5" + double_underline));
            b54.setText(Html.fromHtml("5" + dot_above));
            b55.setText(Html.fromHtml("5" + dot_above + underlinenosub));
            b56.setText(Html.fromHtml("5" + dot_above + double_underlinenosub));
            b57.setText(Html.fromHtml("5" + dot_below));
            b58.setText(Html.fromHtml("5" + dot_below + underlinenosub));
            b59.setText(Html.fromHtml("5" + dot_below + double_underlinenosub));
            buttonsharp.setText(Html.fromHtml("#"));

            b61.setText(Html.fromHtml("6"));
            b62.setText(Html.fromHtml("6" + underline));
            b63.setText(Html.fromHtml("6" + double_underline));
            b64.setText(Html.fromHtml("6" + dot_above));
            b65.setText(Html.fromHtml("6" + dot_above + underlinenosub));
            b66.setText(Html.fromHtml("6" + dot_above + double_underlinenosub));
            b67.setText(Html.fromHtml("6" + dot_below));
            b68.setText(Html.fromHtml("6" + dot_below + underlinenosub));
            b69.setText(Html.fromHtml("6" + dot_below + double_underlinenosub));
            buttondot.setText(Html.fromHtml(bullet));

            b71.setText(Html.fromHtml("7"));
            b72.setText(Html.fromHtml("7" + underline));
            b73.setText(Html.fromHtml("7" + double_underline));
            b74.setText(Html.fromHtml("7" + dot_above));
            b75.setText(Html.fromHtml("7" + dot_above + underlinenosub));
            b76.setText(Html.fromHtml("7" + dot_above + double_underlinenosub));
            b77.setText(Html.fromHtml("7" + dot_below));
            b78.setText(Html.fromHtml("7" + dot_below + underlinenosub));
            b79.setText(Html.fromHtml("7" + dot_below + double_underlinenosub));
            buttonspace.setText(Html.fromHtml(" "));


            backspace.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    scores.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));

                }
            });

            b11.setOnClickListener(this);
            b12.setOnClickListener(this);
            b13.setOnClickListener(this);
            b14.setOnClickListener(this);
            b15.setOnClickListener(this);
            b16.setOnClickListener(this);
            b17.setOnClickListener(this);
            b18.setOnClickListener(this);
            b19.setOnClickListener(this);

            b21.setOnClickListener(this);
            b22.setOnClickListener(this);
            b23.setOnClickListener(this);
            b24.setOnClickListener(this);
            b25.setOnClickListener(this);
            b26.setOnClickListener(this);
            b27.setOnClickListener(this);
            b28.setOnClickListener(this);
            b29.setOnClickListener(this);
            bbutton0.setOnClickListener(this);
            b31.setOnClickListener(this);
            b32.setOnClickListener(this);
            b33.setOnClickListener(this);
            b34.setOnClickListener(this);
            b35.setOnClickListener(this);
            b36.setOnClickListener(this);
            b37.setOnClickListener(this);
            b38.setOnClickListener(this);
            b39.setOnClickListener(this);
            buttonbar.setOnClickListener(this);
            b41.setOnClickListener(this);
            b42.setOnClickListener(this);
            b43.setOnClickListener(this);
            b44.setOnClickListener(this);
            b45.setOnClickListener(this);
            b46.setOnClickListener(this);
            b47.setOnClickListener(this);
            b48.setOnClickListener(this);
            b49.setOnClickListener(this);
            buttonhython.setOnClickListener(this);
            b51.setOnClickListener(this);
            b52.setOnClickListener(this);
            b53.setOnClickListener(this);
            b54.setOnClickListener(this);
            b55.setOnClickListener(this);
            b56.setOnClickListener(this);
            b57.setOnClickListener(this);
            b58.setOnClickListener(this);
            b59.setOnClickListener(this);
            buttonsharp.setOnClickListener(this);
            b61.setOnClickListener(this);
            b62.setOnClickListener(this);
            b63.setOnClickListener(this);
            b64.setOnClickListener(this);
            b65.setOnClickListener(this);
            b66.setOnClickListener(this);
            b67.setOnClickListener(this);
            b68.setOnClickListener(this);
            b69.setOnClickListener(this);
            buttondot.setOnClickListener(this);
            b71.setOnClickListener(this);
            b72.setOnClickListener(this);
            b73.setOnClickListener(this);
            b74.setOnClickListener(this);
            b75.setOnClickListener(this);
            b76.setOnClickListener(this);
            b77.setOnClickListener(this);
            b78.setOnClickListener(this);
            b79.setOnClickListener(this);
            buttonspace.setOnClickListener(this);

            tempoSeekBar = (SeekBar) findViewById(R.id.tempoSeekBar);
            tempoSeekBar.setOnSeekBarChangeListener(this);
            btnLogout = (Button) findViewById(R.id.btnLogout);
            btnLogout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    logout();
                }
            });
            spinner = (Spinner) findViewById(R.id.time_signature);
//            Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.time_signature_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            switch (timeSignature) {
                case "4/4":
                    spinner.setSelection(0);
                    break;
                case "3/4":
                    spinner.setSelection(1);
                    break;
            }
            scoreFile = new ScoreFile();
            withMetronome = false;
            opened = false;
            beatLength = 1.0;


        } catch (NumberFormatException e) {
            tempo = 60;
        } finally {
            secondsPerBeat = 60.0 / tempo;
        }


    }

    private void addDrawerItems2() {
        int[] images = {R.drawable.createnewsong, R.drawable.save, R.drawable.saveas, R.drawable.addlyrics, R.drawable.recordlists, R.drawable.share};
        String[] tool_list = this.getResources().getStringArray(R.array.navigation_toolbox_EditScoreActivity);
        myAdapter = new MyAdapter(this, userEmail, userName, tool_list, images, -1);
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
                    //saveasnew
                    case 3:
                        saveAs();
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
//                Toast.makeText(EditScoreActivity.this, "position is " + position + ", id is " + id + " view id is " + view.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exportToPDF() {
        Intent intent = new Intent(this, CreateScoreActivity.class);
        intent.putExtra("action", 1);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
        finish();
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
                if (userScoreName != null) {
                    mActivityTitle = userScoreName;
                } else {
                    mActivityTitle = getTitle().toString();
                }
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
//        Log.i("Log@Main398", "position is " + position + " and id is " + id);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {


//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(scores.getWindowToken(), 0);

        if (v == scores) {
            hideDefaultKeyboard();
        }

        return true;
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
        switch (v.getId()) {

            case R.id.button11:
                t = "1 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button12:
                t = "" + Html.fromHtml("1" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button13:
                t = "" + Html.fromHtml("1" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button14:
                t = "" + Html.fromHtml("1" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button15:
                t = "" + Html.fromHtml("1" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button16:
                t = "" + Html.fromHtml("1" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button17:
                t = "" + Html.fromHtml("1" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button18:
                t = "" + Html.fromHtml("1" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button19:
                t = "" + Html.fromHtml("1" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button21:
                t = "2 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button22:
                t = "" + Html.fromHtml("2" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button23:
                t = "" + Html.fromHtml("2" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button24:
                t = "" + Html.fromHtml("2" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button25:
                t = "" + Html.fromHtml("2" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button26:
                t = "" + Html.fromHtml("2" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button27:
                t = "" + Html.fromHtml("2" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button28:
                t = "" + Html.fromHtml("2" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button29:
                t = "" + Html.fromHtml("2" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button0:
                t = " 0 ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button31:
                t = "3 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button32:
                t = "" + Html.fromHtml("3" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button33:
                t = "" + Html.fromHtml("3" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button34:
                t = "" + Html.fromHtml("3" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button35:
                t = "" + Html.fromHtml("3" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button36:
                t = "" + Html.fromHtml("3" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button37:
                t = "" + Html.fromHtml("3" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button38:
                t = "" + Html.fromHtml("3" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button39:
                t = "" + Html.fromHtml("3" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.buttonbar:
                t = " | ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button41:
                t = "4 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button42:
                t = "" + Html.fromHtml("4" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button43:
                t = "" + Html.fromHtml("4" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button44:
                t = "" + Html.fromHtml("4" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button45:
                t = "" + Html.fromHtml("4" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button46:
                t = "" + Html.fromHtml("4" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button47:
                t = "" + Html.fromHtml("4" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button48:
                t = "" + Html.fromHtml("4" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button49:
                t = "" + Html.fromHtml("4" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.buttonhython:
                t = "- ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button51:
                t = "5 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button52:
                t = "" + Html.fromHtml("5" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button53:
                t = "" + Html.fromHtml("5" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button54:
                t = "" + Html.fromHtml("5" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button55:
                t = "" + Html.fromHtml("5" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button56:
                t = "" + Html.fromHtml("5" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button57:
                t = "" + Html.fromHtml("5" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button58:
                t = "" + Html.fromHtml("5" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button59:
                t = "" + Html.fromHtml("5" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.buttonsharp:
                t = "# ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button61:
                t = "6 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button62:
                t = "" + Html.fromHtml("6" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button63:
                t = "" + Html.fromHtml("6" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button64:
                t = "" + Html.fromHtml("6" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button65:
                t = "" + Html.fromHtml("6" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button66:
                t = "" + Html.fromHtml("6" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button67:
                t = "" + Html.fromHtml("6" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button68:
                t = "" + Html.fromHtml("6" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button69:
                t = "" + Html.fromHtml("6" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.buttondot:
                t = "" + Html.fromHtml(bullet);
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button71:
                t = "7 ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button72:
                t = "" + Html.fromHtml("7" + underline) + " ";
                // int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button73:
                t = "" + Html.fromHtml("7" + double_underline) + " ";
                //int index = getEditSelection();// The location of the cursor
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;

            case R.id.button74:
                t = "" + Html.fromHtml("7" + dot_above) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button75:
                t = "" + Html.fromHtml("7" + dot_above + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button76:
                t = "" + Html.fromHtml("7" + dot_above + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button77:
                t = "" + Html.fromHtml("7" + dot_below) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button78:
                t = "" + Html.fromHtml("7" + dot_below + underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.button79:
                t = "" + Html.fromHtml("7" + dot_below + double_underlinenosub) + " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
                    scores.append(t);

                } else {
                    scores.getEditableText().insert(index, t);// Insert text cursor position
                }
                break;
            case R.id.buttonspace:
                t = " ";
                if (index < 0 || index >= getEditTextViewString().length()) {
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
            Score thisScore = (Score) getIntent().getSerializableExtra("Score2");
//            Log.d("Log@617", "Score is null? " + (thisScore == null));
            if (thisScore != null && thisScore.getScore() != null) {
//                Log.d("Log@Main619", "Score is " + thisScore.getScore().length);
                scores.setText(Html.fromHtml(extractScore(thisScore.getScore(), thisScore.getLengths(), thisScore.getTimeSignature())).toString());
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

        } finally {
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
                metronome.changeTempo(tempo);
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

    public void playBack(View view) {
//        Log.d("PlayBack Log", "PlayBack Entered");
//        Log.d("PlayBack Log", "Length of notesAndRest is " + notesAndRest.length() + "\t" + "Length of lengths is " + lengthOfNotesAndRest.length());
//        Log.d("PlayBack Log@601", "Length of split is" + notesAndRest.split(" ").length + " And Length is " + lengthOfNotesAndRest.split(" ").length);
        if (playBackTrack == null || playBackTrack.getState() == Thread.State.NEW || playBackTrack.getState() == Thread.State.TERMINATED) {

            if (numericNotes.length == lengths.length) {

                if (playBackTrack != null) {
                    lastNote = playBackTrack.getLast();
                }
                getScoreFromText(scores.getEditableText().toString());

                numericNotes = prepareScore();
                lengths = prepareLengths();

                playBackTrack = new PlayBack(numericNotes, lengths, lastNote, tempo);
//                Log.d("PlayBack Log", "PlayBack initialised");
                if (withMetronome) startMetronome(null);
                playBackTrack.start();

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
            try {
                playBackTrack.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Log.i("Log@Main431", "pausePlay clicked" + lastNote);
        }
    }

    public void stopPlay(View view) {
        if (playBackTrack != null) {
            playBackTrack.stopPlaying();
            lastNote = playBackTrack.getLast();
            try {
                playBackTrack.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        Log.i("Log@Main431", "pausePlay clicked" + lastNote);
    }

    private int[] prepareScore() {
        int[] array;
        if (!noteList.isEmpty()) {
            array = new int[noteList.size()];
            for (int i = 0; i < noteList.size(); i++) {
                array[i] = noteList.get(i);
            }
        } else {
            array = new int[]{1, 3, 5};
        }

        return array;
    }

    private void getScoreFromText(String scoreText) {
        noteList.clear();
        lengthList.clear();
        String[] thisArray = scoreText.split("\\s+");
        for (String str : thisArray) {
            for (int i = 0; i < str.length(); i++) {
                String s = str.substring(i, i + 1);
                if (s.matches("[01234567]")) {
                    int note = Integer.valueOf(s);
                    switch (note) {
                        case 1:
                            break;
                        case 2:
                            note = 3;
                            break;
                        case 3:
                            note = 5;
                            break;
                        case 4:
                            note = 6;
                            break;
                        case 5:
                            note = 8;
                            break;
                        case 6:
                            note = 10;
                            break;
                        case 7:
                            note = 12;
                            break;
                        default:
                            break;
                    }
                    noteList.add(note);
                    lengthList.add(1.0);
                } else if (s.equals("\u0307")) {
                    noteList.set(noteList.size() - 1, noteList.get(noteList.size() - 1) * 14);
                } else if (s.equals("\u0323")) {
                    noteList.set(noteList.size() - 1, -noteList.get(noteList.size() - 1));
                } else if (s.equals("\u0332")) {
                    lengthList.set(lengthList.size() - 1, lengthList.get(lengthList.size() - 1) * 0.5);
                } else if (s.equals("\u0333")) {
                    lengthList.set(lengthList.size() - 1, lengthList.get(lengthList.size() - 1) * 0.25);
                } else if (s.equals("\u2022")) {
                    lengthList.set(lengthList.size() - 1, lengthList.get(lengthList.size() - 1) * 1.5);
                } else if (s.equals("-")) {
                    lengthList.set(lengthList.size() - 1, lengthList.get(lengthList.size() - 1) + 1);
                } else if (s.equals("#")) {
                    noteList.set(noteList.size() - 1, noteList.get(noteList.size() - 1) + 1);
                }
            }
        }
    }

    private double[] prepareLengths() {
        double[] array;
        if (!lengthList.isEmpty()) {
            array = new double[lengthList.size()];
            for (int i = 0; i < lengthList.size(); i++) {
                array[i] = lengthList.get(i);
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

    public void openOrNew() {
        Intent intent = new Intent(this, CreateScoreActivity.class);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
    }

    private void save() {

        Score thisScore = new Score();
        thisScore.setTempo(tempo);
        switch (timeSig) {
            case 3:
                thisScore.setTimeSignatureFT();
                break;
            case 4:
                thisScore.setTimeSignatureFF();
                break;
            default:
                break;
        }
        getScoreFromText(scores.getEditableText().toString());
        numericNotes = prepareScore();
        lengths = prepareLengths();
//        Log.i("Log@Main805", "numericNotes is null? " + (numericNotes == null));
//        Log.i("Log@Main806", "lengths is null?" + (lengths == null));
        thisScore.setScore(numericNotes, lengths);
        thisScore.setTitle(userScoreName);
        thisScore.setAuthor(userName);
        try {
            boolean test = scoreFile.save(thisScore);
            Toast.makeText(EditScoreActivity.this, "Score " + test + " Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAs() {
        Intent intent = new Intent(this, SaveActivity.class);
        Score thisScore = new Score();
        thisScore.setTempo(tempo);
        switch (timeSig) {
            case 3:
                thisScore.setTimeSignatureFT();
                break;
            case 4:
                thisScore.setTimeSignatureFF();
                break;
            default:
                break;
        }
        getScoreFromText(scores.getEditableText().toString());
        numericNotes = prepareScore();
        lengths = prepareLengths();
//        Log.i("Log@Main805", "numericNotes is null? " + (numericNotes == null));
//        Log.i("Log@Main806", "lengths is null?" + (lengths == null));
        thisScore.setScore(numericNotes, lengths);
        intent.putExtra("score", thisScore);
        intent.putExtra("ScoreFile", scoreFile);
        startActivity(intent);
    }

    private void addLyrics() {
        Intent i = new Intent(EditScoreActivity.this,
                AddLyricsActivity.class);
        //i.putExtra("scores", Html.fromHtml(displayThread.getDisplay() + "\u2225"));
        i.putExtra("userEmail", userEmail);
        i.putExtra("userName", userName);
        i.putExtra("scores", displayThread.getDisplay());
        startActivity(i);
        finish();
    }

    private void deleteAll() {
        Log.i("Log@Main835", "Delete Status is " + scoreFile.deleteAll());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
//                record();
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
                if (!withMetronome) {
                    withMetronome = true;
                    item.setIcon(R.drawable.metro_on);
                } else {
                    withMetronome = false;
                    item.setIcon(R.drawable.metro_off);
                    try {
                        metronome.stop();
                    } catch (Exception e) {

                    }
                    metronomeRunning = false;
                }
                break;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private int getTimeSignature() {
        if (new Integer(timeSig) == null) {
            timeSig = 4;
        }
        return timeSig;
    }


    @Override
    public void onBackPressed() {
        finish();
    }


    private static String[] split(String string) {
        char[] chars = string.toCharArray();
        String[] strings = new String[chars.length];
        for (int i = 0; i < chars.length; i++) {
            strings[i] = String.valueOf(chars[i]);
        }
        return strings;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
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
                        Intent intent = new Intent(EditScoreActivity.this, welcomePage.class);
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
