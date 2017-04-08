package com.curt.TopNhotch.GoldCallRecorder;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curt.TopNhotch.myapplication.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, FragmentListManager {

    DirectoryAdapter adapter;
    String FILE_PATH = GcrConstants.RECORDINGS_DIRECTORY;
    FileLoader loader;
    File dir;
    private Toolbar toolbar;
    private RecordingsDbHelper dbHelper;
    private SQLiteDatabase writeableDb;
    private TabLayout tabLayout;
    private SearchView search;
    private int c = 0;
    private int PERMISSIONS_REQUEST_CODE;
    private RepopulateTask repopulateTask;
    private ViewPager pager;
    private boolean repopComplete = false;
    private boolean repopStarted = false;
    private SharedPreferences preferences;
    private Menu actionBarMenu;
    private CustomPagerAdapter pagerAdapter;
    private CustomFrag[] fragments;
    private DrawerLayout drawerlayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navView;

    boolean pcCorrect = false;

    @Override
    public void onCreate(Bundle saved) {
        requestPerm();
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        PreferenceManager.setDefaultValues(this, R.xml.security_settings, false);
        super.onCreate(saved);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent passcodeIntent = getIntent();
        boolean fromRestart = passcodeIntent.getBooleanExtra("fromrestart",false);
        if (passcodeIntent != null) {
            pcCorrect = passcodeIntent.getBooleanExtra("passcode_correct", false);
        }
        if (!pcCorrect && !fromRestart) {
            boolean askForCode = preferences.getBoolean(GcrConstants.SECURITY_ON_OFF, false);
            if (askForCode) {
                Intent intent = new Intent(getApplicationContext(), EnterPasscodeActivity.class);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.drawerlayout);
        getReadableAndWriteableDB();
        setToolBar();
        setUpPagerWithAdapter(); //This is where we create the fragments
        setTabLayoutWithPager();
        setupNavigationDrawer();
    }

    public boolean attemptFileSetUp() {
        //Set File dir variable to the directory we want to work with with
        if (setUpFile()) {
            //Create a new FileLoader
            this.loader = (FileLoader) getSupportLoaderManager().initLoader(0, null, this);
        } else {
            Toast.makeText(getApplicationContext(), "The recording directory was not set up", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void setupNavigationDrawer() {

        this.drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerlayout, R.string.Open, R.string.Close) {

        };
        drawerlayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        navView = (NavigationView) findViewById(R.id.naviagtion_view);
        navView.setItemIconTintList(null);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.settings) {
                    drawerlayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
                if (item.getItemId() == R.id.security) {
                    drawerlayout.closeDrawers();
                    Intent intent = new Intent(getApplicationContext(), SecuritySettingsActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    protected void onRestart() {
        if(fragments[0] != null && fragments[0].adapter!= null){
            loader.forceLoad();
        }
        if(fragments[0] != null && fragments[0].adapter!= null){
            fragments[0].adapter.notifyDataSetChanged();
        }
        if(fragments[1] != null && fragments[1].adapter!= null){
            fragments[1].adapter.notifyDataSetChanged();
        }
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

    public void getReadableAndWriteableDB() {
        dbHelper = new RecordingsDbHelper(getApplicationContext());
        writeableDb = dbHelper.getWritableDatabase();
    }

    public void setUpPagerWithAdapter() {
        pager = (ViewPager) findViewById(R.id.pager);

        int recordingCount = fetchRecordingsOnDb(false).getCount();
        if (recordingCount > 0) {
            GcrConstants.NO_RECORDINGS = false;
            Toast.makeText(getApplicationContext(), recordingCount+" Recordings", Toast.LENGTH_LONG).show();
        }else{
            GcrConstants.NO_RECORDINGS = true;
        }
        File tmp = new File(FILE_PATH);
        if(tmp.listFiles()!= null){
            Toast.makeText(getApplicationContext(),"THE DIRECTORY DID NOT RETURN NULL",Toast.LENGTH_SHORT).show();
            if (tmp.listFiles().length > 0 && getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRun", true)){
                GcrConstants.REPOPULATE = true;
            }else{
                GcrConstants.REPOPULATE = false;
            }
        }else{
            Toast.makeText(getApplicationContext(),"THE DIRECTORY RETURNED NULL",Toast.LENGTH_SHORT).show();
        }

        fragments = null;
        AllFragment all = new AllFragment();
        SavedFragment saved = new SavedFragment();
        fragments = new CustomFrag[]{all, saved};
        pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager(), fragments, new String[]{"Inbox", "Saved"});
        pager.setAdapter(pagerAdapter);
    }

    public void setLimitedInboxSize(){
        Toast.makeText(getApplicationContext(),"LIMITED INBOX SIZE WOULD BE SET",Toast.LENGTH_SHORT).show();
    }
    public void setFullInboxSize(){
        Toast.makeText(getApplicationContext(),"FULL INBOX SIZE WOULD BE SET",Toast.LENGTH_SHORT).show();
    }
    public void setTabLayoutWithPager() {

        tabLayout = (TabLayout) findViewById(R.id.all_saved);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        tabLayout.setSelectedTabIndicatorHeight(10);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_mic_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_lock_white_24dp);
        GcrConstants.TAB_SELECTED = 0;
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                GcrConstants.TAB_SELECTED = tab.getPosition();
                pager.setCurrentItem(tab.getPosition());
                MenuItemCompat.collapseActionView(actionBarMenu.findItem(R.id.search_action));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_resources, menu);
        actionBarMenu = menu;
        search = (SearchView) menu.findItem(R.id.search_action).getActionView();
        search.setQueryHint("Name,Number");
        search.setOnQueryTextListener(this);
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    fragments[0].setData(fetchRecordingsOnDb(false));
                    fragments[1].setData(fetchRecordingsOnDb(true));
                }
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    public void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
    }

    public Cursor fetchRecordingsOnDb(boolean saved) {

        String selection = RecordingsContract.Recordings.COL_SAVED + " = ?";
        String[] args = {"false"};
        String[] args2 = {"true"};
        String sortOrder = RecordingsContract.Recordings.COL_LAST_MODIFIED + " DESC ";
        Cursor c = writeableDb.query(
                RecordingsContract.Recordings.TABLE_NAME,
                GcrConstants.PROJECTION,
                selection,
                saved ? args2 : args,
                null,
                null,
                sortOrder
        );
        return c;
    }

    public void requestPerm(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,Manifest.permission.PROCESS_OUTGOING_CALLS},
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //The call recording are stored in a directory on the external storage. This method
    //checks if the external storage is available and sets up the directory(dir variable)
    public boolean setUpFile() {
        //TODO:Need to compensate for if there is no external storage
        FileManager manager = new FileManager(getApplicationContext());
        if (manager.isExternalStorageWritable()) {
            final File tmp = new File(FILE_PATH);
            if (!tmp.exists()) {//Check if the directory doesn't exist, if not we try to create it
                if (!tmp.mkdir()) {//If we didn't create the directory
                    Toast.makeText(getApplicationContext(), "We couldn't create the directory", Toast.LENGTH_SHORT).show();
                    return false;
                }
                //The directory already exists and there are files in the directory
            } else if (tmp.listFiles() != null && tmp.listFiles().length > 0 &&
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstRun", true)) {

                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("firstRun", false).commit();
                repopulateTask = new RepopulateTask(getApplicationContext());
                repopulateTask.execute(tmp.listFiles());
            }
            dir = tmp;
            return true;
        } else {
            Toast.makeText(this, "Please insert or unmount external storage to record calls ", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private class RepopulateTask extends AsyncTask<File[], Integer, Void> {

        private Context context;
        private ProgressBar progressBar;

        @Override
        protected Void doInBackground(File[]... params) {
            if(isCancelled()){
                return null;
            }
            repopStarted = true;
            String fileName = "";
            c = 0;
            for (File f : params[0]) {
                if(isCancelled()){
                    break;
                }
                c++;
                fileName = f.getName();
                RecordedFile parsedValues = DirectoryAdapter.parseFileName(fileName);
                if (parsedValues != null) {
                    ContentValues values = new ContentValues();
                    String name = LookUpContact.getNameByNumber(parsedValues.number, getApplicationContext());
                    if (name != null) {
                        values.put(RecordingsContract.Recordings.COL_CONTACT_NAME, name);
                    } else {
                        values.put(RecordingsContract.Recordings.COL_CONTACT_NAME, "");
                    }
                    values.put(RecordingsContract.Recordings.COL_RECORDING_PATH, parsedValues.path);
                    values.put(RecordingsContract.Recordings.COL_NUMBER, parsedValues.number);
                    values.put(RecordingsContract.Recordings.COL_LAST_MODIFIED, parsedValues.timeStamp);
                    values.put(RecordingsContract.Recordings.COL_CALL_DIRECTION, parsedValues.callDirection);
                    values.put(RecordingsContract.Recordings.COL_SAVED, "false");
                    values.put(RecordingsContract.Recordings.COL_COLOR, CallRecorderService.generateRandomColor());
                    try{
                        writeableDb.insert(RecordingsContract.Recordings.TABLE_NAME, null, values);
                    }catch(Exception e){
                     Log.i("INSERT FAILED:","Path is not unique " + c);
                    }

                }

                float cc = c;
                float l = params[0].length;
                int p = Math.round((cc / l) * 100);
                publishProgress(p);
            }
            return null;
        }

        RepopulateTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            if(progressBar != null){
                progressBar.setMax(100);
            }else{
                this.cancel(true);
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            repopComplete = true;
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("repopulate",true);
            finish();
            startActivity(new Intent(context, MainActivity.class));
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            repopulateTask = null;
            super.onCancelled();
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Create the file loader passing it the context,File dir, and the adapter
        return new FileLoader(this, this.dir, this.adapter);
    }

    public Loader getLoader() {
        return this.loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fragments[GcrConstants.TAB_SELECTED].setData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onDestroy() {
       if(repopStarted && !repopComplete){
           repopulateTask.cancel(true);
            Intent intent = new Intent(this,RepopulateService.class);
            intent.putExtra("count",c);
            startService(intent);
            Toast.makeText(MainActivity.this, "Your Recordings will be loaded in the background", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
        //TODO: Stop background threads or any long running operation in ondestroy
    }



    @Override
    public boolean onQueryTextSubmit(String query) {
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

    @Override
    public boolean onQueryTextChange(String newText) {
        String[] args = {"%" + newText + "%", "%" + newText + "%", "%" + newText + "%",
                tabLayout.getSelectedTabPosition() == 0 ? "false" : "true"};
        String sql = "SELECT * FROM " + RecordingsContract.Recordings.TABLE_NAME + " WHERE ( (" +
                RecordingsContract.Recordings.COL_NUMBER + " LIKE ? OR "+
                        RecordingsContract.Recordings.COL_NOTE + " LIKE ? OR " + RecordingsContract.Recordings.COL_CONTACT_NAME +
                " LIKE ?) AND (" + RecordingsContract.Recordings.COL_SAVED + " = ? " +
                ")) ORDER BY " + RecordingsContract.Recordings.COL_CONTACT_NAME + " ASC";
        Log.i("SEARCH SQL:", sql);
        Cursor c = writeableDb.rawQuery(sql, args);
        if (tabLayout.getSelectedTabPosition() == 0) {
            if(fragments[0] != null && fragments[0].adapter!= null){
                fragments[0].setData(c);
            }

        } else {
            if(fragments[1] != null && fragments[1].adapter!= null){
                fragments[1].setData(c);
            }
        }
        return false;
    }

    @Override
    public void onFragmentViewCreated(CustomFrag fragment) {
        if(!attemptFileSetUp()){
            return;
        }
        if (fragment == fragments[0]) {
            fragment.setListWithAdapter(fetchRecordingsOnDb(false));
        }
        if (fragment == fragments[1]) {
            fragment.setListWithAdapter(fetchRecordingsOnDb(true));
        }
    }

    public static Intent Share(String type, String path){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        intent.setType(type);
        return intent;
    }

    public static Intent ShareMultiple(ArrayList<Uri> paths){

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_STREAM,paths);
        intent.setType("audio/*");
        return intent;
    }
    public static void saveRoutine(String path,Context context){
        //Save
        SQLiteDatabase db = new RecordingsDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.Recordings.COL_SAVED, "true");
        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
        String[] args = {path};
        db.update(
                RecordingsContract.Recordings.TABLE_NAME,
                values,
                selection,
                args
        );
        db.close();
    }
    public static void unSaveRoutine(String path, Context context){
        //unsave
        SQLiteDatabase db = new RecordingsDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.Recordings.COL_SAVED, "false");
        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
        String[] args = {path};
        db.update(
                RecordingsContract.Recordings.TABLE_NAME,
                values,
                selection,
                args
        );
        db.close();
    }
    public static void saveRoutineMultipe(ArrayList<String> paths, Context context){

        //Save
        SQLiteDatabase db = new RecordingsDbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordingsContract.Recordings.COL_SAVED, "true");
        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
        for(String path : paths){
            String[] args = {path};
            db.update(
                    RecordingsContract.Recordings.TABLE_NAME,
                    values,
                    selection,
                    args
            );
        }
        db.close();
    }
}