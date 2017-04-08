package com.curt.TopNhotch.GoldCallRecorder;

/**
 * Created by Curt on 6/8/2016.
 */
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.curt.TopNhotch.myapplication.R;

public class AllFragment extends CustomFrag{

    public AllFragment() {
        super();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(GcrConstants.NO_RECORDINGS && !GcrConstants.REPOPULATE){
            inflatedLayout = inflater.inflate(R.layout.no_recordings_layout,container,false);
        }else if(!GcrConstants.NO_RECORDINGS && !GcrConstants.REPOPULATE){
            inflatedLayout = inflater.inflate(R.layout.all_list,container,false);
        }else if(GcrConstants.NO_RECORDINGS && GcrConstants.REPOPULATE){
            inflatedLayout = inflater.inflate(R.layout.repopulating_db_layout,container,false);
        }
        return inflatedLayout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fragmentListManager = (FragmentListManager)getActivity();
        fragmentListManager.onFragmentViewCreated(this);
    }
    @Override
    public void setListWithAdapter(Cursor r){
        if(!GcrConstants.NO_RECORDINGS){
            adapter = new DirectoryAdapter(getActivity(),r);
            adapter.setActivity((AppCompatActivity)getActivity());

            callList = (ListView)inflatedLayout.findViewById(R.id.list_view);
            callList.setAdapter(adapter);
            callList.setFooterDividersEnabled(false);
            callList.setHeaderDividersEnabled(false);
            callList.setDividerHeight(15);

            adapter.notifyDataSetChanged();
            registerCallListListener();
        }
    }
}
