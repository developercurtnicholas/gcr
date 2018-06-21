package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.content.Context;

import com.curt.TopNhotch.GCR.Models.Recording;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Kurt on 9/24/2017.
 */

public class FileSystemRecordingDataSource extends RecordingDataSource<File> {

    public FileSystemRecordingDataSource(Context context,String Id, File data) {
        super(context,Id,data);
    }

    @Override
    public boolean delete(ArrayList<Recording> recordingsToDelete){

        int amountToDelete = recordingsToDelete.size();
        int amountDeleted = 0;

        for (Recording recording : recordingsToDelete) {
            File f = new File(recording.path);
            if(f.delete()){

                amountDeleted++;
            }
        }

        if(amountDeleted == amountToDelete){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public ArrayList<Recording> getRecordings() {

        ArrayList<Recording> recordings = new ArrayList<>();
        File[] files = dataSource.listFiles();

        for(File f : files){
            recordings.add(Recording.createRecordingFromFileName(f.getName()));
        }
        return recordings;
    }
}
