package com.moysof.whattheblank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

    private TextView mNameTxt;
    private TextView mUsernameTxt;
    private TextView mPhoneTxt;
    private TextView mEmailTxt;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        mNameTxt = (TextView) rootView.findViewById(R.id.settings_name_txt);
        mUsernameTxt = (TextView) rootView.findViewById(R.id.settings_username_txt);
        mPhoneTxt = (TextView) rootView.findViewById(R.id.settings_phone_txt);
        mEmailTxt = (TextView) rootView.findViewById(R.id.settings_email_txt);

        loadUserInfo();

        return rootView;
    }

    private void loadUserInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mNameTxt.setText(prefs.getString("name", ""));
        mUsernameTxt.setText(prefs.getString("username", ""));
        mPhoneTxt.setText(prefs.getString("phone", ""));
        mEmailTxt.setText(prefs.getString("email", ""));
    }

}