package com.curt.TopNhotch.GCR.AsyncTasks;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.LookUpContact;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.RandomColorGenerator;
import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Utilities.RecordingsDirectoryUtils.DirectoryHelper;
import com.curt.TopNhotch.myapplication.R;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Kurt on 6/14/2017.
 */


public class RepopulateTask extends AsyncTask<Void, Integer, Void>{



    private Context context;
    private Activity activity;
    private ProgressBar progressBar;
    private File recordingsDirectory = new File(GcrConstants.RECORDINGS_DIRECTORY);
    private float amountRecordingsInserted = 0;
    private float amountToInsert;
    private SQLiteDatabase db;
    private RepopulateTask THIS = this;

    private  RepopulationEvents events;

    public RepopulateTask(Activity activity,RepopulationEvents events) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.db = RecordingsDbHelper.getReadableAndWriteableDB(context);
        this.events = events;
    }



    public interface RepopulationEvents{

        void onRepopulationComplete();
        void onRepopulationStarted();
    }

    @Override
    protected Void doInBackground(Void...params) {
        if(isCancelled()){
            return null;
        }

        //If the directory somehow has less files than the local db then truncate the local db
        //and do a full repopulation
        int dirLenght = DirectoryHelper.getRecordingsList().length;
        long dbLenght = RecordingsDbHelper.getRecordingCount(context);
        if(dirLenght < dbLenght){
            if(!RecordingsDbHelper.truncate(context)){
                return null;
            }
        }
        amountRecordingsInserted = RecordingsDbHelper.getRecordingCount(context);
        events.onRepopulationStarted();
        insertAllCallsIntoDbAndUpdateProgress();
        return null;
    }

    private void insertAllCallsIntoDbAndUpdateProgress(){

        String fileName = "";
        for (File f : recordingsDirectory.listFiles()) {
            if(isCancelled()){
                break;
            }
            fileName = f.getName();
            Recording recording = Recording.createRecordingFromFileName(fileName);
            if(isRecordingParsed(recording)){
                insertParsedValuesInDb(buildContentValues(recording));
            }else{
                try{
                    deleteFile(f);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            updateProgress();
        }
    }

    private void deleteFile(File badFile) throws Exception{
        if(!badFile.delete())
            throw new Exception("Could Not Delete File: " + badFile.getAbsolutePath() + "/" + badFile.getName());
    }

    private boolean isRecordingParsed(Recording recording){

        if(recording == null){
            return false;
        }else{
            return true;
        }
    }

    private void updateProgress(){

        amountRecordingsInserted++;
        amountToInsert = recordingsDirectory.listFiles().length;
        int p = Math.round((amountRecordingsInserted/ amountToInsert) * 100);
        publishProgress(p);
    }

    private ContentValues buildContentValues(Recording recording){


        ContentValues values = new ContentValues();

        String name = LookUpContact.getNameByNumber(recording.number, context);
        if (name != null) {
            values.put(RecordingsContract.Recordings.COL_CONTACT_NAME, name);
        } else {
            values.put(RecordingsContract.Recordings.COL_CONTACT_NAME, "");
        }
        values.put(RecordingsContract.Recordings.COL_RECORDING_PATH, recording.path);
        values.put(RecordingsContract.Recordings.COL_NUMBER, recording.number);
        values.put(RecordingsContract.Recordings.COL_LAST_MODIFIED, recording.timeStamp);
        values.put(RecordingsContract.Recordings.COL_CALL_DIRECTION, recording.callDirection);
        values.put(RecordingsContract.Recordings.COL_SAVED, "false");
        values.put(RecordingsContract.Recordings.COL_COLOR, RandomColorGenerator.generateRandomColor());

        return values;
    }

    private boolean insertParsedValuesInDb(ContentValues values){

         try {
             long id = db.insert(RecordingsContract.Recordings.TABLE_NAME, null, values);
             return RecordingsDbHelper.wasRowInserted(id);
         } catch (Exception e) {
             return false;
         }
    }

    @Override
    protected void onPreExecute() {
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        if(progressBar != null){
            progressBar.setMax(100);
        }else{
            this.cancel(true);

            try {
                throw new Exception("There needs to be a progress bar with the id: progressBar , present in" +
                        "The activities layout in order to run this task");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        events.onRepopulationComplete();
        context.getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("repopulate",true);

        super.onPostExecute(aVoid);
    }



    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        THIS = null;
        super.onCancelled();
    }


}
