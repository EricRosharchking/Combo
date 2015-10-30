package com.example.liyuan.projectcombo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Liyuan on 10/17/2015.
 */
public class Metronome {
    private final int SAMPLE_RATE = 44100;
    boolean isRunning = true;
    int size = 22050;
    private final double frequency1 = 659.3f;
    private final double frequency2 = 261.1f;
    Thread t;


    public Metronome() {
        t = new Thread() {
            public void run() {
                // set process priority
                setPriority(Thread.MAX_PRIORITY);
                // set the buffer size
                int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                // create an audiotrack object
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, SAMPLE_RATE,
                        AudioTrack.MODE_STREAM);

                short samples[];
                int amp = 10000;
                double twopi = 2 * Math.PI;
                double ph = 0.0;

                // start audio
                audioTrack.play();

                // synthesis loop
                while(isRunning){
                    samples = new short[size];
                    try {
                        long startTime = System.currentTimeMillis();
                        for (int i = 0; i < samples.length; i++) {
                            samples[i] = (short) (amp * Math.sin(ph));
                            ph += twopi * frequency2 / SAMPLE_RATE;
                        }
                        audioTrack.write(samples, 0, samples.length);
                        for (int i = 0; i < samples.length; i++) {
                            samples[i] = 0;
                            ph += twopi * frequency2 / SAMPLE_RATE;
                        }
                        audioTrack.write(samples, 0, samples.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Size Log", "The current size is " + size);
                        Log.d("Size Log", "The sample size is " + samples.length);
                        break;
                    }
                }
                audioTrack.stop();
                audioTrack.release();
            }
        };
    }

    public void startMetronome() {
    }

    public void prepare() {

    }



    public void start() {
        if (t != null) {
            t.start();
        } else {
            t = new Thread() {
                public void run() {
                    // set process priority
                    setPriority(Thread.MAX_PRIORITY);
                    // set the buffer size
                    int buffsize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                    // create an audiotrack object
                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, SAMPLE_RATE,
                            AudioTrack.MODE_STREAM);

                    short samples[];
                    double amp = 32768.0;
                    double twopi = 2 * Math.PI;
                    double ph = 0.0;

                    // start audio
                    audioTrack.play();

                    // synthesis loop
                    while(isRunning){
                        samples = new short[size];
                        try {
                            long startTime = System.currentTimeMillis();
                            for (int i = 0; i < samples.length; i++) {
                                samples[i] = (short) (amp * Math.sin(ph));
                                ph += twopi * frequency2 / SAMPLE_RATE;
                            }
                            audioTrack.write(samples, 0, samples.length);
                            for (int i = 0; i < samples.length; i++) {
                                samples[i] = 0;
                                ph += twopi * frequency2 / SAMPLE_RATE;
                            }
                            audioTrack.write(samples, 0, samples.length);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("Size Log", "The current size is " + size);
                            Log.d("Size Log", "The sample size is " + samples.length);
                            break;
                        }
                    }
                    audioTrack.stop();
                    audioTrack.release();
                }
            };
        }
    }

    public void stop() {
        isRunning = false;
        if (t != null) {
            try{
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            t = null;
        }
    }

    public void changeTempo() {
        if (size == 22050) {
            size = 14700;
        } else if (size == 14700) {
            size = 11025;
        } else if (size == 11025) {
            size = 22050;
        }
    }

}
