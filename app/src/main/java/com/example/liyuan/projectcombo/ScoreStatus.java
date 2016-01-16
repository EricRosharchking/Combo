package com.example.liyuan.projectcombo;

/**
 * Created by Liyuan on 1/6/2016.
 */
public class ScoreStatus {
    private Score score;
    private boolean status;

    public ScoreStatus() {
        status = false;
        score = new Score();
    }

    public ScoreStatus(Object object) {
        score = new Score();

        if(object instanceof Score) {
            score = (Score)object;
            status = true;
        } else {
            status = false;
        }
    }

    public Score getScore() {
        return score;
    }

    public boolean getStatus() {
        return status;
    }
}
