package com.curt.TopNhotch.GCR.AsyncTasks;

import android.os.AsyncTask;

import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.RecordingDataSource;
import com.curt.TopNhotch.GCR.Models.Recording;

import java.util.ArrayList;

/**
 * Created by Kurt on 2/10/2018.
 */

public class GetRecordingsAsyncTask extends AsyncTask<Object,Integer,ArrayList<Recording>> {

    private RecordingDataSource dataSource;
    private RecordingDataSource.Listener listener;
    public interface Listener{

        public void onResult(ArrayList<Recording> recordings);
    }
    public GetRecordingsAsyncTask(RecordingDataSource dataSource,RecordingDataSource.Listener listener){
        this.dataSource = dataSource;
        this.listener = listener;
    }

    @Override
    protected ArrayList<Recording> doInBackground(Object... params) {

        return this.dataSource.getRecordings();
    }

    @Override
    protected void onPostExecute(ArrayList<Recording> recordings) {
        this.listener.onRecordingsRetrieved(recordings);
        super.onPostExecute(recordings);
    }
}

