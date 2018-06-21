package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Kurt on 9/24/2017.
 */
public abstract class Synchronizer {

    protected Context context;

    protected ArrayList<RecordingDataSource> dataSources = new ArrayList<>();

    public Synchronizer(Context context){

        populateDataSources(context);
    }

    public void addDataSource(RecordingDataSource dataSource){

        this.dataSources.add(dataSource);
    }

    public RecordingDataSource getDataSource(String id){

        for(RecordingDataSource source : dataSources){

            if(source.Id == id){
                return source;
            }
        }

        return null;
    }

    public abstract void populateDataSources(Context context);
}
