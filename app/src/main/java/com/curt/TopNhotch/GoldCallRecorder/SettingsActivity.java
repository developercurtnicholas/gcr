package com.curt.TopNhotch.GoldCallRecorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 8/8/2016.
 */
public class SettingsActivity extends AppCompatActivity{

    private SettingsFragment fragment;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.settings_activity_layout);
        fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().add(R.id.settings_activity_lLayout, fragment).commit();
    }
}

