package com.example.liyuan.projectcombo;

import android.app.Application;
import android.content.Context;

import java.io.Serializable;

/**
 * Created by Liyuan on 1/7/2016.
 */
public class App extends Application implements Serializable{
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        App.mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.mContext;
    }
}
