package com.curt.TopNhotch.GCR.States.ApplicationStates;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.PermittedOperations.LoadSavedRecordingOperation;
import com.curt.TopNhotch.GCR.States.State;
import com.curt.TopNhotch.GCR.Fragments.SavedFragment;

/**
 * Created by Kurt on 6/16/2017.
 */

public class SavedFragmentCreated extends State{


    MainActivity activity;
    public SavedFragment fragment;

    public SavedFragmentCreated(Context context, MainActivity activity, SavedFragment savedFragment) {
        super(context);
        this.activity = activity;
        this.fragment = savedFragment;
    }

    @Override
    protected void doAction() {

        LoadSavedRecordingOperation operation = new LoadSavedRecordingOperation(activity,fragment);
        operation.execute();
    }

    @Override
    protected void setPreReqs(State.Context context){
        this.addPreRequisite(new RepopulationComplete(context));
    }

    @Override
    protected void setKeyName() {
        this.KEY = "SAVED_FRAGMENT_CREATED";
    }
}
