package com.curt.TopNhotch.GCR.Utilities.DbUtils.DatabaseAsyncBuffer;

import android.os.AsyncTask;

/**
 * Created by Kurt on 2/8/2018.
 */

public  class AsyncBufferTask<T,V> extends AsyncTask<T,Integer,V> {

    private AsyncBuffer<T,V> asyncBuffer;

    public AsyncBufferTask(AsyncBuffer<T,V> buffer){
        this.asyncBuffer = buffer;
    }

    @Override
    protected V doInBackground(T... params) {
        AsyncBuffer.l("doInBackground(T... params) : " + params[0].toString());
        return this.asyncBuffer.doLongOperation(params[0]);
    }

    @Override
    protected void onPostExecute(V v) {
        AsyncBuffer.l("onPostExecute(V v) : " + v.toString());
        super.onPostExecute(v);
        this.asyncBuffer.getResults(v);
        this.asyncBuffer.onLongOperationComplete();
    }
}