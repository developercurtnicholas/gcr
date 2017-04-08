package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Context;
import android.os.Environment;

/**
 * Created by Curt on 1/29/2016.
 */
public class FileManager{

    Context context;

    FileManager(Context context){
        this.context = context;
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
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
