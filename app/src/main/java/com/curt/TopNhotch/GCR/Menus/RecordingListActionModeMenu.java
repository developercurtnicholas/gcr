package com.curt.TopNhotch.GCR.Menus;
import android.content.Intent;
import android.net.Uri;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.AdapterRecordingDataSource;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.RecordingManager;
import com.curt.TopNhotch.GCR.Adapters.RecordingListAdapter;
import com.curt.TopNhotch.myapplication.R;

import java.io.File;
import java.util.ArrayList;

/**
 * The RecordingListActionModeMenu is the <b>bar that appears at the top of the screen</b> when a user
 * begins a multiple select on the recordings. It contains options to <b>delete,save,select all and share</b> the selected recordings.
 * These options are listened for in the RecordingListActionModeMenu <b>onActionItemClicked</b> method.
 */

public class RecordingListActionModeMenu<Activity extends MainActivity, Adapter extends RecordingListAdapter>
        implements ActionMode.Callback {

    private ActionMode actionMode;
    private Activity activity;
    private RecordingListAdapter adapter;
    private RecordingManager recordingManager;
    private AdapterRecordingDataSource recordingDataSource;
    ArrayList<Recording> recordingsToDelete;

    public RecordingListActionModeMenu(Activity activity,RecordingListAdapter adapter){

        this.activity = activity;
        recordingDataSource = (AdapterRecordingDataSource)activity.synchronizer.getDataSource("Adapter");
        this.recordingManager = new RecordingManager(activity,recordingDataSource.getDataSource());
        this.adapter = adapter;
    }

    public void showActionModeBar(){

        if(this.getActionMode() == null) {
            actionMode = activity.startActionMode(this);
            if(activity.getSupportActionBar() != null){
                activity.getSupportActionBar().hide();
            }
        }
    }

    public ArrayList<String> getPathsOfSelectedItems(){

        ArrayList<String> paths = new ArrayList<String>();
        for(Recording recording : adapter.getSelectedListItems()){
            paths.add(recording.path);
        }
        return paths;
    }

    public ArrayList<Uri> getUriOfSelectedItems(){

        ArrayList<Uri> paths = new ArrayList<>();
        int index;
        for(Recording recording : adapter.getSelectedListItems()){
            Uri uri = Uri.fromFile(new File(recording.path));
            paths.add(uri);
        }
        return paths;
    }


    public void delete(ArrayList<Recording> recordings){

        this.recordingsToDelete = recordings;
        recordingManager.delete(recordings, new RecordingManager.DeleteListener() {
            @Override
            public void deleted() {
                actionMode.finish();
            }
            @Override
            public void canceled() {
                actionMode.finish();
            }
        });
    }

    public ActionMode getActionMode(){

        return this.actionMode;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu){

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()){

            case R.id.contextual_delete:{

                delete(adapter.getSelectedListItems());
                break;
            }
            case R.id.select_all:{
                selectAll();
                break;
            }
            case R.id.share_all:{
                Intent intent = recordingManager.ShareMultiple(getUriOfSelectedItems());
                activity.startActivity(Intent.createChooser(intent, activity.
                        getResources().getString(R.string.share_recordings)));
                break;
            }
        }

        return false;
    }

    public void selectAll(){

        ArrayList<Recording> allRecordings = adapter.getDataSource();
        adapter.setSelectedListItems(null);
        adapter.setSelectedListItems(allRecordings);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        activity.getSupportActionBar().show();
        adapter.setSelectedListItems(null);
        adapter.setSelectedListItems(new ArrayList<Recording>());
        adapter.notifyDataSetChanged();
    }
}
