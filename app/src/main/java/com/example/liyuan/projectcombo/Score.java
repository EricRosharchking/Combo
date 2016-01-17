package com.example.liyuan.projectcombo;

import java.io.Serializable;

/**
 * Created by Liyuan on 1/5/2016.
 */
public class Score implements Serializable {
    private String author;
    private String title;
    private int tempo;
    private String timeSignature;
    private String score;

    public Score() {
        author = "Anonymous";
        title = "New Score";
        tempo = 60;
        timeSignature = "4/4";
        score = "1___2___3___4___5___";
    }

    public void appendScore(String append){
        score += append;
    }

    public void setScore(String score) {
        this.score = score;
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

    public String getScore() {
        return score;
    }
}
