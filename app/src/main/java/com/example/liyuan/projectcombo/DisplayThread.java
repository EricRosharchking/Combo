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

    private final String underline = "<sub>\u0332</sub>";
    private final String double_underline = "<sub>\u0333</sub>";
    private final String curve = "<sup>\u0361</sup>";
    private final String bullet = "&#8226\n";
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
        barTime = timeSignature * 1000;
        displayTime = barTime * 4;
        octave = 4;
        quarterBeat = 250;
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

        startTime = System.currentTimeMillis();
        Log.d("DisplayThread Log", "The start Time is " + startTime);
        long elapsedTime = System.currentTimeMillis() - startTime;
        long count = elapsedTime / quarterBeat;
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
                    display = display+" - ";//短破折号
//                    display = display+"—";//长破折号
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
                count = elapsedTime / 250;
//                Log.d("DisplayThread Log", "The current system time is " + System.currentTimeMillis());
//                Log.d("DisplayThread Log", "The elapsed time is " + elapsedTime);
//                Log.d("DisplayThread Log", "The elapsed 250 milliseconds period is" + count);
//                Log.d("DisplayThread Log", "The display is " + display);
                if ((elapsedTime % barTime) == 0) {
                    display = display + "|";
                    if ((elapsedTime % displayTime) == 0) {
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
        key = strike;
    }

    public void setTimeSignature(int timeSignature) {
        this.timeSignature = timeSignature;
        barTime = timeSignature * 1000;
        displayTime = barTime * 4;
        Log.d("DisplayThread Log", "The current timeSignature is " + timeSignature);
        Log.d("DisplayThread Log", "The current barTime is " + barTime);
        Log.d("DisplayThread Log", "The current displayTime is " + displayTime);
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