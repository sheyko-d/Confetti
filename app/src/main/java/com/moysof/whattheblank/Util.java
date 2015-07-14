package com.moysof.whattheblank;

import android.content.pm.ApplicationInfo;
import android.util.Log;

public class Util {

    // Constants
    public static final String URL_SUPPORT = "http://www.moyersoftware.com";
    public static final String URL_SIGN_IN = "http://moyersoftware.com/blank/sign_in.php";
    public static final String URL_SIGN_IN_SOCIAL
            = "http://moyersoftware.com/blank/sign_in_social.php";
    private static final String LOG_TAG = "BlankDebug";

    // Methods
    public static void Log(Object text) {
        Log.d(LOG_TAG, text + "");
    }

    public static Boolean isDebugging() {
        return (0 != (BaseApplication.getAppContext().getApplicationInfo().flags
                &= ApplicationInfo.FLAG_DEBUGGABLE));
    }

}
