package com.example.liyuan.projectcombo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Liyuan on 10/18/2015.*/


public class PlayBack extends Thread{

    int tempo;
	int j;
    static int lastNote;
    private final double strike = 0.002;
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
            if (i > 0) {
                musicScore.put(i * -1, (new Note(i).getFrequency()) / 2.0);
                musicScore.put(i * 14, (new Note(i).getFrequency()) * 2.0);
            }

        }
    }


    public PlayBack(int[] notes, double[] lengths, int x, int tempo) {
        if (notes == null || notes.length == 0 || notes.length != lengths.length) {
            isRunning = false;
//            Log.d("PlayBack Log", "notes or length is null or empty");
            return;
        } else {
            this.tempo = tempo;
            lastNote = x;
//            Log.d("PlayBack Log", "notes and lengths are not null");
            notesScore = notes;
            notesLength = lengths;
            isRunning = true;
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE,AudioTrack.MODE_STREAM);

            size = notesScore.length;
        }
    }


    public void run() {
        if (tempo <= 0)
            tempo = 60;
        if (audioTrack != null) {
            audioTrack.play();
            j = lastNote;
//            Log.d("PlayBack Log", "audioTrack is not Null");
            try {
                while (isRunning) {
                        for (; j < size; j++) {

                            double amplitude = 16384.0;
                            //Log.d("PlayBack Log", "The integer j is " + j);
                            int sampleSize = (int) ((SAMPLE_RATE * notesLength[j]) * 2 * 60.0 / tempo);
                            samples = new short[2 * sampleSize];
                            int sample_count = sampleSize / 2;
                            double phase_Index = 0.0;
                            double frequency = musicScore.get(notesScore[j]);
                            if (frequency != 0) {
                                for (int i = 0; i < sample_count; i++) {
                                    double a = getExtend(i, SAMPLE_RATE, frequency, 0);
                                    double b = getExtend(i, SAMPLE_RATE, frequency, 0.25);
                                    double c = getExtend(i, SAMPLE_RATE, frequency, 0.5);
                                    double extra = Math.pow(a, 2) + (0.75 * b) + (0.1 * c);
                                    double test = generate(i, SAMPLE_RATE, frequency, extra);

                                    double dampen = getDampen(SAMPLE_RATE, frequency, amplitude);

                                    double remaining = 1.998;
                                    double y = 1 - (i - SAMPLE_RATE * strike) / (SAMPLE_RATE * remaining);

                                    double k = Math.pow(y, dampen);
                                    double value = amplitude * k * test;

                                    if (i < 88) {
                                        value = amplitude * (i / (SAMPLE_RATE * strike)) * test;
                                    }

                                    ////TODO:把phase_Index算回去 然后可以根据触摸长短调整音节的长短

                                    samples[i * 4] = (short) value;
                                    samples[i * 4 + 1] = (short) value;
                                    value = value / (Math.pow(2, 8));
                                    samples[i * 4 + 2] = (short) value;
                                    samples[i * 4 + 3] = (short) value;
                                }

                            } else {
                                if (sampleSize > 0) {
                                    samples = new short[sampleSize];
                                    for (int i = 0; i < sampleSize; i++) {
                                        samples[i] = (short) (amplitude * Math.sin(phase_Index));
                                        phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                                    }
                                }
                            }
                            //Log.d("PlayBack While Log", "The current sample size is " + sampleSize);

                            audioTrack.write(samples, 0, sampleSize);
                            if (!isRunning) {
                                break;
                            }
                        }
                    j = 0;

                    isRunning = false;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            audioTrack.stop();
            audioTrack.flush();
            audioTrack.release();

        }
    }
	
	
    public void pausePlaying() {
        isRunning = false;
    }
	

    public void stopPlaying() {
        isRunning = false;
    }

	
    public int getJ() {
        if (j == size -1)
            j = 0;
        return j;
    }

	
    public int getLast() {
        return 0;
    }

	
    public int getSize() {
        return size;
    }
	
	
    private double generate(int i, int sampleRate, double frequency, double extra) {
        double data = 0.0;

        double di = (double) i;
        double ds = (double) sampleRate;

        double a = getExtend(i, sampleRate, frequency, 0);
        double b = getExtend(i, sampleRate, frequency, 0.25);
        double c = getExtend(i, sampleRate, frequency, 0.5);

        extra = Math.pow(a, 2) + (0.75 * b) + (0.1 * c);
        data = Math.sin(2 * Math.PI * (di / ds) * frequency + extra);

        return data;
    }

	
    private double getExtend(int i, int sampleRate, double frequency, double extra) {

        double di = (double) i;
        double ds = (double) sampleRate;
        return Math.sin(2 * Math.PI * (di / ds) * frequency + extra);
    }

	
    private double getDampen(int sampleRate, double frequency, double volume) {
        double result = 0.0;
        double ds = (double) sampleRate;
        double a = Math.log((frequency * volume) / ds);
        result = Math.pow((0.5 * a),2);

        return result;
    }

}