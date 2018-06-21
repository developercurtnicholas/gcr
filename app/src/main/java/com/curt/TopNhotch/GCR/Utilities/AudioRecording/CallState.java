package com.curt.TopNhotch.GCR.Utilities.AudioRecording;

public interface CallState{
    void onIdle();

    void onOffHook();

    void onRinging();
}
