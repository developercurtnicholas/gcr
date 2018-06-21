package com.curt.TopNhotch.GCR.AsyncTasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.curt.TopNhotch.GCR.Fragments.RecordingListFragment;
import com.curt.TopNhotch.GCR.Models.Recording;

import java.util.ArrayList;

/**
 * Created by Kurt on 2/9/2018.
 */

public class ParseRecordingFromCursorTask extends AsyncTask<Object,Integer,ArrayList<Recording>> {

    private Listener listener;
    private RecordingListFragment fragment;
    private Cursor data;
    private Context context;

    public interface Listener{

        public void onResult(ArrayList<Recording> recordings,Cursor cursor,RecordingListFragment fragment);
    }

    public ParseRecordingFromCursorTask(RecordingListFragment fragment, Cursor data, Context context, Listener listener){
        this.listener = listener;
        this.data = data;
        this.fragment = fragment;
        this.context = context;
    }

    @Override
    protected ArrayList<Recording> doInBackground(Object... params) {
        ArrayList<Recording> recordings = Recording.createRecordingsFromCursor(data,context);
        return recordings;
    }

    @Override
    protected void onPostExecute(ArrayList<Recording> recordings) {
        super.onPostExecute(recordings);
        this.listener.onResult(recordings,this.data,this.fragment);
    }
}
