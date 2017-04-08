package com.curt.TopNhotch.GoldCallRecorder;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.curt.TopNhotch.myapplication.R;

import java.io.File;
import java.util.Random;
import java.util.UUID;


/**
 * Created by Kurt on 1/27/2016.
 */

interface CallState{
    void onIdle();

    void onOffHook();

    void onRinging();
}

public class CallRecorderService extends Service implements CallState {

    private TelephonyManager tManager;
    private boolean offHookCalled = false;
    private int sId;
    private MediaRecorder recorder;
    private String logTag = "##INFO##:";
    private long timeStamp;
    private int recordingSourceFlag = 0;
    private int RECORDING_INBOX_SIZE;
    private PhoneState pstate;
    private int attempts = 0;
    private boolean failed = false;
    private SharedPreferences prefs;
    private RecordingsDbHelper dbHelper;
    private SQLiteDatabase db;
    //TODO:Use sharedPreferences to get the file path
    String SETTINGS = "UserSettings";
    SharedPreferences settings;
    //This file path is where the recording will be saved
    private String FILE_PATH;
    private String FILE_NAME = "/";
    private String FILE_EXTENSION;
    private String NAME;
    private String COLOR;
    private String PHONE_NUMBER;
    private String CALL_DIRECTION;



    public void onCreate(){
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String inboxSizeString = prefs.getString(GcrConstants.INBOX_SIZE,"10");
        int inboxSize = Integer.parseInt(inboxSizeString);
        if(inboxSizeString.equals(getResources().getString(R.string.unlimited))){
            RECORDING_INBOX_SIZE = inboxSize;
        }else{
            RECORDING_INBOX_SIZE = 10000000;
        }
        FILE_EXTENSION = prefs.getString(GcrConstants.RECORDING_FILE_TYPE,".amr");
        if(!checkExternalStorage()){//Checking if external storage is writeable since we will be writing the recording to it
            Toast.makeText(getApplicationContext(),"Please insert or unmount external storage to record calls",Toast.LENGTH_LONG)
                    .show();
            onDestroy();
        }
        Log.i(logTag, "OnCreate");
        tManager = (TelephonyManager)getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        pstate = new PhoneState(getApplicationContext(),this);
        tManager.listen(pstate, PhoneStateListener.LISTEN_CALL_STATE);//Telephony manger now listen the state of the phone call
    }

    public void generateFileName(){
        this.FILE_NAME = "";
        this.FILE_NAME += PHONE_NUMBER;
        long currentTime = System.currentTimeMillis();
        this.FILE_NAME += "_"+currentTime+"_";
        this.FILE_NAME += CALL_DIRECTION+"_";
        this.FILE_NAME+= UUID.randomUUID();
    }
    public boolean checkExternalStorage(){
        FileManager manager = new FileManager(getApplicationContext());
        if(manager.isExternalStorageWritable()){
            return true;
        }else{
            return false;
        }
    }
    //Directory to save the recorded call
    public void setUpFilePath(){
        FILE_PATH = GcrConstants.RECORDINGS_DIRECTORY;
    }


