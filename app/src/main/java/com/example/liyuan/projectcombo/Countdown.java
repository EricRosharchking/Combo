package com.example.liyuan.projectcombo;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by Liyuan on 3/24/2016.
 */
public class Countdown implements Runnable{
    private ValueAnimator animator;
    TextView textView_countdown;
    private int timeSig;
    Context context;
    public Countdown(Context context) {
        Log.d("CountdownLog", "Countdown initialized");
        this.timeSig = 4;
        this.context = context;
        animator = new ValueAnimator();
    }

    public void setTimeSig(int timeSig) {
        this.timeSig = timeSig;
        animator.setObjectValues(timeSig, 0);
    }

    public void run() {
        Log.d("CountdownLog", "Countdown started");
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d("Log@Countdown", "inflater is null? " + (inflater != null));
        View v = inflater.inflate(R.layout.activity_main, null);
        Log.d("Log@Countdown", "too_bar's id is " + v.getId());
        textView_countdown = (TextView) v.findViewById(R.id.countdown);
        Log.d("CountDownLog", "textView_countdown is " + textView_countdown.getId());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView_countdown.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });
        animator.setObjectValues(timeSig, 0);
        animator.setDuration(5000);
        animator.start();

    }
}
