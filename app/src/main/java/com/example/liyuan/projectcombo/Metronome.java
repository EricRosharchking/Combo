package com.example.liyuan.projectcombo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by Liyuan on 10/17/2015.
 */
public class Metronome extends Activity {

    private final int SAMPLE_RATE = 22050;
    boolean isRunning = true;
    int sound  = 4410;
    int count;
    int timeSignature;
    int size = 22050;
    private final double frequency1 = 659.3f;
    private final double frequency2 = 261.1f;
    Thread t;
    double sliderval;
    int metronomeTempo;
    SeekBar tempoSeekBar;


    public Metronome() {
        timeSignature = 4;
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
                int amp = 20000;
                double twopi = 2 * Math.PI;
                double ph = 0.0;

                double frequency = frequency1;
                // start audio
                audioTrack.play();
                count = 1;

                // synthesis loop
                while(isRunning){
                    if (count == 1) {
                        frequency = frequency1;
                        count = 2;
                        Log.d("Metronome Count Log" , "Count is " + count);
                    } else {
                        frequency = frequency2;
                        if (count < timeSignature) {
                            count ++;
                            Log.d("Metronome Count Log", "Count is " + count);
                        } else {
                            count = 1;
                            Log.d("Metronome Count Log", "Count is " + timeSignature);
                        }
                    }
                    samples = new short[size];
                    try {
                        for (int i = 0; i < sound; i++) {
                            samples[i] = (short) (amp * Math.sin(ph));
                            ph += twopi * frequency / SAMPLE_RATE;
                        }
                        for (int i = sound; i < samples.length; i++) {
                            samples[i] = 0;
                        }
                        audioTrack.write(samples, 0, samples.length);
                        Log.d("Metronome Log", "The volume is " + count);
                        Log.d("Metronome Log", "The frequency is " + frequency);
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




    public void start() {
        isRunning = true;
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
                    double frequency = frequency1;

                    // start audio
                    audioTrack.play();

                    // synthesis loop
                    while(isRunning){
                        if (count == 1) {
                            frequency = frequency1;
                            count = 2;
                            Log.d("Metronome Count Log" , "Count is " + count);
                        } else {
                            frequency = frequency2;
                            if (count < timeSignature) {
                                count ++;
                                Log.d("Metronome Count Log", "Count is " + count);
                            } else {
                                count = 1;
                                Log.d("Metronome Count Log", "Count is " + timeSignature);
                            }
                        }
                        samples = new short[size];
                        try {
                            for (int i = 0; i < sound; i++) {
                                samples[i] = (short) (amp * Math.sin(ph));
                                ph += twopi * frequency / SAMPLE_RATE;
                            }
                            for (int i = sound; i < samples.length; i++) {
                                samples[i] = 0;
                            }
                            audioTrack.write(samples, 0, samples.length);
                            Log.d("Metronome Log", "The volume is " + count);
                            Log.d("Metronome Log", "The frequency is " + frequency);
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

    public void changeTempo(int newSize) {
        metronomeTempo = newSize;
        size = 44100 * 60 / metronomeTempo / 2;
    }

    public void changeTimeSignature(int timeSignature) {
        this.timeSignature = timeSignature;
    }

}
