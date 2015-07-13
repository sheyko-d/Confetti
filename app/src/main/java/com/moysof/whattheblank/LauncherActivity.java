package com.moysof.whattheblank;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

public class LauncherActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getString("id", "").equals("")) {
            startActivity(new Intent(LauncherActivity.this,
                    LoadingActivity.class));
        } else {
            startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        }
        finish();
    }

}