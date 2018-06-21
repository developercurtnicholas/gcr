package com.curt.TopNhotch.GCR.Utilities.DbUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;

import java.util.ArrayList;

/**
 * Created by Kurt on 5/16/2016.
 */
public class RecordingsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "RECORDINGSDB";
    public static final int DATABASE_VERSION = 3;
    private Context context;

    private static final String CREATE_REC_TABLE = "CREATE TABLE "+ RecordingsContract.Recordings.TABLE_NAME+"( "+
            RecordingsContract.Recordings._ID + " INTEGER PRIMARY KEY, "+
            RecordingsContract.Recordings.COL_PHOTO_URI + " TEXT, "+
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

    public static boolean truncate(Context context){

        RecordingsDbHelper helper = new RecordingsDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        try{
            db.execSQL("delete from " + RecordingsContract.Recordings.TABLE_NAME);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Pass The column,value, wether to compare with like or = and the amount to limit by(or pass anything less than 1 to not add a limit)
     *
     * @param context
     * @param column
     * @param value
     * @param like
     * @param limit
     * @return
     */
    public static ArrayList<Recording> filterRecordingByColoumn(Context context, String column, String value, boolean like,int limit){

        //TODO: This is entire method is not yet tested or ever used!!!
        RecordingsDbHelper helper = new RecordingsDbHelper(context);
        String comparison = like ? " LIKE " : " = ";
        String limitQuery = limit > 0 ? " LIMIT " + limit : "";
        SQLiteDatabase db = helper.getWritableDatabase();
        try{
            Cursor data = db.rawQuery(
                    "select * from " +
                    RecordingsContract.Recordings.TABLE_NAME +
                            " where " + comparison + " " +  value + limitQuery,null);
            return Recording.createRecordingsFromCursor(data,context);
        }catch (Exception e){
            try{
                Log.e("filterFailed: ", e.getMessage());
            }catch (Exception e2){
                e2.printStackTrace();
            }
            return new ArrayList<Recording>();
        }
    }

    public static long getRecordingCount (Context context){

        RecordingsDbHelper helper = new RecordingsDbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        long count = DatabaseUtils.queryNumEntries(db, RecordingsContract.Recordings.TABLE_NAME);
        return count;
    }

    public static boolean wasRowInserted(long id){

        if(id != -1){
            return true;
        }else{
            return false;
        }
    }

    public static SQLiteDatabase getReadableAndWriteableDB(Context context){
        return new RecordingsDbHelper(context).getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
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
