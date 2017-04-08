package com.curt.TopNhotch.GoldCallRecorder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.curt.TopNhotch.myapplication.R;


/**
 * Created by Kurt on 8/8/2016.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;

    private String recording_source_summary;
    private String inbox_size_summary;
    @Override
    public void onCreate(Bundle saved){
        super.onCreate(saved);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addPreferencesFromResource(R.xml.settings);
        recording_source_summary  = getResources().getString(R.string.recording_source_string);
        inbox_size_summary  = getResources().getString(R.string.inbox_size_string);

    }
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(GcrConstants.RECORDING_SOURCE)) {
            findPreference(key).setSummary(recording_source_summary + "\n"+sharedPreferences.getString(
                    GcrConstants.RECORDING_SOURCE,"")+"");
        }
        if(key.equals(GcrConstants.INBOX_SIZE)){
            findPreference(key).setSummary(inbox_size_summary + "\n"+sharedPreferences.getString(GcrConstants.INBOX_SIZE,"")+"");
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findPreference(GcrConstants.RECORDING_SOURCE).setSummary(recording_source_summary + "\n" +prefs.getString(
                GcrConstants.RECORDING_SOURCE, "")+ "");
        findPreference(GcrConstants.INBOX_SIZE).setSummary(inbox_size_summary + " \n" + prefs.getString(GcrConstants.
                INBOX_SIZE, "")+"");
    }
}
