package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.curt.TopNhotch.GCR.PermittedOperations.LoadAllRecordingsFromDbOperation;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;

import java.util.ArrayList;

/**
 * Created by Kurt on 9/24/2017.
 */

public class SqlLiteRecordingDataSource extends RecordingDataSource<SQLiteDatabase>{

    public SqlLiteRecordingDataSource(Context context,String Id, SQLiteDatabase database) {
        super(context,Id, database);
    }

    //TODO: we are returning true by default when we shold not. instead check what way we can decide when data is actually deleted from the db
    @Override
    public boolean delete(ArrayList<Recording> recordingsToDelete) {

        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";

        for (Recording recording : recordingsToDelete) {
            String[] arguments = {recording.path};
            dataSource.delete(RecordingsContract.Recordings.TABLE_NAME, selection, arguments);
        }

        return true;
    }

    @Override
    public ArrayList<Recording> getRecordings() {

        LoadAllRecordingsFromDbOperation op = new LoadAllRecordingsFromDbOperation(context);
        op.execute();
        return op.recordings;
    }
}
