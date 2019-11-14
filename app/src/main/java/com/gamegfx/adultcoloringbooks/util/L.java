package com.gamegfx.adultcoloringbooks.util;

import android.util.Log;

/**
 * Created by GameGFX Studio on 2015/8/4.
 */
public class L {

    private static final String TAG = "FillColor";
    public static boolean isDebug = false;

    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void i(String msg) {
        if (isDebug)
            if (msg != null)
                Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isDebug)
            if (msg != null)
                Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isDebug)
            if (msg != null)
                Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isDebug)
            if (msg != null)
                Log.v(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (isDebug)
            if (msg != null)
                Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug)
            if (msg != null)
                Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug)
            if (msg != null)
                Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug)
            if (msg != null)
                Log.v(tag, msg);
    }
}