package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Kurt on 5/16/2016.
 */
public class RecordingsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RECORDINGSDB";
    public static final int DATABASE_VERSION = 2;
    private Context context;
    private static final String CREATE_REC_TABLE = "CREATE TABLE "+RecordingsContract.Recordings.TABLE_NAME+"( "+
            RecordingsContract.Recordings._ID + " INTEGER PRIMARY KEY, "+
            RecordingsContract.Recordings.COL_CONTACT_NAME + " TEXT , "+
            RecordingsContract.Recordings.COL_NUMBER + " TEXT NOT NULL, "+
            RecordingsContract.Recordings.COL_LAST_MODIFIED + " TEXT ,"+
            RecordingsContract.Recordings.COL_CALL_DIRECTION + " TEXT ,"+
            RecordingsContract.Recordings.COL_COLOR + " TEXT ,"+
            RecordingsContract.Recordings.COL_NOTE + " TEXT ,"+
            RecordingsContract.Recordings.COL_RECORDING_PATH + " TEXT UNIQUE , "+
            RecordingsContract.Recordings.COL_SAVED + " TEXT )";

    private  static final String DROP_REC_TABLE = "DROP TABLE "+ RecordingsContract.Recordings.TABLE_NAME+";";
    public RecordingsDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(context,"onCreate", Toast.LENGTH_SHORT).show();
        db.execSQL(CREATE_REC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_REC_TABLE);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_REC_TABLE);
        onCreate(db);
    }
}
