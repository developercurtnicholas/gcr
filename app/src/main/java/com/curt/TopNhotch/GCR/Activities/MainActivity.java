package com.curt.TopNhotch.GCR.Activities;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.curt.TopNhotch.GCR.PermittedOperations.RepopulationOperation;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.States.ApplicationStates.AllFragmentCreated;
import com.curt.TopNhotch.GCR.States.ApplicationStates.AppOpenedNoUI;
import com.curt.TopNhotch.GCR.States.ApplicationStates.ExternalStorageAvailable;
import com.curt.TopNhotch.GCR.States.ApplicationStates.NavigationDrawerSetUp;
import com.curt.TopNhotch.GCR.States.ApplicationStates.RepopulationComplete;
import com.curt.TopNhotch.GCR.States.ApplicationStates.StartNavigationDrawerSetUp;
import com.curt.TopNhotch.GCR.States.ApplicationStates.ToolBarSetUp;
import com.curt.TopNhotch.GCR.States.State;
import com.curt.TopNhotch.GCR.Utilities.UIUtils.TabCreator;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.RecordingSynchronizer;
import com.curt.TopNhotch.GCR.Fragments.RecordingFragment;
import com.curt.TopNhotch.GCR.Fragments.DialerFragment;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.myapplication.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    private SQLiteDatabase writeableDb;
    private TabLayout tabLayout;
    private ViewPager pager;
    public DrawerLayout drawerlayout;
    public ActionBarDrawerToggle drawerToggle;
    public NavigationView navView;
    public RecordingSynchronizer synchronizer;


    //Application States and Context
    private State.Context applicationState = new State.Context();
    private AppOpenedNoUI appOpenedNoUI = new AppOpenedNoUI(applicationState,this);
    public NavigationDrawerSetUp navigationDrawerSetUp = new NavigationDrawerSetUp(applicationState,this);
    public StartNavigationDrawerSetUp startNavigationDrawerSetUp = new StartNavigationDrawerSetUp(applicationState,this);
    public ToolBarSetUp toolBarSetUp;
    private AllFragmentCreated allFragmentCreated;
    public RepopulationComplete repopcomplete = new RepopulationComplete(applicationState);
    public ExternalStorageAvailable externalStorageAvailable = new ExternalStorageAvailable(applicationState);
    public TabCreator creator;

    @Override
    public void onCreate(Bundle saved){
        appOpenedNoUI.execute();
        super.onCreate(saved);
        setContentView(R.layout.drawerlayout);
        onActivityOnCreateCalled();
    }

    public void setupNavigationDrawer() {

        startNavigationDrawerSetUp.execute();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        new RepopulationOperation(this).execute();
        drawerToggle.syncState();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    public void setUpPagerWithAdapter(){

        pager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.all_saved);
        ArrayList<TabCreator.Tab> tabs = new ArrayList<>();
        tabs.add(new TabCreator.Tab(new RecordingFragment(),R.layout.recordings_tab));
        tabs.add(new TabCreator.Tab(new DialerFragment(),R.layout.dialer_tab));
        creator = new TabCreator(pager,tabLayout,tabs,getSupportFragmentManager());
    }

    /** */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    //Set up the toolbar by calling set support action bar
    public void setToolBar() {

        toolBarSetUp = new ToolBarSetUp(applicationState,this);
        toolBarSetUp.execute();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    public boolean onQueryTextChange(String newText){

        String[] args = {

                "%" + newText + "%",
                "%" + newText + "%",
                "%" + newText + "%",
                tabLayout.getSelectedTabPosition() == 0 ? "false" : "true"
        };

        //SELECT * FROM RECORDINGS WHERE ( (NUMBER LIKE ? OR NOTE LIKE ? OR CONTACT_NAME LIKE ?) AND (SAVED = ? )) ORDER BY CONTACT_NAME ASC
        String sql =
                "SELECT * FROM " + RecordingsContract.Recordings.TABLE_NAME +
                        " WHERE ( (" +
                        RecordingsContract.Recordings.COL_NUMBER + " LIKE ? OR "+
                        RecordingsContract.Recordings.COL_NOTE + " LIKE ? OR " +
                        RecordingsContract.Recordings.COL_CONTACT_NAME + " LIKE ?) AND ("+
                        RecordingsContract.Recordings.COL_SAVED + " = ? " +
                        ")) ORDER BY " + RecordingsContract.Recordings.COL_CONTACT_NAME + " ASC";

        Cursor c = writeableDb.rawQuery(sql, args);
        ArrayList<Recording> recordings = Recording.createRecordingsFromCursor(c,getApplicationContext());


        return false;
    }

    //Free UI resources
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            releaseUIResources();
        }
    }

    public void releaseUIResources() {

    }


    private RecordingSynchronizer createRecordingSynchronizer(){

        Context context = getApplicationContext();
        RecordingSynchronizer synchronizer = new RecordingSynchronizer(context);
        return synchronizer;
    }

    //after set content view is called
    public void onActivityOnCreateCalled(){
        this.synchronizer = createRecordingSynchronizer();
        writeableDb = RecordingsDbHelper.getReadableAndWriteableDB(getApplicationContext());
        setToolBar();
        setUpPagerWithAdapter();
        setupNavigationDrawer();
    }


    //Called when the All Recordings Fragment view is created
    public void onAllFragmentCreated(RecordingFragment fragment){
        //The fragment may be created but does not go into this state unless the database is populated
        //Check if all fragment created was already executed
        if(this.allFragmentCreated == null){
            try{
                this.allFragmentCreated = new AllFragmentCreated(applicationState,this,fragment);
                allFragmentCreated.execute();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Log.i("State:","Already in that state");
            fragment.setListWithAdapter(synchronizer.getDataSource("Adapter").getRecordings(),R.id.list_view);
        }
    }

}