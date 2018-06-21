package com.curt.TopNhotch.GCR.Utilities.RecordingManagement;

import android.content.Context;
import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.Models.Recording;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.GCR.Adapters.RecordingListAdapter;
import java.io.File;
import java.util.ArrayList;
/**
 * This class is responsible for keeping the integrity of recordings between different data sources.
 * these data sources are: <br><br>*/
public class RecordingSynchronizer extends Synchronizer {

    public RecordingSynchronizer(Context context){
        super(context);
    }

    public void delete(ArrayList<Recording> recordingsToDelete) throws Exception{

        int sourcesToDelete = dataSources.size();
        int sourcesDeleted = 0;

        for(RecordingDataSource dataSource : dataSources){

            if(dataSource.delete(recordingsToDelete)){

                sourcesDeleted++;
            }
        }

        if(sourcesToDelete != sourcesDeleted){

            throw new Exception("Some data sources did not perform deletion successfully");
        }
    }

    @Override
    public void populateDataSources(final Context context) {

        SqlLiteRecordingDataSource sqlLiteRecordingDataSource =
                new SqlLiteRecordingDataSource(context, "Database", new RecordingsDbHelper(context).getWritableDatabase());

        FileSystemRecordingDataSource fileSystemRecordingDataSource =
                new FileSystemRecordingDataSource(context,"File", new File(GcrConstants.RECORDINGS_DIRECTORY));

        AdapterRecordingDataSource adapterRecordingDataSource = new AdapterRecordingDataSource(context,"Adapter",new RecordingListAdapter(context,new ArrayList<Recording>()));

        this.addDataSource(sqlLiteRecordingDataSource);
        this.addDataSource(fileSystemRecordingDataSource);
        this.addDataSource(adapterRecordingDataSource);


    }
}
