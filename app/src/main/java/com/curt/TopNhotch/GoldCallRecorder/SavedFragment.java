package com.curt.TopNhotch.GoldCallRecorder;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.curt.TopNhotch.myapplication.R;


/**
 * Created by Kurt on 6/8/2016.
 */
public class SavedFragment extends CustomFrag {

    public SavedFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentListManager = (FragmentListManager)getActivity();
        fragmentListManager.onFragmentViewCreated(this);
    }

    @Override
    public void setListWithAdapter(Cursor r){

        adapter = new DirectoryAdapter(getActivity(),r);
        adapter.setActivity((AppCompatActivity)getActivity());


        callList = (ListView)inflatedLayout.findViewById(R.id.saved_list_view);
        callList.setItemsCanFocus(true);
        callList.setAdapter(adapter);
        callList.setFooterDividersEnabled(false);
        callList.setHeaderDividersEnabled(false);
        callList.setDividerHeight(15);
        adapter.notifyDataSetChanged();

        registerCallListListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

