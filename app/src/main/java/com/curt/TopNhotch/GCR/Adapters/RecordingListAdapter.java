package com.curt.TopNhotch.GCR.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.curt.TopNhotch.GCR.Animation.Animation;
import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Menus.RecordingListActionModeMenu;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.RecordingManager;
import com.curt.TopNhotch.myapplication.R;
import java.util.ArrayList;



public class RecordingListAdapter extends BaseAdapter{

    private Context context;
    private AppCompatActivity activity;
    private ArrayList<Recording> recordings;
    private TextView circle;
    private ImageView saveUnsave;
    private ArrayList<Recording> selectedListItems;
    private RecordingManager recordingManager;
    private RecordingListActionModeMenu recordingListActionModeMenu;

    public RecordingListAdapter(final Context context, ArrayList<Recording> recordings){
        this.context = context;

        this.recordings = recordings;
        selectedListItems = new ArrayList<>();
    }

    public void setActivity(AppCompatActivity activity){

        this.activity = activity;
        this.recordingListActionModeMenu = new RecordingListActionModeMenu((MainActivity)activity,this);
        this.recordingManager = new RecordingManager(activity,this);
    }

    private static class ViewHolder{
        TextView nameTv;
        TextView dateTv;
        TextView titleTv;
        TextView callDirection;
        RelativeLayout rl;
    }

    @Override
    public int getCount() {
        if(recordings == null){
            return 0;
        }
        return recordings.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
    public void setDataSource(ArrayList<Recording> recordings){ this.recordings = recordings; }

    public ArrayList<Recording> getDataSource(){
        return recordings;
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

            convertView = inflater.inflate(R.layout.recording_list_container_layout, parent, false);

            vh = new ViewHolder();
            vh.titleTv = (TextView)convertView.findViewById(R.id.recording_title);
            vh.nameTv = (TextView)convertView.findViewById(R.id.name);
            vh.dateTv = (TextView)convertView.findViewById(R.id.date);
            vh.callDirection = (TextView)convertView.findViewById(R.id.inOut);
            vh.rl = (RelativeLayout)convertView.findViewById(R.id.container);


            convertView.setTag(vh);
            insertFromRecordingList(position, convertView,vh);
        }
        else{
            vh = (ViewHolder)convertView.getTag();
            insertFromRecordingList(position, convertView,vh);
        }
        return convertView;
    }

    private void insertFromRecordingList(final int position,final View view ,final ViewHolder  vh){

        final Recording recording = recordings.get(position);

        decideIfRowIsHighlighted(vh,recording);

        setSaveUnsaveListener(view,recording);

        setRecordingViewProperties(view,vh,recording);
    }

    private void setSaveUnsaveListener(View view,final Recording recording){

        if(GcrConstants.TAB_SELECTED == 0){

            saveUnsave = (ImageView)view.findViewById(R.id.savestar);
            saveUnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> paths = new ArrayList<>();
                    paths.add(recording.path);
                    recordingManager.saveAndRestartActivity(paths,activity);
                }
            });

        }else if(GcrConstants.TAB_SELECTED == 1){

            saveUnsave = (ImageView)view.findViewById(R.id.savestar);
            saveUnsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> paths = new ArrayList<>();
                    paths.add(recording.path);
                    recordingManager.unSaveAndRestartActivity(paths, activity);
                }
            });
        }
    }



    //Decide if the row is highlighted
    private void decideIfRowIsHighlighted(ViewHolder vh,Recording recording){

        if(!selectedListItems.contains(recording)){
            vh.rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }else if(selectedListItems.contains(recording)){
            vh.rl.setBackgroundColor(Color.parseColor("#FFF4DE"));
        }
    }


    //Set the textual data for a row
    private void setRecordingViewProperties(View view,final ViewHolder vh, final Recording recording){



        vh.titleTv.setText(recording.note);

        vh.nameTv.setText(recording.name);


         circle = (TextView)view.findViewById(R.id.imageView);
         circle.setEnabled(true);
         circle.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 recordingImageClicked(vh,recording,v);
             }
         });
         circle.setText(vh.nameTv.getText().charAt(0)+"");
         recording.setImage(circle,context);


        vh.dateTv.setText(recording.date);

        vh.callDirection.setText(recording.callDirection);

        vh.callDirection.setTextColor(recording.callDirectionColor);
    }


    //When the circle image for a row is clicked
    private void recordingImageClicked(ViewHolder vh,Recording recording,View v){

        //Show the action menu
        recordingListActionModeMenu.showActionModeBar();

        //Fade the circle
        Animation.fade(v,0f,1f,500);

        //Decide if row is selected and deselect or select the row
        if(rowIsSelected(recording)){
            deSelectRecording(vh,recording);
        }else{
            selectRecording(vh,recording);
        }
    }

    //Find out if a row is selected
    private boolean rowIsSelected(Recording recording){

        if(selectedListItems.contains(recording)){
            return true;
        }else{
            return false;
        }
    }

    //Set the color of the row to light gold and add it to the selected list items
    private void selectRecording(ViewHolder vh,Recording recording){

        vh.rl.setBackgroundColor(Color.parseColor("#FFF4DE"));
        selectedListItems.add(recording);
    }

    //Set the color of the row to white and remove it from the selected list items
    private void deSelectRecording(ViewHolder vh, Recording recording){

        vh.rl.setBackgroundColor(Color.parseColor("#FFFFFF"));
        selectedListItems.remove(recording);
        if(selectedListItems.size() == 0){
            recordingListActionModeMenu.getActionMode().finish();
        }
    }

    //Get the selected items in the list
    public ArrayList<Recording> getSelectedListItems(){

        return this.selectedListItems;
    }

    //Set the selected items in the list
    public void setSelectedListItems(ArrayList<Recording> selectedListItems){

        this.selectedListItems = selectedListItems;
    }

}
