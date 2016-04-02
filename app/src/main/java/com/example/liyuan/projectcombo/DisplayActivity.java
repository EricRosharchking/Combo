package com.example.liyuan.projectcombo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DisplayActivity extends ActionBarActivity {
    Score score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        TextView textView = (TextView) findViewById(R.id.score_content);
        TextView scoreKey = (TextView) findViewById(R.id.score_key);
        TextView scoreTimeSig = (TextView) findViewById(R.id.score_timeSig);
        TextView scoreTempo = (TextView) findViewById(R.id.score_tempo);
        TextView scoreTitle = (TextView) findViewById(R.id.score_title);
        TextView scoreAuthor = (TextView) findViewById(R.id.score_author);
//        char[] t = {'1', '\u0332', '\u0020', '2', '\u0323', '|'};
        int[] notes = (int[]) getIntent().getSerializableExtra("notes");
        double[] lengths = (double[]) getIntent().getSerializableExtra("lengths");

        if (getIntent().getSerializableExtra("score") != null) {
            score = (Score) getIntent().getSerializableExtra("score");
            notes = score.getScore();
            lengths = score.getLengths();
            String key = score.getKey();
            scoreKey.setText(key);
            scoreTimeSig.setText(score.getTimeSignature());
            String tempo = "" + score.getTempo();
            scoreTempo.setText(tempo.trim());
            scoreTitle.setText(score.getTitle());
            scoreAuthor.setText(score.getAuthor());
        }

        if (notes != null && lengths != null) {
            if (notes.length != lengths.length) {
                finish();
            }
            String t = "|";
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
                    thisKey += "<sub>\u0323</sub>";
                } else if (n > 13) {
                    thisKey += "<sup>\u0307</sup>";
                } else {
                    thisKey += "";
                }
                t += thisKey;
                for (int j = 2; j < l; j++) {
                    t += "\u2010 ";
                }

                double r = l % 1;
                if (l > 2.15 && r < 0.85)
                    t += thisKey;

                if (r >= 0.85)
                    t += "\u2010 ";
                else if (r >= 0.69)
                    t += "<sub>\u0332</sub>\u2022 ";
                else if (r >= 0.4)
                    t += "<sub>\u0332</sub> ";
                else if (r >= 0.15)
                    t += "<sub>\u0333</sub> ";

                totalLengths += l;
                if (totalLengths > barCount * 4) {
                    t += "|";
                    barCount += 1;
                    if (barCount > lineCount * 3) {
                        t += "\n ";
                        t += "|";
                        lineCount++;
                    }
                }
            }
            t += "\u2225";
            textView.setText(Html.fromHtml(t));
        }
        //textView.setText(Html.fromHtml(getString(R.string.example)));
//        Log.d("Log@DisplayActivity133", t);
        ////TODO: 把score变成两个TextView，一个只有数字，一个只有横线。use \n to separate the two
        ////TODO: behaviour of back button on screen will start a new MainActivity instead of resuming the old one
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
