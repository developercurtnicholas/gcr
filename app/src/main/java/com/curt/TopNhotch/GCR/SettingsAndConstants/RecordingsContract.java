package com.curt.TopNhotch.GCR.SettingsAndConstants;
import android.provider.BaseColumns;
public final class RecordingsContract {

    public RecordingsContract(){
    }
    public static abstract class Recordings implements BaseColumns{
        public static final String TABLE_NAME = "RECORDINGS";
        public static final String COL_CONTACT_NAME = "CONTACT_NAME";
        public static final String COL_NUMBER = "NUMBER";
        public static final String COL_RECORDING_PATH = "PATH";
        public static final String COL_LAST_MODIFIED = "DATE";
        public static final String COL_CALL_DIRECTION = "DIRECTION";
        public static final String COL_SAVED = "SAVED";
        public static final String COL_COLOR = "COLOR";
        public static final String COL_NOTE = "NOTE";
        public static final String COL_PHOTO_URI = "PHOTO";
    }
}
