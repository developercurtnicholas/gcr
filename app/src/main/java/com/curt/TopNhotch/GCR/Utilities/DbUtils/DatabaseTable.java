package com.curt.TopNhotch.GCR.Utilities.DbUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kurt on 5/13/2016.
 */
public class DatabaseTable  {

    public static final String COL_NAME = "NAME";
    public static final String COL_NUMBER = "NUMBER";

    private static final String DATABASE_NAME = "RECORDINGS";
    private static final int DATABASE_VERSION = 1;
    private static final String FTS_VIRTUAL_TABLE = "FTS";

    private final DatabaseOpenHelper helper = null;

    public DatabaseTable(Context context){

    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper{

        private Context helperContext = null;
        private SQLiteDatabase database;

        private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE "+ FTS_VIRTUAL_TABLE + "Using fts3 ("+
                COL_NAME + ", "+COL_NUMBER + ")";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            helperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            database = db;
            database.execSQL(FTS_TABLE_CREATE);
            ContentValues values = new ContentValues();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+FTS_VIRTUAL_TABLE);

            onCreate(db);
        }
    }
}
