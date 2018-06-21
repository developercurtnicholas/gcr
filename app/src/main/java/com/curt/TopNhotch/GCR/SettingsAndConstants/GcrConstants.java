package com.curt.TopNhotch.GCR.SettingsAndConstants;

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
    public static final String PASSWORD_EXTRA_KEY = "passcode_correct";


    /**
     * This constant refers to a shared preference file that stores all the settings about recording from the dialer*/
    public static final String DIALER_RECORDING_SETTINGS = "call_settings";
    /**
     * This constant is a preference within DIALER_RECORDING_SETTINGS that tell wether we should record or not*/
    public static final String DIALER_SHOULD_RECORD = "shoul_record";

    /**
     * This constant is a preference within DIALER_RECORDING_SETTINGS that tells wether the call is made from the dialer*/
    public static final String DIALER_CALL_FROM_DIALER = "from_dialer";

    public static int TAB_SELECTED = 0;
    public static final String[] PROJECTION = {
            RecordingsContract.Recordings._ID,
            RecordingsContract.Recordings.COL_CONTACT_NAME,
            RecordingsContract.Recordings.COL_NUMBER,
            RecordingsContract.Recordings.COL_LAST_MODIFIED,
            RecordingsContract.Recordings.COL_CALL_DIRECTION,
            RecordingsContract.Recordings.COL_RECORDING_PATH,
            RecordingsContract.Recordings.COL_COLOR,
            RecordingsContract.Recordings.COL_NOTE,
            RecordingsContract.Recordings.COL_PHOTO_URI
    };
    
}
