package com.curt.TopNhotch.GCR.States.ApplicationStates;
import android.preference.PreferenceManager;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.PermittedOperations.CheckPasswordNeededOperation;
import com.curt.TopNhotch.GCR.PermittedOperations.CreateRecordingsDirectoryOperation;
import com.curt.TopNhotch.GCR.States.State;
import com.curt.TopNhotch.myapplication.R;

public class AppOpenedNoUI extends State{

    MainActivity activity;

    public AppOpenedNoUI(Context context, MainActivity activity) {
        super(context);
        this.activity = activity;
    }



    @Override
    protected void doAction(){

        setDefaultPreferenceValues();

        new CreateRecordingsDirectoryOperation(activity.getApplicationContext()).execute();

        CheckPasswordNeededOperation<MainActivity> checkPw = new CheckPasswordNeededOperation<>(
                activity
        );
        checkPw.execute();

    }



    public void setDefaultPreferenceValues(){

        PreferenceManager.setDefaultValues(activity, R.xml.settings, false);
        PreferenceManager.setDefaultValues(activity, R.xml.security_settings, false);
    }

    @Override
    protected void setPreReqs(Context context) {
        this.setNoPreReqs();
    }

    @Override
    protected void setKeyName() {
        this.KEY = "APP_OPENED";
    }
}
