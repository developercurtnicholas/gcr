package com.curt.TopNhotch.GoldCallRecorder;


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.curt.TopNhotch.myapplication.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Kurt on 2/8/2016.
 */


public class DirectoryAdapter extends BaseAdapter{

    private Context context;
    private AppCompatActivity activity;
    private Cursor dbData;
    private TextView circle;
    private ImageView saveUnsave;
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallbacks;
    private ArrayList<Integer> selectedListItems;
    private DirectoryAdapter THIS = this;

    DirectoryAdapter(final Context context,Cursor data){
        this.context = context;
        this.dbData = data;
        selectedListItems = new ArrayList<>();
        actionModeCallbacks = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.contextual_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.contextual_delete:{

                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        // 2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage(R.string.delete_message)
                                .setTitle(R.string.delete_title)
                                .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int index;
                                        ArrayList<String> args = new ArrayList<>();
                                        for (int pos : selectedListItems) {
                                            dbData.moveToPosition(pos);
                                            index = dbData.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                                            args.add(dbData.getString(index));
                                        }
                                        SQLiteDatabase db = new RecordingsDbHelper(context).getWritableDatabase();
                                        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";

                                        //Delete the recording from the file system
                                        for (String path : args) {
                                            String[] arguments = {path};
                                            db.delete(RecordingsContract.Recordings.TABLE_NAME, selection, arguments);
                                            File f = new File(path);
                                            f.delete();
                                        }
                                        actionMode.finish();
                                    }
                                });

                        // 3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        break;
                    }
                    case R.id.save_all:{
                        ArrayList<String> paths = new ArrayList<>();
                        int index;
                        for(int  pos  : selectedListItems){
                            dbData.moveToPosition(pos);
                            index = dbData.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                            paths.add(dbData.getString(index));
                        }
                        MainActivity.saveRoutineMultipe(paths, context);
                        activity.finish();
                        Intent intent = new Intent(context,MainActivity.class);
                        intent.putExtra("fromrestart", true);
                        activity.startActivity(intent);
                        break;
                    }
                    case R.id.select_all:{
                        int c = THIS.getCount();
                        selectedListItems = null;
                        selectedListItems = new ArrayList<>();
                        for(int i = 0;i < c;i++){
                            selectedListItems.add(i);
                        }
                        THIS.notifyDataSetChanged();
                        break;
                    }
                    case R.id.share_all:{

                        ArrayList<Uri> paths = new ArrayList<>();
                        int index;
                        for(int pos : selectedListItems){
                            dbData.moveToPosition(pos);
                            index = dbData.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                            Uri uri = Uri.fromFile(new File(dbData.getString(index)));
                            paths.add(uri);
                        }
                        Intent intent = MainActivity.ShareMultiple(paths);
                        activity.startActivity(Intent.createChooser(intent, activity.
                                getResources().getString(R.string.share_recordings)));
                        break;
                    }
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
                activity.getSupportActionBar().show();
                selectedListItems = null;
                selectedListItems = new ArrayList<>();
                THIS.notifyDataSetChanged();
            }
        };
    }

    public void setActivity(AppCompatActivity activity){
        this.activity = activity;
    }

    private static class ViewHolder{
        TextView nameTv;
        TextView dateTv;
        TextView titleT;
        TextView inout;
        RelativeLayout rl;
    }

    @Override
    public int getCount() {
        if(dbData == null){
            return 0;
        }
        return dbData.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    public void setDataSource(Cursor data){ this.dbData = data; }
    public Cursor getDataSource(){
        return  this.dbData;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.container_layout, parent, false);

            vh = new ViewHolder();
            vh.titleT = (TextView)convertView.findViewById(R.id.recording_title);
            vh.nameTv = (TextView)convertView.findViewById(R.id.name);
            vh.dateTv = (TextView)convertView.findViewById(R.id.date);
            vh.inout = (TextView)convertView.findViewById(R.id.inOut);
            vh.rl = (RelativeLayout)convertView.findViewById(R.id.container);


            convertView.setTag(vh);

            insertFromCursor(position, convertView,vh);
        }
        else{
            vh = (ViewHolder)convertView.getTag();
            insertFromCursor(position, convertView,vh);
        }
        return convertView;
    }
    private void insertFromCursor(final int position, final View view, final ViewHolder vh){

        dbData.moveToPosition(position);

        int nameIndex = dbData.getColumnIndex(RecordingsContract.Recordings.COL_CONTACT_NAME);
        int numIndex = dbData.getColumnIndex(RecordingsContract.Recordings.COL_NUMBER);
        int titleIndex = dbData.getColumnIndex(RecordingsContract.Recordings.COL_NOTE);
        int dateIndex = dbData.getColumnIndex(RecordingsContract.Recordings.COL_LAST_MODIFIED);
        int dirIndex = dbData.getColumnIndex(RecordingsContract.Recordings.COL_CALL_DIRECTION);
        int colorIndex = dbData.getColumnIndex(RecordingsContract.Recordings.COL_COLOR);

        if(!selectedListItems.contains(position)){
            vh.rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else if(selectedListItems.contains(position)){
            vh.rl.setBackgroundColor(Color.parseColor("#FFF4DE"));
        }


        String titledesc = dbData.getString(titleIndex);

        if(titledesc != null && !titledesc.equals("")){
            vh.titleT.setText(titledesc);
        }else{
            vh.titleT.setText(context.getResources().getString(R.string.no_title_description));
        }


        if(GcrConstants.TAB_SELECTED == 0){
            saveUnsave = (ImageView)view.findViewById(R.id.savestar);
            saveUnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbData.moveToPosition(position);
                    int index = dbData.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                    MainActivity.saveRoutine(dbData.getString(index),context);
                    activity.finish();
                    Intent intent = new Intent(context,MainActivity.class);
                    activity.startActivity(intent);
                }
            });
        }else if(GcrConstants.TAB_SELECTED == 0){
            saveUnsave = (ImageView)view.findViewById(R.id.unsave);
            saveUnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbData.moveToPosition(position);
                    int index = dbData.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);
                    MainActivity.unSaveRoutine(dbData.getString(index), context);
                    activity.finish();
                    Intent intent = new Intent(context,MainActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
        circle = (TextView)view.findViewById(R.id.imageView);
        circle.setEnabled(true);
        switch (dbData.getString(colorIndex)){

            case "green": circle.setBackgroundResource(R.drawable.color_shadow_green);
                break;
            case "red": circle.setBackgroundResource(R.drawable.circle_shadow_red);
                break;
            case "blue": circle.setBackgroundResource(R.drawable.circle_shadow_blue);
                break;
            case "gold": circle.setBackgroundResource(R.drawable.color_shadow_gold);
                break;
            case "purple": circle.setBackgroundResource(R.drawable.color_shadow_purple);
        }
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(actionMode == null) {
                    actionMode = activity.startActionMode(actionModeCallbacks);
                    activity.getSupportActionBar().hide();
                }
                ObjectAnimator on = ObjectAnimator.ofFloat(v,"alpha",0f,1f);
                on.setDuration(500);
                on.start();
                if(!selectedListItems.contains(position)){
                    vh.rl.setBackgroundColor(Color.parseColor("#FFF4DE"));
                    selectedListItems.add(position);
                }else{
                    vh.rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    Object item = position;
                    selectedListItems.remove(item);
                    if(selectedListItems.size() == 0){
                        actionMode.finish();
                    }
                }
            }
        });
        if(!dbData.getString(nameIndex).equals("")){
            vh.nameTv.setText(dbData.getString(nameIndex));
        }else{
            vh.nameTv.setText(dbData.getString(numIndex));
        }
        circle.setText(vh.nameTv.getText().charAt(0)+"");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        vh.dateTv.setText(dateFormat.format(Long.parseLong(dbData.getString(dateIndex))));

        if(dbData.getString(dirIndex).equals("I")){
            vh.inout.setText(context.getResources().getString(R.string.incoming));
            vh.inout.setTextColor(context.getResources().getColor(R.color.myGreen));
        }else{
            vh.inout.setText(context.getResources().getString(R.string.outgoing));
            vh.inout.setTextColor(context.getResources().getColor(R.color.pictonBlue));
        }
    }
    public static RecordedFile parseFileName(String filename){
        String number = "";
        String timeStamp = "";
        String callDirection = "";
        String path = GcrConstants.RECORDINGS_DIRECTORY+"/"+filename;
        //Find the first underscore
        try{
            int first = filename.indexOf("_");
            //If none was found that file was not recorded
            if(first == -1){
                return null;
            }else{//Store the name or number
                number = filename.substring(0,first);
            }

            //Find the second underscore
            int second = filename.indexOf("_",first+1);
            if(second == -1){
                return null;
            }else{//Store the timestamp
                timeStamp = filename.substring(first+1,second);
            }
            int I = filename.indexOf("I",second+1);//Incoming
            int O = filename.indexOf("O",second+1);//Outgoing

            //If neither was found return null
            if(I != -1){
                callDirection = "I";
                return new RecordedFile(number,timeStamp,callDirection,path);
            }if(O != -1){
                callDirection = "O";
                return new RecordedFile(number,timeStamp,callDirection,path);
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
}
