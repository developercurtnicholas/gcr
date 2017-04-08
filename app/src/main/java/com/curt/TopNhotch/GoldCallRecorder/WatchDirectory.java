package com.curt.TopNhotch.GoldCallRecorder;

import android.database.sqlite.SQLiteDatabase;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Curt on 4/27/2016.
 */
public class WatchDirectory extends FileObserver {

    private String directoryPath = "";
    private FileLoader loader;
    private AppCompatActivity activity;
    private SQLiteDatabase db;
    private RecordingsDbHelper dbHelper;

    public WatchDirectory(String path,FileLoader loader,AppCompatActivity activity) {
        super(path);

        dbHelper = new RecordingsDbHelper(activity.getApplicationContext());
        db = dbHelper.getWritableDatabase();

        this.directoryPath = path;//The path to the directory we are going to watch for changes
        this.loader = loader;//The loader that we are going to call forceLoad() on when a change is detected
        this.activity = activity;//Because forceLoad() should be called on the UI thread we need a referece to the activity
        //So that we can call activit.runOnUiThread()
    }

    @Override
    public void onEvent(int event, String path) {

        //New File was created in the directory
        if(event == 256){
            loader.forceLoad();
        }
        //A file was moved from the directory
        if(event == 64){

        }
        //A file was moved to the directory
        if(event == 128){

        }
        //A File was deleted from the directory
        if(event == 512){
            //Delete that file from the database
            dbHelper = new RecordingsDbHelper(activity.getApplicationContext());
            db = dbHelper.getWritableDatabase();
            String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
            String[] args = {GcrConstants.RECORDINGS_DIRECTORY+"/"+path};
            db.delete(RecordingsContract.Recordings.TABLE_NAME, selection, args);
            loader.forceLoad();
        }
    }
}
