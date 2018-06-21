package com.curt.TopNhotch.GCR.Utilities.DbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;

import java.util.ArrayList;

/**
 * Created by Kurt on 6/14/2017.
 */

public class RecordingSaverUtility {

    public static void unSaveRoutine(String path, Context context){

        //unsave
        setSaveValueAndUpdate(SaveValues.UNSAVE,new String[]{path},context);
    }

    public enum SaveValues{
        SAVE("true"),UNSAVE("false");

        private final String value;

        SaveValues(String value){
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }
    }

    private static void setSaveValueAndUpdate(SaveValues value,String paths[],Context context){

        //Save
        SQLiteDatabase db = new RecordingsDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.Recordings.COL_SAVED, value.getValue());
        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
        for(String path : paths){
            String[] args = {path};
            db.update(
                    RecordingsContract.Recordings.TABLE_NAME,
                    values,
                    selection,
                    args
            );
        }
        db.close();
    }

    public static void saveRoutine(String path,Context context){
        //Save
        setSaveValueAndUpdate(SaveValues.SAVE,new String[]{path},context);
    }

    public static void saveRoutineMultipe(ArrayList<String> paths, Context context){
        String[] pathsArray = new String[paths.size()];
        paths.toArray(pathsArray);
        setSaveValueAndUpdate(SaveValues.SAVE,pathsArray,context);
    }
}
