package com.example.liyuan.projectcombo;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

/**
 * Created by Liyuan on 11/1/2015.
 */
public class DisplayThread extends Thread {
    private long startTime;
    private int tempo;
    private int timeSignature;
    boolean isRunning;
    private String display;
    private String archived;
    private int key;
    private int lastKey;
    private int barTime;
    private int displayTime;
    private int octave;
    private int quarterBeat;
    private int noteTime;
    private int lastNoteTime;
    private double secondsPerBeat;

    private final String underline = "<sub>\u0332</sub>";
    private final String double_underline = "<sub>\u0333</sub>";
    private final String curve = "<sup>\u0361</sup>";
    private final String bullet = "\u2022";
    private final String dot_above = "<sub>\u0307</sub>";
    private final String dot_below = "<sub>\u0323</sub>";

    public DisplayThread() {
        timeSignature = 4;
        tempo = 60;
        display = "";
        archived = display;
        isRunning = true;
        key = 0;
        lastKey = -1;
        octave = 4;
    }

    public void run() {
//        String strUtf8 = "&#FF0D";
//        String strChinese = null;
//
//        try {
//            strChinese = new String(strUtf8.getBytes("UTF-8"),  "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//
//            strChinese = "decode error";
//        }


        secondsPerBeat = 60.0 / tempo;
        barTime = (int) (timeSignature * 1000 * secondsPerBeat);
        displayTime = barTime * 4;
        quarterBeat = (int) (1000 * secondsPerBeat / 4);

        startTime = System.currentTimeMillis();
        Log.d("DisplayThread Log", "The start Time is " + startTime);
        long elapsedTime = System.currentTimeMillis() - startTime;
        long count = elapsedTime / quarterBeat;
        int displayCount = 1;
        int barCount = 1;
        while (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime;

            String addon = "";
            if (octave == 3) {
                addon = dot_below;
            } else if (octave == 5) {
                addon = dot_above;
            }
            if (key == 0) {
                addon = "";
            }
            if ((elapsedTime % quarterBeat) == 0 && elapsedTime > count * quarterBeat && (elapsedTime / quarterBeat) > 0) {
                Log.d("Log@DisplayThread72", "Octave is " + octave);
                if (lastKey == key) {
//                    display = display + "⏜";//"\u23DC"和"⏜"textview都无法显示
//                    display = display +"-";//短破折号
                    display = display+" \u2010 ";//长破折号
//                    display = display+"\uFF0D\n";//长破折号怪怪的
//                    display = display+"\u2040\n";//tie是短的
                } else {
//                    String underline = "<u>" + key + "&#8226\n </u>";//"<u>"underline
//                    String underline = lastKey+"<sup>\u0361</sup><sub><big>\u0333</big></sub>" + key+"<sub><big>\u0333</big></sub>" +"&#8226\n";//bullet point
//                    String addon = lastKey+curve+underline+key+double_underline+bullet+lastKey+dot_below;//testing
                    lastKey = key;
                    addon = lastKey + addon;
                    display += addon;
                    Log.d("Log@DisplayThread89", "Addon is " + addon);
                    //display += addon;
                }
                count = elapsedTime / quarterBeat;
                int x = (int)count / 4;
                int y = (int)count % 4;

//                for (int i = 1; i <= x; i ++) {
//                    display += "-";
//                }


                switch (y) {
                    case 1:
                        display += double_underline;
                        break;
                    case 2:
                        display += underline;
                        break;
                    case 3:
                        display += underline;
                        display += bullet;
                        break;
                    default:
                        break;
                }

//                  display = Html.fromHtml(display).toString();
//                Log.d("DisplayThread Log", "The current system time is " + System.currentTimeMillis());
//                Log.d("DisplayThread Log", "The elapsed time is " + elapsedTime);
//                Log.d("DisplayThread Log", "The elapsed 250 milliseconds period is" + count);
//                Log.d("DisplayThread Log", "The display is " + display);
                if (elapsedTime >= barTime * barCount) {
                    display = display + "|";
                    barCount ++;
                    if (elapsedTime >= displayTime * displayCount) {
                        displayCount ++;
                        archived += display;
                        display = "";
                    }
                }
//                Log.d("Debug Log", "TimeSignature is " + timeSignature);
//                Log.d("Debug Log", "BarTime is " + barTime);
//                Log.d("Debug Log", "DisplayTime is " + displayTime);
            }
        }
        Log.d("DisplayThread Log", "Length of Archived is " + archived.length());
        Log.d("DisplayThread Log", "Length of Archived after adding Display is " + archived.length());
        Log.d("DisplayThread Log", "Archived after adding Display is " + archived);
    }

    public String getDisplay() {
        return display;
    }

    public String getArchived() {
        return archived;
    }

    public void stopThread() {
        isRunning = false;
        Log.d("DisplayThread Log", "The elapsed time is " + (System.currentTimeMillis() - startTime));
        Log.d("DisplayThread Log", "The length of archived is " + archived.length());
        Log.d("DisplayThread Log", "The length of display is " + display.length());
        Log.d("DisplayThread Log", "Display is " + display);
        Log.d("DisplayThread Log", "Archived is " + archived);

        archived += display;
    }

    public void update(int strike) {
        int thisKey = 1;
        switch (strike) {
            case 0:
                thisKey = 0;
                break;
            case -3:
            case 3:
            case 42:
                thisKey = 2;
                break;
            case -4:
            case 4:
            case 56:
            case -5:
            case 5:
            case 70:
                thisKey = 3;
                break;
            case -6:
            case 6:
            case 84:
            case -7:
            case 7:
            case 98:
                thisKey = 4;
                break;
            case -8:
            case 8:
            case 112:
            case -9:
            case 9:
            case 126:
                thisKey = 5;
                break;
            case -10:
            case 10:
            case 140:
                thisKey = 6;
                break;
            case -11:
            case 11:
            case 154:
            case -12:
            case 12:
            case 168:
                thisKey = 7;
                break;
            default:
                break;
        }
        key = thisKey;
    }

    public void setTimeSignature(int timeSignature) {
        this.timeSignature = timeSignature;
        //barTime = timeSignature * 1000;
        //displayTime = barTime * 4;
//        Log.d("DisplayThread Log", "The current timeSignature is " + timeSignature);
//        Log.d("DisplayThread Log", "The current barTime is " + barTime);
//        Log.d("DisplayThread Log", "The current displayTime is " + displayTime);
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
        //secondsPerBeat = 60.0 / tempo;
        //barTime = (int) (timeSignature * 1000 * secondsPerBeat);
        //displayTime = barTime * 4;
        //quarterBeat = (int) (1000 * secondsPerBeat / 4);
    }

    public void setDelay(int delay) {

    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public void setDisplay(String score) {
        display = score;
    }

    public void setArchived(String score) {
        archived = score;
    }
}