package com.curt.TopNhotch.GCR.BroadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.curt.TopNhotch.GCR.Services.CallRecorderService;
import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Utilities.AudioRecording.CallState;
import com.curt.TopNhotch.GCR.Utilities.AudioRecording.PhoneState;

/**
 * Created by Kurt on 1/24/2016.
 */
public class BackGroundRecorder extends BroadcastReceiver implements CallState {
    private TelephonyManager tManager;
    private PhoneState pstate;
    private Intent intent;
    private Context context;
    private SharedPreferences prefs;
    @Override
    public void onReceive(Context context, Intent intent) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean recording_on_off = prefs.getBoolean(GcrConstants.RECORDING_ON_OFF,true);
        this.intent = intent;
        this.context = context;

        /**
         * This record boolean can decides if we record or not and can be set in the following places:
         * <br>If the record call button is pressed on the dialer then the value is true
         * <br><br>If the regular call button is pressed on the dialer then the value is false*/
        SharedPreferences p = context.getSharedPreferences(GcrConstants.DIALER_RECORDING_SETTINGS,Context.MODE_PRIVATE);
        SharedPreferences.Editor e = context.getSharedPreferences(GcrConstants.DIALER_RECORDING_SETTINGS,Context.MODE_PRIVATE).edit();
        boolean fromDialer = p.getBoolean(GcrConstants.DIALER_CALL_FROM_DIALER,false);
        boolean record = p.getBoolean(GcrConstants.DIALER_SHOULD_RECORD,true);

        if(fromDialer && record){
            record();
            e.putBoolean(GcrConstants.DIALER_CALL_FROM_DIALER,false);
            e.commit();
        }else if(fromDialer && !record){
            e.putBoolean(GcrConstants.DIALER_CALL_FROM_DIALER,false);
            e.commit();
            //Don't record
        }else if(!fromDialer && recording_on_off){
            record();
        }
    }

    public void record(){

        //An outgoing call was placed
        if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")){
            Intent mIntent = new Intent(context,CallRecorderService.class);
            mIntent.putExtras(intent.getExtras());
            mIntent.putExtra("CallDirection", "O");
            context.startService(mIntent);
            return;
        }else{
            //Set up the telephony manager to listen for the call state
            tManager = (TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
            pstate = new PhoneState(context,this);
            tManager.listen(pstate, PhoneStateListener.LISTEN_CALL_STATE);
            String action = intent.getAction();
        }
    }

    @Override
    public void onIdle() {
    //Listen for no events on the telephony manager
        //Patched with try catch...threw weird exception
        try{
            tManager.listen(pstate,PhoneStateListener.LISTEN_NONE);
            tManager = null;
        }catch (Exception e){

        }

    }

    @Override
    public void onOffHook() {
        //Listen for no events on the telephony manager
        tManager.listen(pstate,PhoneStateListener.LISTEN_NONE);
        tManager = null;
    }

    //Incoming phone call
    @Override
    public void onRinging() {
        //Listen for no events on the telephony manager
        tManager.listen(pstate,PhoneStateListener.LISTEN_NONE);
        tManager = null;
        Log.i("##INFO","Incoming call");
        //Start the service
        Intent mIntent = new Intent(context,CallRecorderService.class);
        mIntent.putExtras(intent.getExtras());
        mIntent.putExtra("CallDirection", "I");
        mIntent.putExtra("incoming_num",intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
        context.startService(mIntent);
    }
}
