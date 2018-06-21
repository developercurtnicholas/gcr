package com.curt.TopNhotch.GCR.Utilities.Miscellaneous;

import android.content.Context;
import android.os.Environment;

import com.curt.TopNhotch.GCR.Utilities.UIUtils.DisplayMessages;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Curt on 1/29/2016.
 */
public class FileManager{

    Context context;

    public FileManager(Context context){
        this.context = context;
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean checkExternalStorageAvailable(){

        if (isExternalStorageWritable()){

            return true;
        }else{

            String msg = context.getResources().getString(R.string.No_External_Storage);
            DisplayMessages.showDialog(context,msg,"");
            return false;
        }
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
