package com.example.liyuan.projectcombo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Liyuan on 10/18/2015.*/


public class PlayBack extends Thread{

    private final double strike = 0.002;
    Thread t;
    AudioTrack audioTrack;
    private final double TWO_PI = 2 * Math.PI;
    private final int SAMPLE_RATE = 22050;
    private final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    private boolean isRunning;
    private short[] samples;
    private int[] notesScore;
    private double[] notesLength;
    int size;
    private static HashMap<Integer, Double> musicScore = null;

    static {
        musicScore = new HashMap<Integer, Double>();
        for (int i = 0; i < 14; i ++) {
            musicScore.put(i, (new Note(i)).getFrequency());
        }
    }

    public PlayBack(int[] notes, String[] lengths) {
        if (notes == null || notes.length == 0 || notes.length != lengths.length) {
            isRunning = false;
            Log.d("PlayBack Log", "notes or length is null or empty");
            return;
        } else {

            Log.d("PlayBack Log", "notes and lengths are not null");
            notesScore = notes;
            notesLength = new double [lengths.length];
            for(int i = 0; i < lengths.length; i ++) {
                notesLength[i] = Double.parseDouble(lengths[i]);
            }
            isRunning = true;
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE,AudioTrack.MODE_STREAM);

            size = notesScore.length;
        }
    }

    public void run() {
        if (audioTrack != null) {
            audioTrack.play();

            Log.d("PlayBack Log", "audioTrack is not Null");
            try {
                while (isRunning) {

                    for (int j = 0; j < size; j ++) {

                        double amplitude = 32768.0;
                        Log.d("PlayBack Log", "The integer j is " + j);
                        int sampleSize = (int) (SAMPLE_RATE * notesLength[j]) * 2;
                        samples = new short[sampleSize];
                        double phase_Index = 0.0;
                        double  frequency = musicScore.get(notesScore[j]);
                        if (frequency != 0) {
                            for (int i = 0; i < sampleSize / 2; i ++) {
                                double a = xxx(i, SAMPLE_RATE, frequency, 0);
                                double b = xxx(i, SAMPLE_RATE, frequency, 0.25);
                                double c = xxx(i, SAMPLE_RATE, frequency, 0.5);
                                double extra = Math.pow(a, 2) + (0.75 * b) + (0.1 * c);
                                double test = generate(i, SAMPLE_RATE, frequency, extra);

                                double dampen = getDampen(SAMPLE_RATE, frequency, amplitude);

                                double y = 1 - (i -  44100.0    * 0.002)    / (44100.0    *  1.998);

                                double k = Math.pow(y, dampen);
                                double value = amplitude * k * test;

                                if(i < 88) {
                                    value = amplitude * (i / (SAMPLE_RATE * strike)) * test;
                                }

                                ////TODO:把phase_Index算回去 然后可以根据触摸长短调整音节的长短

                                samples[i * 2] = (short) value;
                                value = value / (Math.pow(2, 8));
                                samples[i *2 + 1] = (short) value;
                            }
                            audioTrack.write(samples, 0, sampleSize);
                            Log.d("PlayBack While Log", "The current sample size is " + sampleSize);


                            //samples = new short[SAMPLE_RATE / 2];
                            //for (int i = 0; i < SAMPLE_RATE / 2 && amplitude > 0; i ++) { // 这个Decay的长度要改成跟tempo有关的，八分之一的beat长度
                            //    samples[i] = (short) (amplitude * Math.sin(phase_Index));
                            //    phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                            //    amplitude -= 2;
                            //}
                            //audioTrack.write(samples, 0, SAMPLE_RATE / 2);
                        } else {
                            sampleSize -= 22050;
                            if (sampleSize > 0) {
                                samples = new short[sampleSize];
                                for (int i = 0; i < sampleSize; i ++) {
                                    samples[i] = (short) (amplitude * Math.sin(phase_Index));
                                    phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                                }
                                audioTrack.write(samples, 0, sampleSize);
                            }
                        }
                        Log.d("PlayBack While Log", "The current sample size is " + sampleSize);
                    }
                    isRunning = false;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            audioTrack.stop();
            audioTrack.release();

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
        data = Math.sin(2 * Math.PI * (di / ds) * frequency + extra);

        return data;
    }

    private double xxx(int i, int sampleRate, double frequency, double extra) {

        double di = (double) i;
        double ds = (double) sampleRate;
        double xx = Math.sin(2 * Math.PI * (di / ds) * frequency + extra);
        //if (i < 10) {
        //Log.d("base Log", "base is [" + i +"]" + xx);
        //}
        return xx;
    }

    private double getDampen(int sampleRate, double frequency, double volume) {
        double result = 0.0;
        double ds = (double) sampleRate;
        double a = Math.log((frequency * volume) / ds);
        result = Math.pow((0.5 * a),2);

        return result;
    }

}