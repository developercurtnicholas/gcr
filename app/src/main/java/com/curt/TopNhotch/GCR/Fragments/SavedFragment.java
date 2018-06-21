package com.curt.TopNhotch.GCR.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.curt.TopNhotch.myapplication.R;


/**
 * Created by Kurt on 6/8/2016.
 */
public class SavedFragment extends RecordingListFragment {

    public SavedFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
    public interface SavedFragmentViewListener{
        void onSavedFragmentCreated(SavedFragment fragment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(adapter.getDataSource().size() > 0){
                getActivity().getSupportLoaderManager().getLoader(0).forceLoad();
                Log.i("REFRESH","notifyDataSetChanged()");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflatedLayout = inflater.inflate(R.layout.saved_list,container,false);
        return inflatedLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

