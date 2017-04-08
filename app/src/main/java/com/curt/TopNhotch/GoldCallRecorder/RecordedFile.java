package com.curt.TopNhotch.GoldCallRecorder;

public class RecordedFile {
    String name = "";
    String number = null;
    String timeStamp = "";
    String callDirection = "";
    String path = "";
    String title = "";
    String duration = "";
    String description = "";

    RecordedFile(String number,String timeStamp,String callDirection,String path){
        this.number = number;
        this.timeStamp = timeStamp;
        this.callDirection = callDirection;
        this.path = path;
    }

}
