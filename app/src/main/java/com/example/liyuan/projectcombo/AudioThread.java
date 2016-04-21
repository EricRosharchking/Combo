package com.example.liyuan.projectcombo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Liyuan on 10/5/2015.
 */

public class AudioThread extends Thread {
    private double strike;
    private Note note;
    private AudioTrack audioTrack;
    private final double TWO_PI = 2 * Math.PI;
    private final int SAMPLE_RATE = 44100;
    private final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT) * 2;
    private final int BUFFER_COUNT = BUFFER_SIZE / 2;

    private boolean isRunning;

    public AudioThread(Note note) {
        isRunning = true;
        strike  = 0.002;
        this.note = note;
    }


    public void run() {

        audioTrack = init();
        prepare(audioTrack);
        short samples[] = new short[2 * BUFFER_SIZE];
        double amplitude = 16384.0;
        double frequency = 261.63f;
        double remainder = 1.998;
        if (note != null) {
            frequency = note.getFrequency();
        }
//        Log.i("Frequency is ","" + frequency);
        //the frequency needs to be obtained from the constructor later
        int phase_Index = 0;

        try {
            int elapsed_Buffer = 0;
            while(isRunning) {
                for (int i = 0; i < BUFFER_COUNT ; i++, phase_Index ++) {
                    //Please do not change this part

                    double a = xxx(phase_Index, SAMPLE_RATE, frequency, 0);
                    double b = xxx(phase_Index, SAMPLE_RATE, frequency, 0.25);
                    double c = xxx(phase_Index, SAMPLE_RATE, frequency, 0.5);
                    double extra = Math.pow(a, 2) + (0.75 * b) + (0.1 * c);
                    double test = generate(phase_Index, SAMPLE_RATE, frequency, extra);

                    double dampen = getDampen(SAMPLE_RATE, frequency, amplitude);

                    double y = 1 - (phase_Index -  SAMPLE_RATE    * strike)    / (SAMPLE_RATE    * remainder );

                    double j = Math.pow(y, dampen);
                    double value = amplitude * j * test;

                    if(phase_Index < 88) {
                        value = amplitude * (phase_Index / (SAMPLE_RATE * strike)) * test;
                    }

                    double value1 = value / (Math.pow(2, 8));
                    samples[i * 4] = (short) value;
                    samples[i * 4 + 1] = (short) value;
                    samples[i * 4 + 2] = (short) value1;
                    samples[i * 4 + 3] = (short)  value1;


                }
                if (audioTrack != null) {
                    audioTrack.write(samples, 0, BUFFER_SIZE);
                }
                elapsed_Buffer += BUFFER_SIZE;
            }

//            Log.d("Elapsed Buffer Log", "The elapsed buffer is " + elapsed_Buffer);
            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                audioTrack.stop();
                audioTrack.flush();
                audioTrack.release();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d("AudioTrack Log", "The audiotrack pointer is " + e.toString());
        }
        if (!isRunning) {
            audioTrack = null;
        }
    }

    private double generate(int i, int sampleRate, double frequency, double extra) {
        double data = 0.0;

        double di = (double) i;
        double ds = (double) sampleRate;

        double a = xxx(i, sampleRate, frequency, 0);
        double b = xxx(i, sampleRate, frequency, 0.25);
        double c = xxx(i, sampleRate, frequency, 0.5);

        extra = Math.pow(a, 2) + (0.75 * b) + (0.1 * c);
        data = Math.sin(TWO_PI * (di / ds) * frequency + extra);

        return data;
    }

    private double xxx(int i, int sampleRate, double frequency, double extra) {

        double di = (double) i;
        double ds = (double) sampleRate;
        return Math.sin(TWO_PI * (di / ds) * frequency + extra);
    }

    private double getDampen(int sampleRate, double frequency, double volume) {
        double result = 0.0;
        double ds = (double) sampleRate;
        double a = Math.log((frequency * volume) / ds);
        result = Math.pow((0.5 * a),2);

        return result;
    }

    public void stopPlaying() {
        isRunning = false;
    }

    private AudioTrack init() {
        audioTrack = null;
        return new AudioTrack(AudioManager.STREAM_MUSIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE,AudioTrack.MODE_STREAM);
    }

    private void prepare(AudioTrack track) {
        try {
            track.play();
        } catch ( IllegalStateException e) {
            track = init();
            prepare(track);
        }
    }

}