package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Adapters.RecordingListAdapter;
import com.curt.TopNhotch.myapplication.R;

import java.io.File;
import java.util.ArrayList;

public class RecordingManager{

    public static String FROM_RESTART_KEY = "fromrestart";
    private Context context;
    private DeleteListener deleteListener = null;
    private Activity activity;
    private ArrayList<Recording> recordingsToDelete;
    private RecordingListAdapter adapter = null;

    public interface DeleteListener{
        void deleted();
        void canceled();
    }

    private DialogInterface.OnClickListener deleteConfirm = new DialogInterface.OnClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int which){
            confirmDelete();
        }
    };

    private DialogInterface.OnClickListener deleteCancel = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which){
            cancelDelete();
        }
    };

    public RecordingManager(Activity activity,RecordingListAdapter adapter){

        this.context = activity.getApplicationContext();
        this.adapter = adapter;
        this.activity = activity;
    }

    public RecordingManager(Context context){
        this.context = context;
    }

    public  Intent Share(String type, String path){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        intent.setType(type);
        return intent;
    }
    public  Intent ShareMultiple(ArrayList<Uri> paths){

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_STREAM,paths);
        intent.setType("audio/*");
        return intent;
    }

    public void delete(ArrayList<Recording> recordings,DeleteListener listener){

        recordingsToDelete = recordings;
        deleteListener = listener;
        promptDelete();
    }

    private void confirmDelete(){

        if(recordingsToDelete == null){return;}
        try {
            ((MainActivity)activity).synchronizer.delete(recordingsToDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteListener.deleted();
        nullifyPathAndListener();

    }

    private void cancelDelete(){

        deleteListener.canceled();
        nullifyPathAndListener();
    }
    private void nullifyPathAndListener(){
        recordingsToDelete = null;
        deleteListener = null;
    }
    private void promptDelete(){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);


        builder.setMessage(R.string.delete_message)
                .setTitle(R.string.delete_title)
                .setNegativeButton(R.string.No,deleteCancel)
                .setPositiveButton(R.string.Yes,deleteConfirm);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void restart(Activity activity){

        if(activity == null)
                throw new NullPointerException("Activity is null");

        activity.finish();
        Intent intent = new Intent(activity,activity.getClass());
        intent.putExtra(FROM_RESTART_KEY, true);
        activity.startActivity(intent);
    }

    private void execute(ArrayList<String> paths,boolean save){
        //Save
        SQLiteDatabase db = new RecordingsDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.Recordings.COL_SAVED, save ? "true" : "false" );
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

    public void save(ArrayList<String> paths,Context context){
        execute(paths,true);
    }

    public  void unsave(ArrayList<String> paths){
        execute(paths,false);
    }

    public  void unSaveAndRestartActivity(ArrayList<String> paths, Activity activity){
        unsave(paths);
        restart(activity);
    }

    public void saveAndRestartActivity(ArrayList<String> paths, Activity activity){

        save(paths,activity.getApplicationContext());
        restart(activity);
    }
}
