package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.content.Context;

import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.Adapters.RecordingListAdapter;

import java.util.ArrayList;

/**
 * Created by Kurt on 9/24/2017.
 */

public class AdapterRecordingDataSource extends RecordingDataSource<RecordingListAdapter> {

    public AdapterRecordingDataSource(Context context,String Id, RecordingListAdapter data) {
        super(context,Id, data);

    }

    @Override
    public boolean delete(ArrayList<Recording> recordingsToDelete){

        ArrayList<Recording> currentRecordings = dataSource.getDataSource();

        for (Recording recording : recordingsToDelete){
            currentRecordings.remove(recording);
        }
        dataSource.notifyDataSetChanged();
        return true;
    }

    @Override
    public ArrayList<Recording> getRecordings(){

        return dataSource.getDataSource();
    }
}
