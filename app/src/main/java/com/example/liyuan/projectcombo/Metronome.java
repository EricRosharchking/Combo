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

import com.example.liyuan.projectcombo.kiv.WavReader;

/**
 * Created by Liyuan on 10/17/2015.
 */
public class Metronome extends Activity {

    private final int SAMPLE_RATE = 44100;
    boolean isRunning = true;
    boolean withMetronome;
    int sound = 4410;
    int count;
    int timeSignature;
    int size = 44100;
    int beats;
    int metronomeTempo;
    private final double frequency1 = 659.3f;
    private final double frequency2 = 261.1f;
    Thread t;
    double sliderval;
    SeekBar tempoSeekBar;
    short[] peep;
    short[] pop;
    short[] array;


    public Metronome(int tempo) {
        timeSignature = 4;
        peep = WavReader.readFile(App.getAppContext(), R.raw.pop3);
        pop = WavReader.readFile(App.getAppContext(), R.raw.pop1);
        array = peep;
        count = 1;
        withMetronome = false;
        changeTempo(tempo);

        /*t = new Thread() {
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
                double amp = 20000.0;
                double twopi = 2 * Math.PI;
                double ph = 0.0;

                double frequency = frequency1;
                // start audio
                audioTrack.play();
                count = 1;

                // synthesis loop
                while(isRunning){
                    if (count == 1) {
                        array = peep;
                        count = 2;
                        Log.d("Metronome Count Log" , "Count is " + count);
                    } else {
                        array = pop;
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
                        for (int i = 0; i < array.length; i ++) {
                            samples[i] = array[i];
                        }
                        for (int i = array.length; i < samples.length; i ++) {
                            samples[i] = 0;
                        }
                        audioTrack.write(samples, 0, samples.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
//                    try {
//                        for (int i = 0; i < sound; i++) {
//                            samples[i] = (short) (amp * Math.sin(ph));
//                            ph += twopi * frequency / SAMPLE_RATE;
//                        }
//                        for (int i = sound; i < samples.length; i++) {
//                            samples[i] = 0;
//                        }
//                        audioTrack.write(samples, 0, samples.length);
//                        Log.d("Metronome Log", "The volume is " + count);
//                        Log.d("Metronome Log", "The frequency is " + frequency);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.d("Size Log", "The current size is " + size);
//                        Log.d("Size Log", "The sample size is " + samples.length);
//                        break;
//                    }
                }
                audioTrack.stop();
                audioTrack.release();
            }
        };*/
    }


    public void start() {
        isRunning = true;
        count = 1;
        Log.i("Log@Metro125", "Metronome Started");
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
                double amp = 20000.0;
                double twopi = 2 * Math.PI;
                double ph = 0.0;
                double frequency = frequency1;

                // start audio
                audioTrack.play();
                count = 1;
                beats = 0;

                // synthesis loop
                whileLoop:
                while (isRunning) {

                    Log.i("Log@Metronome162", "WithMetronome is " + withMetronome);
                    if (!withMetronome) {
                        if (beats >= timeSignature)
                            break whileLoop;
                    }
                    if (count == 1) {
                        array = peep;
                        count = 2;
                        Log.d("Metronome Count Log", "Count is " + count);
                    } else {
                        array = pop;
                        if (count < timeSignature) {
                            count++;
                            Log.d("Metronome Count Log", "Count is " + count);
                        } else {
                            count = 1;
                            Log.d("Metronome Count Log", "Count is " + timeSignature);
                        }
                    }
                    samples = new short[size];
                    try {
                        for (int i = 0; i < array.length; i++) {
                            samples[i] = array[i];
                        }
                        for (int i = array.length; i < size; i++) {
                            samples[i] = 0;
                        }
                        audioTrack.write(samples, 0, samples.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                    beats ++;

//                        try {
//                            for (int i = 0; i < sound; i++) {
//                                samples[i] = (short) (amp * Math.sin(ph));
//                                ph += twopi * frequency / SAMPLE_RATE;
//                            }
//                            for (int i = sound; i < samples.length; i++) {
//                                samples[i] = 0;
//                            }
//                            audioTrack.write(samples, 0, samples.length);
//                            Log.d("Metronome Log", "The volume is " + count);
//                            Log.d("Metronome Log", "The frequency is " + frequency);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.d("Size Log", "The current size is " + size);
//                            Log.d("Size Log", "The sample size is " + samples.length);
//                            break;
//                        }
                }
                audioTrack.stop();
                audioTrack.release();
            }
        };
        t.start();

    }

    public void stop() {
        isRunning = false;
        if (t != null) {
            try {
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            t = null;
        }
    }

    public void changeTempo(int metronomeTempo) {
        this.metronomeTempo = metronomeTempo;
        size = 44100 * 60 / metronomeTempo;
    }

    public void changeTimeSignature(int timeSignature) {
        this.timeSignature = timeSignature;
    }

    public void setWithMetronome(boolean withMetronome) {
        this.withMetronome = withMetronome;
    }
}
