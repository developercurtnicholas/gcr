package com.curt.TopNhotch.GCR.Services;

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
import android.widget.Toast;

import com.curt.TopNhotch.GCR.Utilities.AudioRecording.CallState;
import com.curt.TopNhotch.GCR.Utilities.AudioRecording.PhoneState;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.FileManager;
import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.LookUpContact;
import com.curt.TopNhotch.GCR.Activities.RecordingCompleteActivity;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.RandomColorGenerator;
import java.io.File;
import java.util.UUID;

public class CallRecorderService extends Service implements CallState {

    private TelephonyManager tManager;
    private boolean offHookCalled = false;
    private int sId;
    private MediaRecorder recorder;
    private long timeStamp;
    private PhoneState pstate;
    private int attempts = 0;
    private SharedPreferences prefs;
    private RecordingsDbHelper recordingsDbHelper;
    private SQLiteDatabase db;
    private String FILE_NAME;
    private String FILE_EXTENSION = ".amr";
    private String NAME;
    private String COLOR;
    private String PHONE_NUMBER;
    private String CALL_DIRECTION;

    public void onCreate(){

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!checkExternalStorage()){//Checking if external storage is writeable since we will be writing the recording to it
            Toast.makeText(getApplicationContext(),"Please insert or unmount external storage to record calls",Toast.LENGTH_LONG)
                    .show();
            onDestroy();
        }
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


    private void setPhoneNumberFromIntent(Intent intent){

        this.PHONE_NUMBER = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
        if(this.PHONE_NUMBER == null){
            this.PHONE_NUMBER = intent.getStringExtra("incoming_num");
            if(this.PHONE_NUMBER == null){
                this.PHONE_NUMBER = "Unknown";
            }
        }
    }

    private void setCallDirectionFromIntent(Intent intent){

        this.CALL_DIRECTION = intent.getStringExtra("CallDirection");
    }

    private void setRecordingsDbHelper(){

        recordingsDbHelper = new RecordingsDbHelper(getApplicationContext());
        db = recordingsDbHelper.getWritableDatabase();
    }

    public int onStartCommand(Intent intent,int flags,int startId){

        sId = startId;
        setRecordingsDbHelper();
        setPhoneNumberFromIntent(intent);
        setCallDirectionFromIntent(intent);
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

        Intent recordingComplete = new Intent(getApplicationContext(),RecordingCompleteActivity.class);
        recordingComplete.putExtra("color", COLOR);
        recordingComplete.putExtra("nameOrNumber", NAME == null ? PHONE_NUMBER : NAME);
        recordingComplete.putExtra("path", getOutputFile());
        recordingComplete.putExtra("date",Long.toString(timeStamp));
        recordingComplete.putExtra("success", false);
        recordingComplete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(recordingComplete);
        File tmp = new File(getOutputFile());
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
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    public void stopRecording(){
        nullifyRecorder();
        //Check if the file is in the directory if so insert it into the database
        File tmp = new File(getOutputFile());
        if(tmp.exists()){
            insertRecordingInDb();
            Intent recordingComplete = new Intent(getApplicationContext(),RecordingCompleteActivity.class);
            recordingComplete.putExtra("color", COLOR);
            recordingComplete.putExtra("nameOrNumber", NAME == null ? PHONE_NUMBER : NAME);
            recordingComplete.putExtra("path",getOutputFile());
            recordingComplete.putExtra("date",Long.toString(timeStamp));
            recordingComplete.putExtra("success",true);
            recordingComplete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(recordingComplete);
        }
        stopSelf(sId);
    }

    public void onDestroy(){

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
        values.put(RecordingsContract.Recordings.COL_RECORDING_PATH, getOutputFile());
        values.put(RecordingsContract.Recordings.COL_LAST_MODIFIED,timeStamp);
        values.put(RecordingsContract.Recordings.COL_CALL_DIRECTION, CALL_DIRECTION);
        values.put(RecordingsContract.Recordings.COL_SAVED, "false");
        COLOR = RandomColorGenerator.generateRandomColor();
        values.put(RecordingsContract.Recordings.COL_COLOR,COLOR);
        db.insert(RecordingsContract.Recordings.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void onOffHook() {
        //The device went offhook and we are ready to start recording
        offHookCalled = true;
        timeStamp = System.currentTimeMillis();//Time the recording was started

        if(makeThreeAttempts()){
            return;
        }
        else{//Our three attempts with that source failed so we need to try others
            for(int i = 0; i <= 4;i++){
                if(startRecording(false,i)){
                    return;
                }else{
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
            if(startRecording(true,0)){
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
    public boolean startRecording(boolean fromPrefs,int source) {

        recorder = new MediaRecorder();
        if(fromPrefs){
            setAudioSourecFromPreference();
        }else{
            TryDifferentSource(source);
        }
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setOutputFile(getOutputFile());
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
        Toast.makeText(getApplicationContext(),"Recording",Toast.LENGTH_SHORT).show();
        return true;
    }

    public void TryDifferentSource(int source){
        switch (source){
            case 0:{
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                break;
            }
            case 1:{
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                break;
            }
            case 2:{
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                break;
            }
            case 3:{
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                break;
            }
            case 4:{
                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                break;
            }
            default:{
                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
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

    private String getOutputFile(){

        return GcrConstants.RECORDINGS_DIRECTORY + "/" + FILE_NAME + FILE_EXTENSION;
    }



    @Override
    public void onRinging() {

    }
}