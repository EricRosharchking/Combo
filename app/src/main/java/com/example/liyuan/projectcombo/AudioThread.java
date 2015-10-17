package com.example.liyuan.projectcombo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Liyuan on 10/5/2015.
 */

public class AudioThread extends Thread {
    private Note note;
    private AudioTrack audioTrack;
    private final double TWO_PI = 2 * Math.PI;
    private final int SAMPLE_RATE = 44100;
    private final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    private boolean isRunning;

    public AudioThread(Note note) {
        isRunning = true;
        this.note = note;
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE,AudioTrack.MODE_STREAM);
//        audioTrack.play();
        Log.d("BufferSize Log", "The current buffer size in use is: " + BUFFER_SIZE);
    }

    public void run() {
        audioTrack.play();
        short samples[] = new short[BUFFER_SIZE];
        int amplitude = 10000;
        double frequency = 261.1f;
        if (note != null) {
            frequency = note.getFrequency();
        }
        //the frequency needs to be obtained from the constructor later
        double phrase_Index = 0.0;

        try {
            while(isRunning) {
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    samples[i] = (short) (amplitude * Math.sin(phrase_Index));
                    phrase_Index += TWO_PI * frequency / SAMPLE_RATE;
                }
                audioTrack.write(samples, 0, BUFFER_SIZE);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d("AuidioTrack Log", "The audiotrack pointer is " + e.toString());
        }

/*
        audioTrack.stop();
        audioTrack.release();*/
    }


    public void stopPlaying() {
        audioTrack.stop();
        audioTrack.release();
        isRunning = false;
    }

/*    public void upOctave() {
        note.upOctave();
    }

    public void lowerOctave() {
        note.lowerOctave();
    }*/

}