    public int onStartCommand(Intent intent,int flags,int startId){
        dbHelper  = new RecordingsDbHelper(getApplicationContext());
        db = dbHelper.getWritableDatabase();
        Log.i(logTag, "OnStart");
        sId = startId;

        //Store the phone number of the call
        this.PHONE_NUMBER = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
        String contact = LookUpContact.getNameByNumber(intent.getStringExtra("incoming_num"),getApplicationContext());
        Log.i("##NUMBER: ", intent.getStringExtra("incoming_num")+" contact: "+contact);
        this.CALL_DIRECTION = intent.getStringExtra("CallDirection");

        if(this.PHONE_NUMBER == null){
            this.PHONE_NUMBER = intent.getStringExtra("incoming_num");
            if(this.PHONE_NUMBER == null){
                this.PHONE_NUMBER = "Unknown";
            }
        }
        Log.i("##DATA:PHONE NUMBER: ",this.PHONE_NUMBER);
        //Set the directory we are going to store the recoding in
        setUpFilePath();
        //Create a unique filename with the necessary information needed such as the phone number and the contact name
        generateFileName();
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    public void failure(){

        if(recorder != null){
            recorder.reset();
            recorder.release();
            recorder = null;
        }

        Intent recordingComplete = new Intent(getApplicationContext(),RecordingComplete.class);
        recordingComplete.putExtra("color", COLOR);
        recordingComplete.putExtra("nameOrNumber", NAME == null ? PHONE_NUMBER : NAME);
        recordingComplete.putExtra("path", FILE_PATH + "/" + FILE_NAME + FILE_EXTENSION);
        recordingComplete.putExtra("date",Long.toString(timeStamp));
        recordingComplete.putExtra("success", false);
        recordingComplete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(recordingComplete);
        File tmp = new File(FILE_PATH+"/"+FILE_NAME+FILE_EXTENSION);
        if(tmp.exists()){
            tmp.delete();
        }
        stopSelf(sId);
    }
    public void nullifyRecorder(){
        if(recorder != null){
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }
    public void reset() throws Exception{
        attempts++;
        Log.i(logTag, "Resetting|" + attempts);
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    public void stopRecording(){
        nullifyRecorder();
        //Check if the file is in the directory if so insert it into the database
        File tmp = new File(FILE_PATH+"/"+FILE_NAME+FILE_EXTENSION);
        if(tmp.exists()){
            insertRecordingInDb();

            Intent recordingComplete = new Intent(getApplicationContext(),RecordingComplete.class);
            recordingComplete.putExtra("color", COLOR);
            recordingComplete.putExtra("nameOrNumber", NAME == null ? PHONE_NUMBER : NAME);
            recordingComplete.putExtra("path", FILE_PATH + "/" + FILE_NAME + FILE_EXTENSION);
            recordingComplete.putExtra("date",Long.toString(timeStamp));
            recordingComplete.putExtra("success",true);
            recordingComplete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(recordingComplete);
        }
        stopSelf(sId);
    }

    public void onDestroy(){
        Log.i(logTag, "DESTROYED");
        tManager.listen(pstate, PhoneStateListener.LISTEN_NONE);
        tManager = null;
    }

    @Override
    public void onIdle() {
        //If we went offhook and then go idle that means a call was initiated
        //So after going idle after offhook we stop the recording
        if(offHookCalled){
            stopRecording();
        }
    }
    public void insertRecordingInDb(){
        ContentValues values = new ContentValues();
        NAME = LookUpContact.getNameByNumber(PHONE_NUMBER,getApplicationContext());
        if(NAME != null){
            values.put(RecordingsContract.Recordings.COL_CONTACT_NAME,NAME);
        }else{
            values.put(RecordingsContract.Recordings.COL_CONTACT_NAME,"");
        }
        values.put(RecordingsContract.Recordings.COL_NUMBER, PHONE_NUMBER);
        values.put(RecordingsContract.Recordings.COL_RECORDING_PATH, FILE_PATH + "/" + FILE_NAME + FILE_EXTENSION);
        values.put(RecordingsContract.Recordings.COL_LAST_MODIFIED,timeStamp);
        values.put(RecordingsContract.Recordings.COL_CALL_DIRECTION, CALL_DIRECTION);
        values.put(RecordingsContract.Recordings.COL_SAVED, "false");
        COLOR = generateRandomColor();
        values.put(RecordingsContract.Recordings.COL_COLOR,COLOR);
        long rowId = db.insert(RecordingsContract.Recordings.TABLE_NAME, null, values);
        db.close();
    }
    public static String generateRandomColor(){

        Random rand = new Random();
        int num = rand.nextInt(5);
        switch (num){
            case 0: return "green";
            case 1: return "red";
            case 2: return "blue";
            case 3: return "gold";
            case 4: return "purple";
        }
        return "green";
    }
    @Override
    public void onOffHook() {
        //The device went offhook and we are ready to start recording
        offHookCalled = true;
        timeStamp = System.currentTimeMillis();//Time the recording was started
        Log.i(logTag, "OFFHOOK");

        if(makeThreeAttempts()){
            return;
        }
        else{//Our three attempts with that source failed so we need to try others
            for(int i = 0; i <= 4;i++){
                if(startRecording(false)){
                    return;
                }else{
                    recordingSourceFlag++;
                    try {
                        reset();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //After 3 attempts and trying out different sources we assume we can't record on the device
        failure();
    }

    public boolean makeThreeAttempts(){
        //Make 3 attempts at recording
        while(attempts < 2){
            if(startRecording(true)){
                return true;
            }
            try {
                reset();
                Thread.sleep(400);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean startRecording(boolean fromPrefs) {
        recorder = new MediaRecorder();
        if(fromPrefs){
            setAudioSourecFromPreference();
        }else{
            TryDifferentSources();
        }

        switch (prefs.getString(GcrConstants.RECORDING_FILE_TYPE,".amr")){
            case ".3gp":{
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                break;
            }
            case ".amr":{
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                break;
            }
            case ".awb":{
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
                break;
            }
            case ".mp4":{
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                break;
            }
        }

        recorder.setOutputFile(FILE_PATH + "/" + FILE_NAME + FILE_EXTENSION);

        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(logTag, "" + e.getMessage());
            Log.i(logTag,""+e.toString()+"|Prepare failed");
            Toast.makeText(getApplicationContext(),"Prepare failed",Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(logTag, "" + e.getMessage());
            Log.i(logTag, "" + e.toString()+"|Start failed");
            Log.i(logTag,""+e.getLocalizedMessage());
            return  false;
        }
        Toast.makeText(getApplicationContext(),"Recording",Toast.LENGTH_SHORT).show();
        Log.i(logTag, "Recording was started");
        return true;
    }

    public void TryDifferentSources(){
        switch (recordingSourceFlag){
            case 3:{
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                Log.i(logTag, "COMMUNICATION");
                break;
            }
            case 4:{
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                Log.i(logTag, "RECOGNITION");
                break;
            }
            case 2:{
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                Log.i(logTag, "CALL");
                break;
            }
            case 0:{
                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                Log.i(logTag,"DEFAULT");
                break;
            }
            case 1:{
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                Log.i(logTag, "MICROPHONE");
                break;
            }

            default:{
                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                Log.i(logTag, "DEFAULT DEFAULT");
                break;
            }
        }
    }

    public void setAudioSourecFromPreference(){
        switch (prefs.getString(GcrConstants.RECORDING_SOURCE,"")){
            case "VOICE_CALL(RECOMMENDED)":recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                break;
            case "DEFAULT":recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                break;
            case "VOICE_UPLINK": recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_UPLINK);
                break;
            case "VOICE_DOWNLINK": recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
                break;
            case "MICROPHONE": recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                break;
            case "VOICE_COMMUNICATION":recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                break;
            case "VOICE_RECOGNITION":recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                break;
        }
    }
    @Override
    public void onRinging() {
        Log.i(logTag, "RINGING");
    }
}