package com.curt.TopNhotch.GoldCallRecorder;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;

/**
 * Created by Kurt on 1/6/2017.
 */
public class RepopulateService extends IntentService {

    public RepopulateService(){
        super("RepopulateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int count = intent.getIntExtra("count", 0);
        Log.i("COUNT----",""+count);
        int c;
        if(count > 0){
            c = count-1;
        }else{
            c = count;
        }
        SQLiteDatabase db = new RecordingsDbHelper(getApplicationContext()).getWritableDatabase();
        String fileName = "";
        String path = GcrConstants.RECORDINGS_DIRECTORY;
        File file = new File(path);
        File[] files = file.listFiles();
        for (int i = c; i < files.length;i++) {
            File f = files[c];
            Log.i("Inserting: ",""+c);
            c++;
            fileName = f.getName();
            RecordedFile parsedValues = DirectoryAdapter.parseFileName(fileName);
            if (parsedValues != null) {
                ContentValues values = new ContentValues();
                String name = LookUpContact.getNameByNumber(parsedValues.number, getApplicationContext());
                if (name != null) {
                    values.put(RecordingsContract.Recordings.COL_CONTACT_NAME, name);
                } else {
                    values.put(RecordingsContract.Recordings.COL_CONTACT_NAME, "");
                }
                values.put(RecordingsContract.Recordings.COL_RECORDING_PATH, parsedValues.path);
                values.put(RecordingsContract.Recordings.COL_NUMBER, parsedValues.number);
                values.put(RecordingsContract.Recordings.COL_LAST_MODIFIED, parsedValues.timeStamp);
                values.put(RecordingsContract.Recordings.COL_CALL_DIRECTION, parsedValues.callDirection);
                values.put(RecordingsContract.Recordings.COL_SAVED, "false");
                values.put(RecordingsContract.Recordings.COL_COLOR, CallRecorderService.generateRandomColor());
                db.insert(RecordingsContract.Recordings.TABLE_NAME, null, values);
            }
        }
        db.close();
    }
}
