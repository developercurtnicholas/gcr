package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.content.Context;

import com.curt.TopNhotch.GCR.AsyncTasks.GetRecordingsAsyncTask;
import com.curt.TopNhotch.GCR.Models.Recording;
import java.util.ArrayList;

public abstract class RecordingDataSource<T> {

    protected String Id;
    protected T dataSource;
    protected  Context context;
    protected Listener listener;
    public static interface Listener{

       void onRecordingsRetrieved(ArrayList<Recording> recordings);
    }

    public RecordingDataSource(Context context,String Id, T dataSource){

        this.Id = Id;
        this.dataSource = dataSource;
        this.context = context;
    }

    public T getDataSource(){

        return dataSource;
    }

    public void setDataSource(T dataSource){
        this.dataSource = dataSource;
    }

    public abstract boolean delete(ArrayList<Recording> recordings);

    public abstract ArrayList<Recording> getRecordings();
    public void getRecordingsAsync(Listener listener){
        new GetRecordingsAsyncTask(this,listener).execute();
    }
}
