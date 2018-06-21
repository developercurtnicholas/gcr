package com.curt.TopNhotch.GCR.States.ApplicationStates;

import com.curt.TopNhotch.GCR.States.State;

/**
 * Created by Kurt on 6/16/2017.
 */

public class RepopulationComplete extends State {


    public RepopulationComplete(Context context) {
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

        this.KEY = "DB_REPOPULATION_COMPLETE";
    }
}
