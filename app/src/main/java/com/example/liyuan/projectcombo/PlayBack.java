package com.example.liyuan.projectcombo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Liyuan on 10/18/2015.*/


public class PlayBack extends Thread{
    Thread t;
    AudioTrack audioTrack;
    private final double TWO_PI = 2 * Math.PI;
    private final int SAMPLE_RATE = 44100;
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
                        int sampleSize = (int) (44100 * notesLength[j]) * 2;
                        samples = new short[sampleSize];
                        double phase_Index = 0.0;
                        double  frequency = musicScore.get(notesScore[j]);
                        for (int i = 0; i < sampleSize; i ++) {
                            samples[i] = (short) (amplitude * Math.sin(phase_Index));
                            phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                        }
                        audioTrack.write(samples, 0, sampleSize);
                        Log.d("PlayBack While Log", "The current sample size is " + sampleSize);

                        samples = new short[22050];
                        for (int i = 0; i < 22050 && amplitude > 0; i ++) { // 这个Decay的长度要改成跟tempo有关的，八分之一的beat长度
                            samples[i] = (short) (amplitude * Math.sin(phase_Index));
                            phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                            amplitude -= 2;
                        }
                        audioTrack.write(samples, 0, 22050);
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
}
