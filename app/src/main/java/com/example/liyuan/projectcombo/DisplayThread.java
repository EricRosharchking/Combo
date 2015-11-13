package com.example.liyuan.projectcombo;

import android.util.Log;

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

    }

    public void run() {

        startTime = System.currentTimeMillis();
        Log.d("DisplayThread Log", "The start Time is " + startTime);
        long elapsedTime = System.currentTimeMillis() - startTime;
        long count = elapsedTime / 250;
        while (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime;
            if ((elapsedTime % 250) == 0 && elapsedTime > count * 250 && (elapsedTime / 250) > 0) {
                if (lastKey == key) {
                    display = display + "_";
                } else {
                    lastKey = key;
                    display += key;
                }
                count = elapsedTime / 250;
                Log.d("DisplayThread Log", "The current system time is " + System.currentTimeMillis());
                Log.d("DisplayThread Log", "The elapsed time is " + elapsedTime);
                Log.d("DisplayThread Log", "The elapsed 250 milliseconds period is" + count);
                Log.d("DisplayThread Log", "The display is " + display);
                if ((elapsedTime % barTime) == 0) {
                    display = display + "|";
                    if ((elapsedTime % displayTime) == 0) {
                        archived += display;
                        display = "";
                    }
                }
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
    }
}