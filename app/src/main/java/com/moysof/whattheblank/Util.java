package com.moysof.whattheblank;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Util {

    // Max number of teams, players per team, or cards per player
    public static final Integer MAX_NUMBER = 10;

    // Constants
    public static final String URL_SUPPORT = "http://www.moyersoftware.com";
    public static final String URL_LOG_IN = "http://moyersoftware.com/blank/log_in.php";
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

    public static String getPhone() {
        try {
            return ((TelephonyManager) BaseApplication.getAppContext().
                    getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        } catch (Exception e) {
            return "";
        }
    }

}
