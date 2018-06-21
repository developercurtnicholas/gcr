package com.curt.TopNhotch.GCR.Models;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.LookUpContact;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.Factories.DrawableFactory;
import com.curt.TopNhotch.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

//TODO: Make Recording Abstract so we can have different types of recordings using polymorphism
public class Recording {

    public String name = "";
    public String number = null;
    public String date = "";
    public String timeStamp = "";
    public String callDirection = "";
    public String path = "";
    public String note = "";
    public String color = "";
    public int callDirectionColor;
    public Uri photoUri;
    private Contact contact;

    public Recording(String number, String timeStamp, String callDirection, String path){
        this.number = number;
        this.timeStamp = timeStamp;
        this.callDirection = callDirection;
        this.path = path;
    }

    private Recording(Context context,String name, String number,
                      String timeStamp, String callDirection,
                      String color, String description,String path,Uri photoUri){

        this.number = number;
        setNameOrNumber(name);
        setDate(timeStamp);
        setCallDirectionAndColor(callDirection,context);
        setDescription(description,context);
        this.color = color;
        this.path = path;
        this.timeStamp = timeStamp;
        this.photoUri = photoUri;
    }

    private void setNameOrNumber(String nameNumber){

        if(!nameNumber.equals("")){
            this.name = nameNumber;
        }else{
            this.name = number;
        }
    }
    private void setDate(String timeStamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        this.date = dateFormat.format(Long.parseLong(timeStamp));
    }
    private void setCallDirectionAndColor(String direction,Context context){
        String dir;
        if(direction.equals("I")){
            this.callDirection = context.getResources().getString(R.string.incoming);
            callDirectionColor = context.getResources().getColor(R.color.myGreen);
        }else{
            this.callDirection = context.getResources().getString(R.string.outgoing);
            callDirectionColor = context.getResources().getColor(R.color.pictonBlue);
        }
    }


    public void setImage(View view, Context context){

        if(this.photoUri == null){
            switch (color){
                case "green": view.setBackgroundResource(R.drawable.color_shadow_green);
                    break;
                case "red": view.setBackgroundResource(R.drawable.circle_shadow_red);
                    break;
                case "blue": view.setBackgroundResource(R.drawable.circle_shadow_blue);
                    break;
                case "gold": view.setBackgroundResource(R.drawable.color_shadow_gold);
                    break;
                case "purple": view.setBackgroundResource(R.drawable.color_shadow_purple);
            }
        }else{
            try{
                view.setBackground(DrawableFactory.createRoundImageFromUri(context,this.photoUri));
            }catch (Exception e){
            }
        }
    }

    public void setDescription(String description,Context context){
        if(description == null || description.equals("")){
            this.note = context.getResources().getString(R.string.no_title_description);
        }else{
            this.note = description;
        }
    }

    public static ArrayList<Recording> createRecordingsFromCursor(Cursor recordings,Context context){

        ArrayList<Recording> recordingsList = new ArrayList<>();
        int i = 0;

        Recording recording;
        int count = recordings.getCount();
        for(i = 0; i < count ;i++){
            recording = createRecordingFromCursorRow(recordings,context);
            if(recording != null){
                recordingsList.add(recording);
            }
        }
        recordings.close();
        return recordingsList;
    }

    public static Recording createRecordingFromCursorRow(Cursor recording,Context context){

        if(recording == null){
            return null;
        }

        recording.moveToNext();

        int numIndex = recording.getColumnIndex(RecordingsContract.Recordings.COL_NUMBER);
        int titleIndex = recording.getColumnIndex(RecordingsContract.Recordings.COL_NOTE);
        int dateIndex = recording.getColumnIndex(RecordingsContract.Recordings.COL_LAST_MODIFIED);
        int dirIndex = recording.getColumnIndex(RecordingsContract.Recordings.COL_CALL_DIRECTION);
        int colorIndex = recording.getColumnIndex(RecordingsContract.Recordings.COL_COLOR);
        int pathIndex = recording.getColumnIndex(RecordingsContract.Recordings.COL_RECORDING_PATH);

        String number = recording.getString(numIndex);
        Contact cc = LookUpContact.getContact(number,context);
        String name = "";
        Uri photoUri = null;
        if(cc != null){
            name = cc.getName();
            photoUri = cc.contactsPhotoUri;
        }
        String title = recording.getString(titleIndex);
        String date = recording.getString(dateIndex);
        String direction = recording.getString(dirIndex);
        String color = recording.getString(colorIndex);
        String path = recording.getString(pathIndex);

        Recording r = new Recording(context,name,number,date,direction,color,title,path,photoUri);

        return r;
    }

    public static Recording createRecordingFromFileName(String filename){

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
                return new Recording(number,timeStamp,callDirection,path);
            }if(O != -1){
                callDirection = "O";
                return new Recording(number,timeStamp,callDirection,path);
            }
            return null;
        }catch(Exception e){
            return null;
        }
    }
    public static Recording createRecordingFromFileName2(String fileName){

        String[] sections = fileName.split("_");
        return null;
    }

}
