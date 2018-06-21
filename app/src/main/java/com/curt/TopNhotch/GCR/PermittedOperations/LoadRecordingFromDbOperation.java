package com.curt.TopNhotch.GCR.PermittedOperations;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;

import java.util.ArrayList;

/**
 * Created by Kurt on 6/16/2017.
 */

public  abstract class LoadRecordingFromDbOperation extends PermissionOperation {

    private LoadAction action;
    public ArrayList<Recording> recordings;

    public LoadRecordingFromDbOperation(Context context) {
        super(context);
    }

    public interface LoadAction{

        void loadActionGranted();
        void loadActionDenied();
    }

    @Override
    protected void granted(){
        thowExceptionIfActionNull();
        action.loadActionGranted();
    }

    @Override
    protected void denied(){
        thowExceptionIfActionNull();
        action.loadActionDenied();
    }

    protected void setLoadAction(LoadAction action){
        this.action = action;
    }

    private void thowExceptionIfActionNull(){
        if(action == null)
            throw new IllegalStateException("The load action was not set, please call setLoadAction");
    }

    @Override
    protected void callSetPermissionsHere() {

        setRequiredPermission(new String[]{

                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        });
    }

    public ArrayList<Recording> loadRecordingsFromDb(SortOrder order, RecordingType recordingtype){

        SQLiteDatabase db = RecordingsDbHelper.getReadableAndWriteableDB(context);
        String selection = RecordingsContract.Recordings.COL_SAVED + " = ?";
        Cursor c = db.query(
                RecordingsContract.Recordings.TABLE_NAME,
                GcrConstants.PROJECTION,
                recordingtype != null ? selection : null,
                recordingtype != null ? recordingtype.getType() : null,
                null,
                null,
                order.getOrder()
        );

        return Recording.createRecordingsFromCursor(c,context);
    }


    protected enum SortOrder{
        ASCENDING(RecordingsContract.Recordings.COL_LAST_MODIFIED + " ASC "),
        DESCENDING(RecordingsContract.Recordings.COL_LAST_MODIFIED + " DESC ");
        final String order;
        SortOrder(String order){
            this.order = order;
        }
        public String getOrder(){
            return this.order;
        }
    }

    protected enum RecordingType{
        SAVED(new String[]{"true"}),UNSAVED(new String[]{"false"});
        final String[] type;
        RecordingType(String[] type){
            this.type = type;
        }
        public String[] getType(){
            return this.type;
        }
    }
}
