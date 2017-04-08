package com.curt.TopNhotch.GoldCallRecorder;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import java.io.File;

/**
 * Created by Kurt on 2/8/2016.
 */
//TODO:The loader should load from the database

//As a reminder to trace which methods are called and the sequence:
    //1)forceLoad()
    //2)loadInBackGround()
    //3)deliverResults()
    //4)onloadFinished()
public class FileLoader extends AsyncTaskLoader<Cursor>{

    private File directory;
    private Cursor result;
    private DirectoryAdapter adapter;
    private static WatchDirectory watch;
    private RecordingsDbHelper dbHelper;
    SQLiteDatabase db;
    AppCompatActivity activity;


    public FileLoader(AppCompatActivity activity, File directory,DirectoryAdapter adapter) {
        super(activity);
        this.directory = directory;
        this.adapter = adapter;
        this.activity = activity;
    }


    @Override
    public Cursor loadInBackground() {

        dbHelper = new RecordingsDbHelper(getContext());
        db = dbHelper.getReadableDatabase();
        String sortOrder = RecordingsContract.Recordings.COL_LAST_MODIFIED + " DESC ";
        String selection = RecordingsContract.Recordings.COL_SAVED + " = ?";
        String[] arg = {"false"};
        String[] arg2 = {"true"};
        int tabSelected = GcrConstants.TAB_SELECTED;

        result = db.query(
                RecordingsContract.Recordings.TABLE_NAME,
                GcrConstants.PROJECTION,
                selection,
                tabSelected == 0? arg : arg2,
                null,
                null,
                sortOrder
        );
        return result;
    }

    @Override
    public void deliverResult(Cursor r){
        if(isReset()){
            if(r != null){
                releaseResources(r);
            }
        }
        if(isStarted()){
            super.deliverResult(r);
        }
        //If we have old data release it(Throw it away)
        if(result != null){
            releaseResources(result);
        }
    }

    @Override
    protected void onStartLoading(){
        if(isStarted()){
        }
        //if we have data as soon as we start we call deliver results to return the list of files from the directory to
        //onLoadFinished
        if(this.result != null) {
            deliverResult(result);
        }

        //This is where you implement a mechanism to watch the data source for changes
        //In our case we are going to watch a directory for changes
        if(watch == null){
            watch = new WatchDirectory(this.directory.toString(),this,this.activity);
            watch.startWatching();
        }else{
            watch.stopWatching();
            watch = null;
            watch = new WatchDirectory(this.directory.toString(),this,this.activity);
            watch.startWatching();
        }
    }

    protected void onStopLoading(){
        cancelLoad();
    }
    public void onCanceled(Cursor result){

        super.onCanceled(result);

        releaseResources(result);
    }
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if(result != null){

            releaseResources(result);
            result = null;
        }
    }

    private void releaseResources(Cursor c){
        c = null;
    }
}
