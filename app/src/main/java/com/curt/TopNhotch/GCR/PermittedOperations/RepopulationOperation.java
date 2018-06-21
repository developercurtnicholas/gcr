package com.curt.TopNhotch.GCR.PermittedOperations;

import android.Manifest;
import android.content.Intent;

import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.FileManager;
import com.curt.TopNhotch.GCR.Activities.MainActivity;
import com.curt.TopNhotch.GCR.Utilities.RecordingsDirectoryUtils.DirectoryHelper;
import com.curt.TopNhotch.GCR.Utilities.UIUtils.DisplayMessages;
import com.curt.TopNhotch.GCR.Activities.RepopulationActivity;
import com.curt.TopNhotch.myapplication.R;


public class RepopulationOperation extends PermissionOperation{

    MainActivity activity;

    public RepopulationOperation(MainActivity activity) {
        super(activity.getApplicationContext());
        this.activity = activity;
    }

    @Override
    protected void granted(){

        FileManager manager = new FileManager(context);
        if(manager.checkExternalStorageAvailable()){
            activity.externalStorageAvailable.execute();
            if(DirectoryHelper.shouldRepopulate(activity)){
                Intent intent = new Intent(activity, RepopulationActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }else{
                activity.repopcomplete.execute();
            }
        }else{
            DisplayMessages.showDialog(context,"",context.getResources().getString(R.string.No_External_Storage));
        }
    }

    @Override
    protected void denied() {

    }

    @Override
    protected void callSetPermissionsHere() {
        setRequiredPermission(new String[]{

                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS
        });
    }
}
