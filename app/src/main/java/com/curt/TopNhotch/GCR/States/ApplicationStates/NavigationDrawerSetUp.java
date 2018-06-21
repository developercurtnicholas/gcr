package com.curt.TopNhotch.GCR.States.ApplicationStates;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.States.State;

/**
 * Created by Kurt on 6/16/2017.
 */

public class NavigationDrawerSetUp extends State {

    MainActivity activity;
    public NavigationDrawerSetUp(Context context,MainActivity activity) {
        super(context);
        this.activity = activity;
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
        this.KEY = "SET_UP_NAVIGATION_DRAWER";
    }
}
