package com.curt.TopNhotch.GCR.States.ApplicationStates;
import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.PermittedOperations.SetNavigationDrawerOperation;
import com.curt.TopNhotch.GCR.States.State;

public class StartNavigationDrawerSetUp extends State {

    MainActivity activity;

    public StartNavigationDrawerSetUp(Context context, MainActivity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void doAction() {
        SetNavigationDrawerOperation operation = new SetNavigationDrawerOperation(activity);
        operation.execute();
    }

    @Override
    protected void setPreReqs(Context context) {
        setNoPreReqs();
    }

    @Override
    protected void setKeyName() {
        this.KEY = "START_NAVIGATION_DRAWER_SET_UP";
    }
}
