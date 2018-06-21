package com.curt.TopNhotch.GCR.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Utilities.RecordingManagement.RecordingManager;
import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Adapters.GridContent;
import com.curt.TopNhotch.GCR.Adapters.OptionsAdapter;
import com.curt.TopNhotch.GCR.Adapters.RecordingListAdapter;
import com.curt.TopNhotch.GCR.Playaudio.RecordingPlayer;
import com.curt.TopNhotch.myapplication.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Kurt on 5/29/2016.
 */
public class RecordingOptionsDialog extends AppCompatDialogFragment {
    private Context context;
    private View v;
    private String dialogname;
    private String dialogdate;
    private String dialogNumber;
    private String dialogPath;
    private SharedPreferences prefs;
    private String dialogDirection;
    private String dialogColor;
    private RecordingManager recordingManager;
    private RecordingListAdapter adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        v = inflater.inflate(R.layout.options_dialog,null);
        builder.setView(v);
        TextView name = (TextView)v.findViewById(R.id.nameOrNumber);
        TextView date = (TextView)v.findViewById(R.id.dialog_date);
        TextView number = (TextView)v.findViewById(R.id.dialog_number);
        TextView contactTextView = (TextView)v.findViewById(R.id.contact_image_view);

        switch (dialogColor){
            case "green": contactTextView.setBackgroundResource(R.drawable.color_shadow_green);
                break;
            case "red": contactTextView.setBackgroundResource(R.drawable.circle_shadow_red);
                break;
            case "blue": contactTextView.setBackgroundResource(R.drawable.circle_shadow_blue);
                break;
            case "gold": contactTextView.setBackgroundResource(R.drawable.color_shadow_gold);
                break;
            case "purple": contactTextView.setBackgroundResource(R.drawable.color_shadow_purple);
        }

        contactTextView.setText(dialogname.equals("") ? dialogNumber.charAt(0)+"":dialogname.charAt(0)+"");

        name.setText(dialogname.equals("") ? dialogNumber : dialogname);

        date.setText(dialogdate);

        if(!dialogname.equals("")){
            number.setText(dialogNumber);
        }

        Resources r = getResources();
        String addNote = r.getString(R.string.add_note);
        String play =r.getString(R.string.play);
        String save =r.getString(R.string.save);
        String share =r.getString(R.string.share);
        String delete =r.getString(R.string.delete);
        String call =r.getString(R.string.call);

        GridView optionsGrid = (GridView)v.findViewById(R.id.options_grid);
        GridContent[] contents = {new GridContent(play,R.drawable.silverplay),
                new GridContent(call,R.drawable.call),
                new GridContent(save,R.drawable.save),
                new GridContent(share,R.drawable.share),
                new GridContent(addNote,R.drawable.adddescription),
                new GridContent(delete,R.drawable.delete)};
        OptionsAdapter adapter = new OptionsAdapter(getActivity().getApplicationContext(),contents);
        optionsGrid.setAdapter(adapter);
        registerGridListeners(optionsGrid);
        return builder.create();
    }
    public void setContext(Context context){
        this.context =context;
    }

    public void registerGridListeners(GridView options){

        recordingManager = new RecordingManager(getContext());

        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = new RecordingsDbHelper(getActivity().getApplicationContext()).getWritableDatabase();

                if(position == 0){
                    //Play
                    startPlayActivity();
                }
                if(position == 1){
                    //Call
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+dialogNumber));

                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                if(position == 2){
                    //Save
                    ArrayList<String> paths = new ArrayList<String>();
                    paths.add(dialogPath);
                    recordingManager.save(paths, getContext());
                    getActivity().finish();
                    Intent intent = new Intent(getContext(),MainActivity.class);
                    intent.putExtra("fromrestart",true);
                    startActivity(intent);
                }
                if(position == 3){
                    //Share
                    prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String type = "audio/amr";
                    switch (prefs.getString(GcrConstants.RECORDING_FILE_TYPE,".amr")){
                        case ".3gp":{
                            type = "audio/3gpp";
                            break;
                        }
                        case ".amr":{
                            type = "audio/amr";
                            break;
                        }
                        case ".awb":{
                            type = "audio/awb";
                            break;
                        }
                        case ".mp4":{
                            type = "audio/mp4";
                            break;
                        }
                    }
                    Intent intent = recordingManager.Share(type,dialogPath);
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_recordings)));
                }
                if(position == 4){
                    //Add note
                    startPlayActivity();
                }
                if(position == 5){

                    //Delete the recording from the database
                    String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
                    String[] args = {dialogPath};
                    db.delete(RecordingsContract.Recordings.TABLE_NAME,selection,args);

                    //Delete the recording from the file system
                    File f = new File(dialogPath);
                    f.delete();
                }
                dismiss();
            }
        });
    }

    public void startPlayActivity(){
        File tmp = new File(dialogPath);
        if (!tmp.exists()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "The file " + dialogPath + " may have been deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity().getApplicationContext(), RecordingPlayer.class);
        intent.putExtra("path", dialogPath);
        intent.putExtra("nameOrNumber", !dialogname.equals("") ? dialogname : dialogNumber);
        intent.putExtra("number",dialogNumber);
        intent.putExtra("date",dialogdate);
        intent.putExtra("color",dialogColor);
        startActivity(intent);
    }

    public void setName(String name){
        this.dialogname = name;
    }
    public void setDate(String date){
        this.dialogdate = date;
    }
    public void setNumber(String number){
        this.dialogNumber = number;
    }
    public void setPath(String path){
        this.dialogPath = path;
    }
    public void setDirection(String direction){
        this.dialogDirection = direction;

    }
    public void setColor(String color){
        this.dialogColor = color;
    }
}
