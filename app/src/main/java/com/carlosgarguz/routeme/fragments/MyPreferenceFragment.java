package com.carlosgarguz.routeme.fragments;


import android.os.Bundle;

import android.preference.PreferenceFragment;



import com.carlosgarguz.routeme.R;

public class MyPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configuration_layout);
    }
}
