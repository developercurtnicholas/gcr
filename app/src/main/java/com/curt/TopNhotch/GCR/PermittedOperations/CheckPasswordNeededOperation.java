package com.curt.TopNhotch.GCR.PermittedOperations;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Activities.EnterPasscodeActivity;

/**
 * Created by Kurt on 6/18/2017.
 */

public class CheckPasswordNeededOperation <T extends Activity> extends PermissionOperation {

    T activity;
    boolean pcCorrect = false;
    private SharedPreferences preferences;

    public CheckPasswordNeededOperation(T activiy){

        super(activiy.getApplicationContext());
        this.activity = activiy;
        preferences = PreferenceManager.getDefaultSharedPreferences(activiy.getApplicationContext());
    }

    @Override
    protected void granted() {

        //Intent coming from the password activity
        Intent passcodeIntent = activity.getIntent();

        //If from restart is true that means we should ask for pw cause all we did was restart the
        //activity
        boolean fromRestart = passcodeIntent.getBooleanExtra("fromrestart",false);
        //if the passcodeIntent is not null that means the password activity started this activity
        if (passcodeIntent != null) {
            //Check if the password entered was correct based on the boolean extra
            pcCorrect = passcodeIntent.getBooleanExtra("passcode_correct", false);
        }
        //If the password is not correct or is false and this is not from a restart then check to see
        //if we should launch the password activity
        if (!pcCorrect && !fromRestart) {
            boolean askForCode = preferences.getBoolean(GcrConstants.SECURITY_ON_OFF, false);
            if (askForCode) {
                //If askforcode is true that means we should launch the passcode activity
                Intent intent = new Intent(activity.getApplicationContext(), EnterPasscodeActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

    @Override
    protected void denied() {

    }

    @Override
    protected void callSetPermissionsHere() {

    }
}
