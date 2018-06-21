package com.curt.TopNhotch.GCR.Loaders;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.curt.TopNhotch.GCR.PermittedOperations.LoadAllRecordingsFromDbOperation;
import com.curt.TopNhotch.GCR.Models.Recording;

import java.util.ArrayList;

/**
 * Created by Kurt on 2/8/2016.
 */
//As a reminder to trace which methods are called and the sequence:
    //1)forceLoad()
    //2)loadInBackGround()
    //3)deliverResults()
    //4)onloadFinished()
public class RecordingLoader extends AsyncTaskLoader<ArrayList<Recording>>{


    private ArrayList<Recording> result;

    public RecordingLoader(Context context) {
        super(context);
    }

    @Override
    public ArrayList<Recording> loadInBackground() {

        LoadAllRecordingsFromDbOperation op = new LoadAllRecordingsFromDbOperation(getContext());
        op.execute();
        this.result = op.recordings;
        return this.result;
    }

    @Override
    public void deliverResult(ArrayList<Recording> r){
        if(isReset() && r != null){
            releaseResources(r);
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
        //if we have data as soon as we start we call deliver results to return the list of files from the directory to
        //onLoadFinished
        if(this.result != null) {
            deliverResult(result);
        }
    }

    protected void onStopLoading(){
        cancelLoad();
    }
    public void onCanceled(ArrayList<Recording> result){

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

    private void releaseResources(ArrayList<Recording> recordings){
        recordings = null;
    }
}
