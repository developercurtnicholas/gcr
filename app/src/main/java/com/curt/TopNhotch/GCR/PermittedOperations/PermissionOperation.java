package com.curt.TopNhotch.GCR.PermittedOperations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public abstract class PermissionOperation{

    protected String[] requiredPermissions = null;
    protected Context context;

    protected PermissionOperation THIS = this;

    public PermissionOperation(Context context){
        callSetPermissionsHere();
        this.context = context;
    }

    private boolean arePermissionsNull(){
        if(this.requiredPermissions == null)
            return true;
        else return false;
    }

    public void execute() {
            //If no permissions were passed, then we just do the operation
            if(requiredPermissions == null){
                onPermissionsGranted();
                return;
            }

            //If permissions were passed we check if we have all required permissions before doing
        //the operation
            if(PermissionHelper.checkPermissions(requiredPermissions,context)){
                  granted();
            }else{
                PermissionRequest.request(context, requiredPermissions, new PermissionRequest.Listener() {
                    @Override
                    public void permissionsGranted(Activity activity) {
                        activity.finish();
                        granted();
                    }

                    @Override
                    public void permissionsDenied(Activity activity) {
                        activity.finish();
                        denied();
                    }
                });
            }
    }

    private void onPermissionsGranted(){
        granted();
    }
    private void onPermissionDenied(){
        denied();
    }

    protected  abstract void granted();
    protected  abstract void denied();
    protected abstract void callSetPermissionsHere();

    protected void setRequiredPermission(String[] permissions){
        this.requiredPermissions = permissions;
    }

    public static class PermissionRequest extends Activity {

        private final int PERMISSION_REQUEST_CODE = 10;
        private static final String PERMISSION_INTENT_STRING = "PERMISSION";
        private static Listener listener;
        private static String[] passedPermission = null;


        //Listener for request result
        public static interface Listener{
            void permissionsGranted(Activity activity);
            void permissionsDenied(Activity activity);
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            Intent intent = getIntent();
            makeRequest(passedPermission);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults) {

            if(areAllGranted(grantResults)){//All were granted
                grant();
            }else{//At least one was denied
                deny();
            }
        }

        private  void grant(){
            listener.permissionsGranted(this);
        }
        private void deny(){
            listener.permissionsDenied(this);
        }

        //If any one of the permissions were denied
        private static boolean areAllGranted(int[] grantResults){

            if(grantResults.length <= 0){
                return false;
            }

            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }


        private boolean areAllGranted(String[] permissions){
            if(permissions == null)
                return true;
            for(String pemission : permissions){
                int value = ContextCompat.checkSelfPermission(this,pemission);

                if(value != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }

        private void makeRequest(String[] permissions){
            //If at least one of the permissions in the list are not granted
            if(!areAllGranted(permissions)){

                //Request the permissions
                ActivityCompat.requestPermissions(this,
                        permissions,
                        PERMISSION_REQUEST_CODE);

            }else{//All Permissions already granted
                try{
                    grant();
                }catch (Exception e){
                   e.printStackTrace();
                }
            }
        }

        public static void request(Context context, String[] permissions,Listener permissionResultListener){

            listener = permissionResultListener;
            passedPermission = permissions;
            Intent intent = new Intent(context,PermissionRequest.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
    public static class PermissionHelper{

        public static final int READ_CONTATCTS = 1;
        public static final int RECORD_AUDIO = 2;
        public static final int READ_EXTERNAL_STORAGE = 3;
        public static final int WRITE_EXTERNAL_STORAGE = 4;
        public static final int READ_PHONE_STATE = 5;
        public static final int PROCESS_OUTGOING_CALLS = 6;

        public static boolean checkPermissions(String[] permissions,Context context){

            for(String permission : permissions){
               if(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED){
                   return false;
               }
            }
            return true;
        }
    }
}
