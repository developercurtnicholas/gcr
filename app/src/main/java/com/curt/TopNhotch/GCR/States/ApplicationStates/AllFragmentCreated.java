package com.curt.TopNhotch.GCR.States.ApplicationStates;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.States.State;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.RecordingDataSource;
import com.curt.TopNhotch.GCR.Fragments.RecordingFragment;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Kurt on 6/16/2017.
 */

public class AllFragmentCreated extends State {

    MainActivity activity;
    public RecordingFragment fragment;

    public AllFragmentCreated(Context context,MainActivity activity,RecordingFragment fragment) {

        super(context);
        this.activity = activity;
        this.fragment = fragment;
    }

    @Override
    protected void doAction(){

        RecordingDataSource dataSource = activity.synchronizer.getDataSource("Database");
        dataSource.getRecordingsAsync(new RecordingDataSource.Listener() {
            @Override
            public void onRecordingsRetrieved(ArrayList<Recording> recordings) {
                fragment.setListWithAdapter(recordings, R.id.list_view);
            }
        });
    }

    @Override
    protected void setPreReqs(Context context) {
        this.addPreRequisite(new RepopulationComplete(context));
    }

    @Override
    protected void setKeyName() {

        this.KEY = "ALL_FRAGMENT_CREATED";
    }
}
