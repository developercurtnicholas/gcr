package com.curt.TopNhotch.GoldCallRecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

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

        if(recording_on_off){
            this.intent = intent;
            this.context = context;
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
    }

    @Override
    public void onIdle() {
    //Listen for no events on the telephony manager
        tManager.listen(pstate,PhoneStateListener.LISTEN_NONE);
        tManager = null;
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
