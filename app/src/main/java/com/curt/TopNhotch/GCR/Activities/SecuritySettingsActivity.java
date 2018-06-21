package com.curt.TopNhotch.GCR.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.curt.TopNhotch.GCR.Fragments.SecuritySettingsFragment;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 12/20/2016.
 */
public class SecuritySettingsActivity extends AppCompatActivity{
    private SecuritySettingsFragment fragment;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.security_settings_activity_layout);
        fragment = new SecuritySettingsFragment();
        getFragmentManager().beginTransaction().add(R.id.securtity_settings_activity_layout, fragment).commit();
    }
}
