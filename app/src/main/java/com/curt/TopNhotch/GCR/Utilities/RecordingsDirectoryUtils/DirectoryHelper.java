package com.curt.TopNhotch.GCR.Utilities.RecordingsDirectoryUtils;

import android.content.Context;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;

import java.io.File;

/**
 * Created by Kurt on 6/14/2017.
 */

public class DirectoryHelper {

    public static boolean recordingDirectoryHasFiles() {

        try{
            File directory = new File(GcrConstants.RECORDINGS_DIRECTORY);
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }



    public static boolean isRecordingsDirectoryCreated(){

        File dir = new File(GcrConstants.RECORDINGS_DIRECTORY);
        if(dir.exists() && dir.isDirectory()){
            return true;
        }else{
            return false;
        }
    }

    public static void createRecordingsDirectory() throws Exception{

        File dir = new File(GcrConstants.RECORDINGS_DIRECTORY);
        if(!isRecordingsDirectoryCreated()){
            try{
                dir.mkdir();
            }catch(Exception e){
                throw new Exception("Directory Not Created: " + e.getMessage());
            }
        }

    }


    public static File[] getRecordingsList(){
        return new File(GcrConstants.RECORDINGS_DIRECTORY).listFiles();
    }

    public static boolean shouldRepopulate(Context context){
        if(recordingDirectoryHasFiles() && !allCallsPopulated(context)){
            return true;
        }else{
            return false;
        }
    }


    public static boolean allCallsPopulated(Context context){

        long dbCount = RecordingsDbHelper.getRecordingCount(context);
        int dirCount = DirectoryHelper.getRecordingsList().length;

        if(dbCount == dirCount){
            return true;
        }else{
            return false;
        }
    }
}
