package com.curt.TopNhotch.GoldCallRecorder;

import android.os.Environment;

/**
 * Created by Kurt on 5/25/2016.
 */
public class GcrConstants{
    public static final String RECORDINGS_DIRECTORY = Environment.getExternalStorageDirectory().toString()+"/GRrecordings";
    public static final String RECORDING_SOURCE = "recording_source_key";
    public static final String RECORDING_FILE_TYPE = "file_type_key";
    public static final String RECORDING_ON_OFF = "recording_on_off_key";
    public static final String SECURITY_ON_OFF = "security_lock_on_off_key";
    public static final String SECURITY_PASSWORD = "security_password";
    public static final String INBOX_SIZE = "inbox_size_key";
    public static boolean NO_RECORDINGS = true;
    public static boolean REPOPULATE = false;
    public static String FILE_TYPE = ".3gp";
    public static int TAB_SELECTED = 0;
    public static final String[] PROJECTION = {
            RecordingsContract.Recordings._ID,
            RecordingsContract.Recordings.COL_CONTACT_NAME,
            RecordingsContract.Recordings.COL_NUMBER,
            RecordingsContract.Recordings.COL_LAST_MODIFIED,
            RecordingsContract.Recordings.COL_CALL_DIRECTION,
            RecordingsContract.Recordings.COL_RECORDING_PATH,
            RecordingsContract.Recordings.COL_COLOR,
            RecordingsContract.Recordings.COL_NOTE
    };
}
