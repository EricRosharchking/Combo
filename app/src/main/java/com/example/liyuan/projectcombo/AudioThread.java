package com.example.liyuan.projectcombo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

/**
 * Created by Liyuan on 10/5/2015.
 */

public class AudioThread extends Thread {
    private final double strike = 0.002;
    private final double sustain = 0.88;
    private final double dive = 0.1;
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
        double amplitude = 32768.0;
        double sustainVolume = amplitude * 5 / 8;
        double frequency = 261.1f;
        if (note != null) {
            frequency = note.getFrequency();
        }
        //the frequency needs to be obtained from the constructor later
        double phase_Index = 0.0;

        try {
            int elapsed_Buffer = 0;
            while(isRunning) {
                for (int i = 0; i < BUFFER_SIZE; i++) {
                    if(i + elapsed_Buffer < SAMPLE_RATE * strike) {
                        // The initial strike or attack
                        amplitude *= (i / (SAMPLE_RATE * strike));
                    } else if (i + elapsed_Buffer < SAMPLE_RATE * (strike + dive)) {
                        // Go down to the sustaining period
                        if (amplitude > sustainVolume) {
                            amplitude -= (amplitude / (SAMPLE_RATE * dive));
                        }
                    } else if (i + elapsed_Buffer < SAMPLE_RATE * sustain) {
                        // Sustain the volume
                        amplitude = sustainVolume;
                    } /*else {
                        // The volume now decay to zero
                        if (amplitude > 0) {
                            amplitude -= (amplitude / (SAMPLE_RATE * dive));
                        }
                    }*/
                    samples[i] = (short) (amplitude * Math.sin(phase_Index));
                    phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                }
                audioTrack.write(samples, 0, BUFFER_SIZE);
                elapsed_Buffer += BUFFER_SIZE;
            }
            Log.d("Elapsed Buffer Log", "The elapsed buffer is " + elapsed_Buffer);
            samples = new short[(int) (22050)];
            for (int i = 0; i < 22050 && amplitude > 0; i ++) { // 这个Decay的长度要改成跟tempo有关的，八分之一的beat长度
                samples[i] = (short) (amplitude * Math.sin(phase_Index));
                phase_Index += TWO_PI * frequency / SAMPLE_RATE;
                amplitude -= 2;
            }
            audioTrack.write(samples, 0, 22050);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d("AudioTrack Log", "The audiotrack pointer is " + e.toString());
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
