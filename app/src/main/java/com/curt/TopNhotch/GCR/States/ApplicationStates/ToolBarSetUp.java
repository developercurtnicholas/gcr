package com.curt.TopNhotch.GCR.States.ApplicationStates;

import android.graphics.Color;
import android.support.v7.widget.Toolbar;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.States.State;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 6/16/2017.
 */

public class ToolBarSetUp extends State {

    MainActivity activity;
    private Toolbar toolbar;

    public ToolBarSetUp(Context context,MainActivity activity) {
        super(context);
        this.activity = activity;
    }

    public Toolbar getToolbar(){
        return this.toolbar;
    }
    @Override
    protected void doAction() {

        toolbar = (Toolbar) activity.findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        activity.setSupportActionBar(toolbar);
    }

    @Override
    protected void setPreReqs(Context context) {
        setNoPreReqs();
    }

    @Override
    protected void setKeyName() {
        this.KEY = "TOOL_BAR_SET_UP";
    }
}
