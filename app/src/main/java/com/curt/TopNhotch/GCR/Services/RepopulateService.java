package com.curt.TopNhotch.GCR.Services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingSaverUtility;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.LookUpContact;
import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.RandomColorGenerator;

import java.io.File;

public class RepopulateService extends IntentService {

    public RepopulateService(){
        super("RepopulateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int count = intent.getIntExtra("count", 0);
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
            c++;
            fileName = f.getName();
            Recording parsedValues =  Recording.createRecordingFromFileName(fileName);
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
                values.put(RecordingsContract.Recordings.COL_SAVED,
                        RecordingSaverUtility.SaveValues.UNSAVE.getValue());
                values.put(RecordingsContract.Recordings.COL_COLOR, RandomColorGenerator.generateRandomColor());
                db.insert(RecordingsContract.Recordings.TABLE_NAME, null, values);
            }
        }
        db.close();
    }
}
