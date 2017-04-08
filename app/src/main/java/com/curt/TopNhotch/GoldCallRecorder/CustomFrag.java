package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Kurt on 6/10/2016.
 */
public abstract class CustomFrag extends Fragment {

    public ListView callList;//List view for the fragment
    public View inflatedLayout;//Layout that contains a list view
    public DirectoryAdapter adapter;//adapter for the fragment list view
    public FragmentListManager fragmentListManager;
    public Cursor r;//data set for the adapter

    public abstract void setListWithAdapter(Cursor r);

    //Register the click and long click listeners for the fragment's list view
    public void registerCallListListener(){
        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                r = adapter.getDataSource();

                //Move to that selected record in the cursor
                r.moveToPosition(position);

                int nameIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_CONTACT_NAME);
                int numberIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_NUMBER);
                int dateIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_LAST_MODIFIED);
                int directionIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_CALL_DIRECTION);
                int pathIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                int colorIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_COLOR);
                int noteIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_NOTE);

                String name = r.getString(nameIndex);//Get the name
                String note = r.getString(noteIndex);
                String number = r.getString(numberIndex);
                String date = r.getString(dateIndex);
                String direction = r.getString(directionIndex);
                String path = r.getString(pathIndex);
                String color = r.getString(colorIndex);

                File tmp = new File(path);
                if (!tmp.exists()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "The file " + path + " may have been deleted", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity().getApplicationContext(), RecordingPlayer.class);
                intent.putExtra("path", path);
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
                r = adapter.getDataSource();

                //Move to that selected record in the cursor
                r.moveToPosition(position);

                int nameIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_CONTACT_NAME);
                int numberIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_NUMBER);
                int dateIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_LAST_MODIFIED);
                int directionIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_CALL_DIRECTION);
                int pathIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                int colorIndex = r.getColumnIndex(RecordingsContract.Recordings.COL_COLOR);

                String name = r.getString(nameIndex);//Get the name
                String number = r.getString(numberIndex);
                String date = r.getString(dateIndex);
                String direction = r.getString(directionIndex);
                String path = r.getString(pathIndex);
                String color = r.getString(colorIndex);
                showRecordingDialog(name, number, date, direction, path, color);
                return false;
            }
        });

    }
    public void setData(Cursor r){

        if(this.adapter != null && r != null){
            this.r = r;
            this.adapter.setDataSource(this.r);
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
    public DirectoryAdapter getAdapter(){
        return this.adapter;
    }
}
