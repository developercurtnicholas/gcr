package com.curt.TopNhotch.GoldCallRecorder;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.curt.TopNhotch.myapplication.R;

import java.io.File;

/**
 * Created by Kurt on 8/16/2016.
 */
public class RecordingComplete extends AppCompatActivity {

    private String name;
    private String number;
    private String path;
    private String color;
    private String date = "";
    private SharedPreferences prefs;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Intent intent = getIntent();
        boolean s = intent.getBooleanExtra("success", false);
        if (s) {
            setContentView(R.layout.recording_complete);
            color = intent.getStringExtra("color");
            name = intent.getStringExtra("nameOrNumber");
            number = intent.getStringExtra("number");
            path = intent.getStringExtra("path");
            date = intent.getStringExtra("date");

            Resources r = getResources();
            String addNote = r.getString(R.string.add_note);
            String play =r.getString(R.string.play);
            String save =r.getString(R.string.save);
            String share =r.getString(R.string.share);
            String delete =r.getString(R.string.delete);

            GridView optionsGrid = (GridView)findViewById(R.id.recording_complete_grid);
            GridContent[] contents = {
                    new GridContent(play,R.drawable.silverplay),
                    new GridContent(save,R.drawable.save),
                    new GridContent(share,R.drawable.share),
                    new GridContent(addNote,R.drawable.adddescription),
                    new GridContent(delete,R.drawable.delete)};
            OptionsAdapter adapter = new OptionsAdapter(getApplicationContext(),contents);
            optionsGrid.setAdapter(adapter);
            optionsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(position == 0){
                        //play
                        startPlayActivity();
                    }
                    if(position == 1){
                        //save
                        SQLiteDatabase db = new RecordingsDbHelper(getApplicationContext()).getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(RecordingsContract.Recordings.COL_SAVED, "true");
                        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
                        String[] args = {path};
                        db.update(
                                RecordingsContract.Recordings.TABLE_NAME,
                                values,
                                selection,
                                args
                        );
                        db.close();
                        Toast.makeText(getApplicationContext(),"Recording has been saved",Toast.LENGTH_LONG).show();
                    }
                    if(position == 2){
                        //share
                        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
                        Intent intent = MainActivity.Share(type,path);
                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_recordings)));
                    }
                    if(position == 3){
                        startPlayActivity();
                    }
                    if( position == 4){
                        //delete

                        SQLiteDatabase db = new RecordingsDbHelper(getApplicationContext()).getWritableDatabase();
                        //Delete the recording from the database
                        String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
                        String[] args = {path};
                        db.delete(RecordingsContract.Recordings.TABLE_NAME, selection, args);



                        //Delete the recording from the file system
                        File f = new File(path);
                        f.delete();
                        db.close();

                        Toast.makeText(getApplicationContext(),"Recording has been deleted",Toast.LENGTH_LONG).show();
                    }
                }
            });

            TextView circle = (TextView)findViewById(R.id.recording_complete_circle);

            TextView nameTv = (TextView)findViewById(R.id.recording_complete_name);
            nameTv.setText(name);


            switch (color){

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
            circle.setText(nameTv.getText().charAt(0)+"");
        } else {
            setContentView(R.layout.recording_failed);
        }
    }

    private void startPlayActivity(){
        File tmp = new File(path);
        if (!tmp.exists()) {
            Toast.makeText(getApplicationContext(),
                    "The file " + path + " may have been deleted", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), RecordingPlayer.class);
        intent.putExtra("path", path);
        intent.putExtra("nameOrNumber", name == null ? number : name);
        intent.putExtra("number",number);
        intent.putExtra("date",date);
        intent.putExtra("color",color);
        startActivity(intent);
    }
}
