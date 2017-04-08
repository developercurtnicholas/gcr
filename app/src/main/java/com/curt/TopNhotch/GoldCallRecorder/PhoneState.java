package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by Kurt on 1/27/2016.
 */
public class PhoneState extends PhoneStateListener {
    Context context;
    CallState mInterface;
    PhoneState(Context context,CallState i){
        this.context = context;
        this.mInterface = i;
    }

    @Override
    public void onCallStateChanged(int state,String number){
        super.onCallStateChanged(state,number);
        if(TelephonyManager.CALL_STATE_OFFHOOK == state){
            mInterface.onOffHook();
        }
        if(TelephonyManager.CALL_STATE_IDLE == state){
            mInterface.onIdle();
        }
        if(TelephonyManager.CALL_STATE_RINGING == state){
            mInterface.onRinging();
        }
    }
}
