package com.example.liyuan.projectcombo;

/**
 * Created by Liyuan on 10/5/2015.
 */
public class Note {
    private String name;
    private double frequency;
    private int octave;
    int number;

    public Note(int noteNO) {
        number = noteNO;
        switch (noteNO) {
            case 1:
                name = "CNatural";  frequency = 261.6f;
                break;
            case 2:
                name = "CSharp";    frequency = 277.2f;
                break;
            case 3:
                name = "DNatural";  frequency = 293.7f;
                break;
            case 4:
                name = "EFlat";     frequency = 311.1f;
                break;
            case 5:
                name = "ENatural";  frequency = 329.6f;
                break;
            case 6:
                name = "FNatural";  frequency = 349.2f;
                break;
            case 7:
                name = "FSharp";    frequency = 370.0f;
                break;
            case 8:
                name = "GNatural";  frequency = 392.0f;
                break;
            case 9:
                name = "GSharp";    frequency = 415.3f;
                break;
            case 10:
                name = "ANatural";  frequency = 440.0f;
                break;
            case 11:
                name = "BFlat";     frequency = 466.2f;
                break;
            case 12:
                name = "BNatural";  frequency = 493.9f;
                break;
            case 13:
                name = "hCNatural"; frequency = 523.3f;
            default:
                break;
        }

        octave = 4;
    }

    public void upOctave() {
        if (octave < 5) {
            frequency *= 2.0;
            octave ++;
        }
    }

    public void lowerOctave() {
        if (octave > 3) {
            frequency /= 2.0;
            octave --;
        }
    }

    public int getOctave() {
        return octave;
    }

    public double getFrequency() {
        return frequency;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String toString() {
        return ("Name of the Note is: " + name + ", at Octave "+ octave + ", with Frequency: " + frequency);
    }
}