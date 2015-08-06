package com.moysof.whattheblank.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.moysof.whattheblank.BaseApplication;

public class Util {

    // Max number of teams, players per team, or cards per player
    public static final Integer MAX_NUMBER = 10;

    // Constants
    public static final String URL_SUPPORT = "http://www.moyersoftware.com";
    public static final String URL_LOG_IN = "http://moyersoftware.com/blank/log_in.php";
    public static final String URL_SIGN_IN = "http://moyersoftware.com/blank/sign_in.php";
    public static final String URL_SIGN_IN_SOCIAL
            = "http://moyersoftware.com/blank/sign_in_social.php";
    public static final String URL_GET_STATS
            = "http://moyersoftware.com/blank/get_user_stats.php";
    public static final String URL_GET_FRIENDS = "http://moyersoftware.com/blank/get_friends.php";
    public static final String URL_UPDATE_FRIEND_STATUS
            = "http://moyersoftware.com/blank/update_friend_status.php";
    public static final String URL_INVITE_FACEBOOK_FRIENDS
            = "http://moyersoftware.com/blank/invite_facebook_friends.php";
    public static final String URL_CHECK_PWD
            = "http://moyersoftware.com/blank/check_password.php";
    public static final String URL_CHANGE_PWD
            = "http://moyersoftware.com/blank/change_password.php";
    public static final String URL_CREATE_GAME
            = "http://moyersoftware.com/blank/create_game.php";
    public static final String URL_CLOSE_GAME
            = "http://moyersoftware.com/blank/close_game.php";
    public static final String URL_GET_TEAMS
            = "http://moyersoftware.com/blank/get_teams.php";
    public static final String URL_GET_PLAYERS
            = "http://moyersoftware.com/blank/get_players.php";
    public static final String URL_ADD_PLAYER
            = "http://moyersoftware.com/blank/add_player.php";
    public static final String URL_GET_GAMES
            = "http://moyersoftware.com/blank/get_games.php";
    public static final String URL_JOIN_GAME
            = "http://moyersoftware.com/blank/join_game.php";
    public static final String URL_LEAVE_GAME
            = "http://moyersoftware.com/blank/leave_game.php";
    public static final String URL_CHANGE_TEAM_COLOR
            = "http://moyersoftware.com/blank/change_team_color.php";
    public static final String URL_CHANGE_PLAYERS_TEAM
            = "http://moyersoftware.com/blank/change_players_team.php";
    public static final String URL_START_GAME
            = "http://moyersoftware.com/blank/start_game.php";
    public static final String URL_UPLOAD_CARDS
            = "http://moyersoftware.com/blank/upload_cards.php";
    public static final String URL_BEGIN_GAME
            = "http://moyersoftware.com/blank/begin_game.php";

    private static final String LOG_TAG = "BlankDebug";
    public static final String SEARCH_ID_REQUESTS = "Util:SEARCH_ID_REQUESTS";

    public static final String BROADCAST_JOINED_GAME = "com.moysof.hashtagnews:JOINED_GAME";
    public static final String BROADCAST_CREATED_GAME = "com.moysof.hashtagnews:CREATED_GAME";
    public static final String BROADCAST_CLOSED_GAME = "com.moysof.hashtagnews:CLOSED_GAME";
    public static final String BROADCAST_STARTED_GAME = "com.moysof.hashtagnews:STARTED_GAME";
    public static final String BROADCAST_BEGIN_GAME = "com.moysof.hashtagnews:BEGIN_GAME";
    public static final String TYPE_JOINED_GAME = "joined_game";
    public static final String TYPE_CREATED_GAME = "created_game";
    public static final String TYPE_CLOSED_GAME = "closed_game";
    public static final String TYPE_STARTED_GAME = "started_game";
    public static final String TYPE_BEGIN_GAME = "begin_game";

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
            String phone = ((TelephonyManager) BaseApplication.getAppContext().
                    getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
            return phone != null ? phone : "";
        } catch (Exception e) {
            return "";
        }
    }

    public static int convertDpToPixel(float dp) {
        Resources resources = BaseApplication.getAppContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * (metrics.densityDpi / 160f));
        return px;
    }

}
