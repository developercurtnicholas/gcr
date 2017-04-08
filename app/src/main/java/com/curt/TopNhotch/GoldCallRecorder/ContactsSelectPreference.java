package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.ListView;

import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 8/10/2016.
 */
public class ContactsSelectPreference extends DialogPreference {

    private int currentValue = 1;
    private static final int DEFAULT_VALUE = 5;
    private ListView contactsSettingsList;
    private static class SavedState extends BaseSavedState{

        int value;

        public SavedState(Parcel source) {
            super(source);
            value = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags){
            super.writeToParcel(dest,flags);
            dest.writeInt(value);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>(){

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public ContactsSelectPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.options_dialog);
    }

    protected void onDialogClosed(boolean positiveResult){
        if(positiveResult){
            persistInt(currentValue);
        }
    }

    @Override
    protected  void onSetInitialValue(boolean restorePersistedValue, Object defaultValue){

        if(restorePersistedValue){
            currentValue = getPersistedInt(DEFAULT_VALUE);
        }else{
            currentValue = (Integer)defaultValue;
            persistInt(currentValue);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState(){
        final Parcelable superState = super.onSaveInstanceState();
        if(isPersistent()){
            return superState;
        }
        final SavedState myState = new SavedState(superState);
        myState.value = currentValue;
        return myState;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
    }
}
