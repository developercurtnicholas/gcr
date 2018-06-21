package com.curt.TopNhotch.GCR.PermittedOperations;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.curt.TopNhotch.GCR.Utilities.UIUtils.DisplayMessages;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 1/6/2018.
 */

/**
 * Use whenever trying to make a call through the dialer. It will call startactivity from the activity/fragment
 * to start make the call.
 */
public class MakeCallOperation extends PermissionOperation {


    Fragment fragmentFrom;
    Activity activityFrom;
    Intent intent;

    public MakeCallOperation(Context context, Fragment from, String number) {
        super(context);
        this.intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        fragmentFrom = from;
    }

    public MakeCallOperation(Context context, Activity from, String number){
        super(context);
        this.intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        activityFrom = from;
    }

    @Override
    protected void granted() {

        if(fragmentFrom != null){
            fragmentFrom.startActivity(this.intent);
        }
        if(activityFrom != null){
            activityFrom.startActivity(this.intent);
        }
    }



    @Override
    protected void denied() {

        DisplayMessages.showDialog(context,"",context.getResources().getString(R.string.call_permission_denied));
    }

    @Override
    protected void callSetPermissionsHere() {
        setRequiredPermission(new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO
        });
    }
}
