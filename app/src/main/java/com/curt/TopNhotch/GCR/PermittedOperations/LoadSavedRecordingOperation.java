package com.curt.TopNhotch.GCR.PermittedOperations;

import android.content.Context;

import com.curt.TopNhotch.GCR.Fragments.RecordingListFragment;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 6/17/2017.
 */

public class LoadSavedRecordingOperation extends LoadRecordingFromDbOperation implements LoadRecordingFromDbOperation.LoadAction {

    private RecordingListFragment fragment;

    public LoadSavedRecordingOperation(Context context, RecordingListFragment fragment) {
        super(context);
        this.setLoadAction(this);
        this.fragment = fragment;
        this.context = context;
    }

    @Override
    public void loadActionGranted() {
        this.recordings = this.loadRecordingsFromDb(SortOrder.DESCENDING,RecordingType.SAVED);
        this.fragment.setListWithAdapter(recordings,R.id.saved_list_view);
    }

    @Override
    public void loadActionDenied() {

    }
}
