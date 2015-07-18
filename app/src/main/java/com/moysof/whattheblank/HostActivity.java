package com.moysof.whattheblank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import com.moysof.whattheblank.adapter.HostSpinnerAdapter;


public class HostActivity extends AppCompatActivity {

    private HostSpinnerAdapter mSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        findViewById(R.id.join_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSpinnerAdapter = new HostSpinnerAdapter(this);
        ((Spinner) findViewById(R.id.host_teams_spinner)).setAdapter(mSpinnerAdapter);
        ((Spinner) findViewById(R.id.host_players_spinner)).setAdapter(mSpinnerAdapter);
        ((Spinner) findViewById(R.id.host_cards_spinner)).setAdapter(mSpinnerAdapter);
        ((Spinner) findViewById(R.id.host_time_spinner)).setAdapter(mSpinnerAdapter);
    }

}
