package com.curt.TopNhotch.GCR.Fragments;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.Dialogs.RecordingOptionsDialog;
import com.curt.TopNhotch.GCR.Adapters.RecordingListAdapter;
import com.curt.TopNhotch.GCR.Playaudio.RecordingPlayer;

import java.io.File;
import java.util.ArrayList;

public abstract class RecordingListFragment extends Fragment {

    public ListView callList;//List view for the fragment
    public View inflatedLayout;//Layout that contains a list view
    public RecordingListAdapter adapter;//adapter for the fragment list view
    public ArrayList<Recording> recordings;//data set for the adapter

    public void setListWithAdapter(ArrayList<Recording> recordings,int list_view_id){

        if(recordings == null)
            return;

        if(recordings.size() <= 0){
            return;
        }

        try{
            adapter.setDataSource(recordings);
            adapter.setActivity((AppCompatActivity)getActivity());
            callList = (ListView)inflatedLayout.findViewById(list_view_id);
            callList.setItemsCanFocus(true);
            callList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            registerCallListListener();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Register the click and long click listeners for the fragment's list view
    public void registerCallListListener(){
        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<Recording> recordings = adapter.getDataSource();

                //Move to that selected record in the cursor
                Recording recording = recordings.get(position);
                String number = recording.number;
                String note = recording.note;
                String name = recording.name;
                String date = recording.date;
                String direction = recording.callDirection;
                String color = recording.color;

                File tmp = new File(recording.path);
                if (!tmp.exists()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "The file " + recording.path + " may have been deleted", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), RecordingPlayer.class);
                intent.putExtra("path", recording.path);
                if (note != null) {
                    intent.putExtra("note", note != null || note.equals("") ? note : "");
                }
                intent.putExtra("nameOrNumber", !name.equals("") ? name : number);
                intent.putExtra("number", number);
                intent.putExtra("date", date);
                intent.putExtra("direction", direction);
                intent.putExtra("color", color);
                startActivity(intent);
            }
        });
        callList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                ArrayList<Recording> recordings = adapter.getDataSource();
                Recording recording = recordings.get(position);
                String number = recording.number;
                String path = recording.path;
                String name = recording.name;
                String date = recording.date;
                String direction = recording.callDirection;
                String color = recording.color;

                showRecordingDialog(name, number, date, direction, path, color);
                return false;
            }
        });

    }
    public void setData(ArrayList<Recording> recordings){

        if(this.adapter != null && recordings != null){
            this.recordings = recordings;
            this.adapter.setDataSource(recordings);
            this.adapter.notifyDataSetChanged();
        }
    }

    public void showRecordingDialog(String name,String number,String date,String direction,String path,String color){

        RecordingOptionsDialog options = new RecordingOptionsDialog();
        options.setContext(getActivity().getApplicationContext());
        options.setName(name);
        options.setDate(date);
        options.setNumber(number);
        options.setPath(path);
        options.setDirection(direction);
        options.setColor(color);
        options.show(getActivity().getSupportFragmentManager(), "tag");
    }

    public RecordingListAdapter getAdapter(){
        return this.adapter;
    }
}
