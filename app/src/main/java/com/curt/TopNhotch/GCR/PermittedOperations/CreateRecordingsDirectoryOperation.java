package com.curt.TopNhotch.GCR.PermittedOperations;

import android.Manifest;
import android.content.Context;

import com.curt.TopNhotch.GCR.Utilities.RecordingsDirectoryUtils.DirectoryHelper;
import com.curt.TopNhotch.GCR.Utilities.UIUtils.DisplayMessages;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 1/6/2018.
 */

public class CreateRecordingsDirectoryOperation extends PermissionOperation {

    public CreateRecordingsDirectoryOperation(Context context) {
        super(context);
    }

    @Override
    protected void granted() {

        try{
            DirectoryHelper.createRecordingsDirectory();
        }catch (Exception e){
            e.printStackTrace();
            DisplayMessages.showDialog(context,"",context.getResources().getString(R.string.create_dir_failed));
        }
    }

    @Override
    protected void denied() {

        String title = context.getResources().getString(R.string.permission_denied);
        String msg = context.getResources().getString(R.string.create_dir_permission_denied);
        DisplayMessages.showDialog(context,title,msg);
    }

    @Override
    protected void callSetPermissionsHere() {
        setRequiredPermission(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });
    }
}
