package com.curt.TopNhotch.GCR.Utilities.UIUtils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

public class DisplayMessages {

    public static void showDialog(Context context,String msg,String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.create();
        builder.show();
    }
}
