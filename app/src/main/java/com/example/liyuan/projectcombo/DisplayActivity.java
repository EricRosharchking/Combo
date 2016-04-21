package com.example.liyuan.projectcombo;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

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
        int tempoInt = 4;
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
            switch (score.getTimeSignature()) {
                case "3/4":
                    tempoInt = 3;
                    break;
                case "4/4":
                    tempoInt = 4;
                    break;
            }
        }

        if (notes != null && lengths != null) {
            if (notes.length != lengths.length) {
                finish();
            }
            String t = "<p>||";
            if (notes.length == lengths.length) {

                double totalLengths = 0.0;
                int barCount = 1;
                int lineCount = 1;

//                Log.i("Log@1257", "notes are " + Arrays.toString(notes));
//                Log.i("Log@Main1258", "lengths are " + Arrays.toString(lengths));

                for (int i = 0; i < notes.length; i++) {
                    int n = notes[i];
                    double l = lengths[i];
                    String thisKey = "  1";
                    switch (n) {
                        case 0:
                            thisKey = "  0";
                            break;
                        case -3:
                        case 3:
                        case 42:
                            thisKey = "  2";
                            break;
                        case -4:
                        case 4:
                        case 56:
                        case -5:
                        case 5:
                        case 70:
                            thisKey = "  3";
                            break;
                        case -6:
                        case 6:
                        case 84:
                        case -7:
                        case 7:
                        case 98:
                            thisKey = "  4";
                            break;
                        case -8:
                        case 8:
                        case 112:
                        case -9:
                        case 9:
                        case 126:
                            thisKey = "  5";
                            break;
                        case -10:
                        case 10:
                        case 140:
                            thisKey = "  6";
                            break;
                        case -11:
                        case 11:
                        case 154:
                        case -12:
                        case 12:
                        case 168:
                            thisKey = "  7";
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
                        if (totalLengths > barCount * tempoInt) {
                            while (totalLengths > barCount * tempoInt) {
                                double firstHalf = barCount * tempoInt - (totalLengths - l);
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
                                t += " |";
                                if ((barCount - 1) == lineCount * 4) {
                                    t += " </p>";
                                    t += "<p>|";
                                    lineCount++;
                                }
                                t += thisKey;
                                l -= firstHalf;
                            }
                            double secondHalf = totalLengths - tempoInt * barCount;
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

                        if (totalLengths == barCount * tempoInt) {
                            barCount++;
                            t += " |";
                            if ((barCount - 1) == lineCount * 4) {
                                t += " </p>";
                                t += "<p>|";
                                lineCount++;
                            }
                        }
                    }
                }
            }
            textView.setText(Html.fromHtml(t));
        }
        //textView.setText(Html.fromHtml(getString(R.string.example)));
//        Log.d("Log@DisplayActivity133", t);
        ////TODO: 把score变成两个TextView，一个只有数字，一个只有横线。use \n to separate the two
        ////TODO: behaviour of back button on screen will start a new MainActivity instead of resuming the old one
        if (score != null)
            export(score);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void export(Score thisScore) {
        int[] notes = thisScore.getScore();
        double[] lengths = thisScore.getLengths();
        ArrayList<String> pList = new ArrayList<>();
        int barCount = 1;
        int lineCount = 1;
        String paragraph = "||";
        double totalLength = 0.0;
        int tempo = 4;
        switch (thisScore.getTimeSignature()) {
            case "3/4":
                tempo = 3;
                break;
            case "4/4":
                tempo = 4;
                break;
        }

        for (int i = 0; i < notes.length; i++) {
            int n = notes[i];
            double l = lengths[i];
//            if (l < 0.15)
//                continue;
            String thisKey = "  1";
            switch (n) {
                case 0:
                    thisKey = "  0";
                    break;
                case -3:
                case 3:
                case 42:
                    thisKey = "  2";
                    break;
                case -4:
                case 4:
                case 56:
                case -5:
                case 5:
                case 70:
                    thisKey = "  3";
                    break;
                case -6:
                case 6:
                case 84:
                case -7:
                case 7:
                case 98:
                    thisKey = "  4";
                    break;
                case -8:
                case 8:
                case 112:
                case -9:
                case 9:
                case 126:
                    thisKey = "  5";
                    break;
                case -10:
                case 10:
                case 140:
                    thisKey = "  6";
                    break;
                case -11:
                case 11:
                case 154:
                case -12:
                case 12:
                case 168:
                    thisKey = "  7";
                    break;
                default:
                    break;
            }
//            Log.d("Log@DisplayActivity133", thisKey);
            if (n < 0) {
                thisKey += "&#x323;";
            } else if (n > 13) {
                thisKey += "<sup>&#x307;</sup>";
            } else {
                thisKey += "";
            }
            paragraph += thisKey;
            totalLength += l;
            if (l == 0) {
                paragraph = paragraph.substring(0, paragraph.length() - 1);
            } else {
                if (totalLength > barCount * tempo) {
                    while (totalLength > barCount * tempo) {
                        double firstHalf = barCount * tempo - (totalLength - l);
                        int count = (int) (firstHalf / 0.25);
                        int x = count / 4;
                        int y = count % 4;

                        for (int j = 1; j < firstHalf; j++) {
                            paragraph += " - ";
                        }

                        if (x > 0 && y > 0) {
                            paragraph += " ";
                            paragraph += thisKey;
                        }
                        if (y > 0) {
                            String key = paragraph.substring(paragraph.lastIndexOf(" ") + 1, paragraph.length());
                            paragraph = paragraph.substring(0, paragraph.lastIndexOf(" ") + 1);
                            switch (y) {
                                case 1:
                                    paragraph += "&#x333;";
                                    break;
                                case 2:
                                case 3:
                                    paragraph += "&#x332;";
                                    break;
                            }
                            paragraph += key;
                        }
                        switch (y) {
                            case 1:
                                paragraph += "&#x333;";
                                break;
                            case 2:
                                paragraph += "&#x332;";
                                break;
                            case 3:
                                paragraph += "&#x332;&#x2022;";
                                break;
                            default:
                                break;
                        }

                        barCount++;
                        paragraph += " | ";
                        if (lineCount * 4 == (barCount - 1)) {
                            pList.add(paragraph);
                            paragraph = "|";
                        }
                        paragraph += thisKey;
                        l -= firstHalf;
                    }
                    double secondHalf = totalLength - tempo * barCount;
                    int count = (int) (secondHalf / 0.25);
                    int x = count / 4;
                    int y = count % 4;

                    for (int j = 1; j < secondHalf; j++) {
                        paragraph += " - ";
                    }

                    if (x > 0 && y > 0) {
                        paragraph += " ";
                        paragraph += thisKey;
                    }

                    if (y > 0) {
                        String key = paragraph.substring(paragraph.lastIndexOf(" ") + 1, paragraph.length());
                        paragraph = paragraph.substring(0, paragraph.lastIndexOf(" ") + 1);
                        switch (y) {
                            case 1:
                                paragraph += "&#x333;";
                                break;
                            case 2:
                            case 3:
                                paragraph += "&#x332;";
                                break;
                        }
                        paragraph += key;
                    }
                    switch (y) {
                        case 1:
                            paragraph += "&#x333;";
                            break;
                        case 2:
                            paragraph += "&#x332;";
                            break;
                        case 3:
                            paragraph += "&#x332;&#x2022;";
                            break;
                        default:
                            break;
                    }
                }
                int count = (int) (l / 0.25);
                int x = count / 4;
                int y = count % 4;

                for (int j = 1; j < l; j++) {
                    paragraph += " - ";
                }

                if (x > 0 && y > 0) {
                    paragraph += " ";
                    paragraph += thisKey;
                }
                if (y > 0) {
                    String key = paragraph.substring(paragraph.lastIndexOf(" ") + 1, paragraph.length());
                    paragraph = paragraph.substring(0, paragraph.lastIndexOf(" ") + 1);
                    switch (y) {
                        case 1:
                            paragraph += "&#x333;";
                            break;
                        case 2:
                        case 3:
                            paragraph += "&#x332;";
                            break;
                    }
                    paragraph += key;
                }
                switch (y) {
                    case 1:
                        paragraph += "&#x333; ";
                        break;
                    case 2:
                        paragraph += "&#x332; ";
                        break;
                    case 3:
                        paragraph += "&#x332;&#x2022;";
                        break;
                    default:
                        break;
                }

                if (totalLength == barCount * tempo) {
                    barCount++;
                    paragraph += " |";
                    if (lineCount * 4 == (barCount - 1)) {
                        pList.add(paragraph);
                        paragraph = "|";
                    }
                }
            }
        }
        paragraph = paragraph.substring(0, paragraph.length() -1);
        paragraph += " ||";
        pList.add(paragraph);

        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/Cambo/";
        File file = new File(dir);
        Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        FileWriter fw = null;
        try {

            boolean result = file.mkdirs();
//            Toast.makeText(this, "mkdirs is " + result, Toast.LENGTH_LONG).show();
            file = new File(dir, thisScore.getTitle() + ".html");
            file.createNewFile();
            fw = new FileWriter(file, false);
            fw.write("<html><head>\n" + "<style>\n" + "body {width:21cm; height:29.7cm; align:center;}\n" +
                    "</style>\n" + "</head><body>");
            fw.write("<h1 style='text-align: center;'><font size='8'>" + thisScore.getTitle() + "</font></h1>\n");
            fw.write("<h2 style='text-align: left;'><font size='6'>1=" + thisScore.getKey() + "     " + thisScore.getTimeSignature() + "     " + thisScore.getTempo() + "</font></h2>\n");
            fw.write("<h2 style='text-align: right;'><font size='6'>" + thisScore.getAuthor() + "</font></h2>\n");
            for (String str : pList) {
                str = "<p style='text-aligh: justify;'><font size='6'>" + str + "</font></p>\n";
                fw.write(str);
//                Log.i("ExportLog", str);
            }
            fw.write("</body></html>");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fw.flush();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
