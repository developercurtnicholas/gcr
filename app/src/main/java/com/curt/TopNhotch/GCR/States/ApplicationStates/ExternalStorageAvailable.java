package com.curt.TopNhotch.GCR.States.ApplicationStates;

import com.curt.TopNhotch.GCR.States.State;

/**
 * Created by Kurt on 6/17/2017.
 */

public class ExternalStorageAvailable extends State {

    public ExternalStorageAvailable(Context context) {
        super(context);
    }

    @Override
    protected void doAction() {

    }

    @Override
    protected void setPreReqs(Context context) {
        setNoPreReqs();
    }

    @Override
    protected void setKeyName() {
        this.KEY = "EXTERNAL_STORAGE_AVAILABLE";
    }
}
