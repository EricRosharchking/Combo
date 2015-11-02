package com.example.liyuan.projectcombo;

/**
 * Created by Liyuan on 11/1/2015.
 */
public class DisplayThread extends Thread{
    private long startTime;
    private int tempo;
    private int timeSignature;


    public DisplayThread() {
        timeSignature = 4;
        tempo = 60;
        startTime = System.currentTimeMillis();

    }

    public void run() {

    }
}
