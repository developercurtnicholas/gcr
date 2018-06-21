package com.curt.TopNhotch.GCR.PermittedOperations;

import android.content.Context;

/**
 * Created by Kurt on 9/24/2017.
 */

public class LoadAllRecordingsFromDbOperation extends LoadRecordingFromDbOperation implements LoadRecordingFromDbOperation.LoadAction {

    public LoadAllRecordingsFromDbOperation(Context context) {
        super(context);
        this.setLoadAction(this);
    }

    @Override
    public void loadActionGranted() {
        this.recordings = this.loadRecordingsFromDb(SortOrder.DESCENDING,null);
    }

    //TODO: Remember to handle the load action was not granted
    @Override
    public void loadActionDenied(){

    }
}
