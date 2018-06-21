package com.curt.TopNhotch.GCR.Fragments;

/**
 * Created by Curt on 6/8/2016.
 */
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Loaders.RecordingLoader;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.AdapterRecordingDataSource;
import com.curt.TopNhotch.GCR.Utilities.RecordingsDirectoryUtils.DirectoryHelper;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.myapplication.R;

import java.util.ArrayList;

public class RecordingFragment extends RecordingListFragment implements LoaderManager.LoaderCallbacks<ArrayList<Recording>> {


    public RecordingFragment() {
        super();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(adapter != null){
                getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {

        if(DirectoryHelper.recordingDirectoryHasFiles()){
            inflatedLayout = inflater.inflate(R.layout.all_list,container,false);
            return inflatedLayout;
        }else{
            inflatedLayout = inflater.inflate(R.layout.no_recordings_layout,container,false);
            return inflatedLayout;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        AdapterRecordingDataSource adapterRecordingDataSource = (AdapterRecordingDataSource)((MainActivity)getActivity()).synchronizer.getDataSource("Adapter");
        this.adapter = adapterRecordingDataSource.getDataSource();
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        this.getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

    }

    @Override
    public Loader<ArrayList<Recording>> onCreateLoader(int id, Bundle args) {

        return new RecordingLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recording>> loader, ArrayList<Recording> data){

        this.setListWithAdapter(data,R.id.list_view);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Recording>> loader) {

    }
}
