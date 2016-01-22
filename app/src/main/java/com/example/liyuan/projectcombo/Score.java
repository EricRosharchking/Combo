package com.example.liyuan.projectcombo;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * Created by Liyuan on 1/5/2016.
 */
public class Score implements Serializable {
    private String author;
    private String title;
    private int tempo;
    private String timeSignature;
    private int[] notes;
    private double[] lengths;

    public Score() {
        author = "Anonymous";
        title = "New Score";
        tempo = 60;
        timeSignature = "4/4";
    }

    public void appendScore(int[] newScore, double[] newLengths){
        notes = addNotes(notes, newScore);
        lengths = addLengths(lengths, newLengths);
    }

    public void setScore(int[] notesAndRest, double[] notesAndRestLengths) {
        this.notes = notesAndRest;
        this.lengths = notesAndRestLengths;
    }

    private int[] addNotes(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        int[] c = (int[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    private double[] addLengths(double[] a, double[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        double[] c = (double[]) Array.newInstance(a.getClass().getComponentType(), aLen+bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public void setTimeSignature(String timeSignature) {
        this.timeSignature = timeSignature;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTempo() {
        return tempo;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimeSignature() {
        return timeSignature;
    }

    public String getTitle() {
        return title;
    }

    public int[] getScore() {
        return notes;
    }

    public double[] getLengths() { return lengths; }
}
