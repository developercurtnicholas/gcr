package com.curt.TopNhotch.GCR.Utilities.DbUtils.DatabaseAsyncBuffer;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
public  abstract class AsyncBuffer<T,V>  {

    private Queue<T> queue;
    private AsyncBufferTask<T,V> asyncBufferTask;
    private boolean inProgress = false;
    private int amount = 1;
    public static final String LOG_TAG  = "AsyncBuffer";

    public AsyncBuffer(int amount){

        this.asyncBufferTask = new AsyncBufferTask<>(this);
        this.queue = new LinkedList<>();
        this.amount = amount;
    }

    public static void l(String msg){
        Log.i(LOG_TAG,msg);
    }


    public void addDataToBuffer(T data){

        l("addDataToBuffer(T data)");
        this.queue.add(data);
        l("added " + data.toString() + " to queue, SIZE: " + this.queue.size());
        if(!inProgress && queue.size() >= amount){
            l("Not In Progress");
            startLongOperation();
        }else{
            l("In Progress");
        }
    }

    private void executeAsyncTaskWithQueHead(){
        l("executeAsyncTaskWithQueHead()");
        try{
            this.asyncBufferTask = new AsyncBufferTask<>(this);
            asyncBufferTask.execute(queue.poll());
        }catch (Exception e){
            onLongOperationFailed();
            Log.i(LOG_TAG,e.getMessage());
        }
    }

    public void onLongOperationComplete(){

        l("onLongOperationComplete()");
        inProgress = false;
        if(isDataAvailable()){
            startLongOperation();
        }
    }

    private void startLongOperation(){

        onLongOperationStarted();
        executeAsyncTaskWithQueHead();
    }

    private void onLongOperationFailed(){
        l("onLongOperationFailed()");
        onLongOperationComplete();
    }

    private boolean isDataAvailable(){
        if(queue.size() > 0){
            return true;
        }else{
            return false;
        }
    }

    public void onLongOperationStarted(){
        l("onLongOperationStarted() : ");
        inProgress = true;
    }

    public abstract V doLongOperation(T data);

    public abstract void getResults(V results);
}
