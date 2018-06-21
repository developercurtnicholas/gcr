package com.curt.TopNhotch.GCR.PermittedOperations;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Activities.SecuritySettingsActivity;
import com.curt.TopNhotch.GCR.Activities.SettingsActivity;
import com.curt.TopNhotch.myapplication.R;



public class SetNavigationDrawerOperation extends PermissionOperation {

    private MainActivity activity;


    public SetNavigationDrawerOperation(MainActivity activity) {
        super(activity.getApplicationContext());
        this.activity = activity;
    }

    @Override
    protected void granted() {

        activity.drawerlayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        activity.drawerToggle = new ActionBarDrawerToggle(activity, activity.drawerlayout, R.string.Open, R.string.Close) {
        };
        activity.drawerlayout.setDrawerListener(activity.drawerToggle);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.navView = (NavigationView) activity.findViewById(R.id.naviagtion_view);
        activity.navView.setItemIconTintList(null);
        activity.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.settings) {
                    activity.drawerlayout.closeDrawers();
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    activity.startActivity(intent);
                }
                if (item.getItemId() == R.id.security) {
                    activity.drawerlayout.closeDrawers();
                    Intent intent = new Intent(activity, SecuritySettingsActivity.class);
                    activity.startActivity(intent);
                }
                return false;
            }
        });
        activity.navigationDrawerSetUp.execute();
    }

    @Override
    protected void denied() {

    }

    @Override
    protected void callSetPermissionsHere() {

    }
}
