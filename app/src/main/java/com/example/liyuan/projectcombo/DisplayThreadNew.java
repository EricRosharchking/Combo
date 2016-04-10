package com.example.liyuan.projectcombo;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

/**
 * Created by Liyuan on 11/1/2015.
 */
public class DisplayThreadNew {
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
    private int noteTime;
    private int lastNoteTime;
    private int barCount;
    private double secondsPerBeat;
    private double totalLength;

    private final int quarterBeat = 250;
    private final String underline = "<sub>\u0332</sub>";
    private final String double_underline = "<sub>\u0333</sub>";
    private final String curve = "<sup>\u0361</sup>";
    private final String bullet = "\u2022";
    private final String dot_above = "<sub>\u0307</sub>";
    private final String dot_below = "\u0323";

    public DisplayThreadNew() {
        timeSignature = 4;
        tempo = 60;
        display = "|| ";
        archived = display;
        isRunning = true;
        key = 0;
        octave = 4;
        lastKey = -1;
        secondsPerBeat = 60.0 / tempo;
        barTime = timeSignature * 1000;
        displayTime = barTime * 4;
        barCount = 1;
        totalLength = 0.0;
    }

    public void whatever() {


    }

    public String getDisplay() {
        return display;
    }

    public String getArchived() {
        return archived;
    }

    public void stopThread() {
        archived += display;
    }

    public void update(double lastLength) {
        lastLength *= 1000;
        int count = (int) (lastLength / quarterBeat);
        int x = count / 4;
        int y = count % 4;

        for (int i = 1; i * 4 * quarterBeat <= lastLength; i++) {
            display += " - ";
        }


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

        display += "||";
    }

    public void update(int strike, double lastLength) {
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

        if (lastKey != -1) {
            lastLength *= 1000;
            totalLength += lastLength;
            if (lastLength == 0) {
                display = display.substring(0, display.length()-1);
            }else if (totalLength > barCount * barTime) {
                while (totalLength > barCount * barTime) {
                    double firstHalf = barCount * barTime - (totalLength - lastLength);
                    int count = (int) (firstHalf / quarterBeat);
                    int x = count / 4;
                    int y = count % 4;

                    for (int i = 2; i * 4 * quarterBeat < firstHalf; i++) {
                        display += " - ";
                    }
                    if (x > 0 && y > 0) {
                        display += " ";
                        display += lastKey;
                    }
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


                    barCount++;
                    display += " | ";
                    display += lastKey;
                    lastLength -= firstHalf;
                }
                double secondHalf = totalLength - barTime * barCount;
                int count = (int) (secondHalf / quarterBeat);
                int x = count / 4;
                int y = count % 4;

                for (int i = 2; i * 4 * quarterBeat < secondHalf; i++) {
                    display += " - ";
                }
                if (x > 0 && y > 0) {
                    display += " ";
                    display += lastKey;
                }
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


            }

            int count = (int) (lastLength / quarterBeat);
            int x = count / 4;
            int y = count % 4;

            for (int i = 2; i * 4 * quarterBeat < lastLength; i++) {
                display += " - ";
            }
            if (x > 0 && y > 0) {
                display += " ";
                display += lastKey;
            }
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


            if (totalLength == barCount * barTime) {
                barCount++;
                display += " | ";
            }

        }
        lastKey = key;
        display += " ";
        String strKey = "" + lastKey;
        if (lastKey!=0) {
            switch (octave) {
                case 3:
                    strKey += dot_below;
                    break;
                case 4:
                    break;
                case 5:
                    strKey += "<sup>\u0307</sup>";
                    break;
            }
        }
        display += strKey;

    }

    public void setTimeSignature(int timeSignature) {
        this.timeSignature = timeSignature;
        barTime = timeSignature * 1000;
        barCount = 1;
//        displayTime = barTime * 4;
//        Log.d("DisplayThread Log", "The current timeSignature is " + timeSignature);
//        Log.d("DisplayThread Log", "The current barTime is " + barTime);
//        Log.d("DisplayThread Log", "The current displayTime is " + displayTime);
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
        barTime = timeSignature * 1000;
//        displayTime = barTime * 4;
        barCount = 1;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }
}