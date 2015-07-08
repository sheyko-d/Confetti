package com.moysof.whattheblank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class LoadingActivity extends AppCompatActivity {

    private Button mBtnEmail;
    private Button mBtnGoogle;
    private Button mBtnFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        mBtnEmail = (Button) findViewById(R.id.loading_button_email);
        mBtnGoogle = (Button) findViewById(R.id.loading_button_google);
        mBtnFacebook = (Button) findViewById(R.id.loading_button_facebook);

        mBtnEmail.setOnClickListener(mLoginClickListener);
        mBtnGoogle.setOnClickListener(mLoginClickListener);
        mBtnFacebook.setOnClickListener(mLoginClickListener);
    }

    View.OnClickListener mLoginClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.loading_button_email) {
                loginEmail();
            } else if (v.getId() == R.id.loading_button_google) {
                loginGoogle();
            } else {
                loginFacebook();
            }
        }
    };

    private void loginEmail() {
        // TODO: Replace with real sign in mechanism
        onLoginSuccess();
    }

    private void loginGoogle() {
        // TODO: Replace with real sign in mechanism
        onLoginSuccess();
    }

    private void loginFacebook() {
        // TODO: Replace with real sign in mechanism
        onLoginSuccess();
    }

    private void onLoginSuccess(){
        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
        finish();
    }

}
