package com.kimjiun.yahoostockapp;

import android.util.Log;

public class MyLog {
    public static void log(String stage, Throwable throwable) {
        Log.e("APP", stage, throwable);
    }

    public static void log(Throwable throwable) {
        Log.e("APP", "Error", throwable);
    }

    public static void log(String stage, String item) { Log.d("APP", stage + ":" + Thread.currentThread().getName() + ":" + item); }

    public static void log(String stage) { Log.d("APP", stage + ":" + Thread.currentThread().getName());}

}
