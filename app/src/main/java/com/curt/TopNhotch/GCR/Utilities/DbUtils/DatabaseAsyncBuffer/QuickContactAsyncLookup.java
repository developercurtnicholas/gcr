package com.curt.TopNhotch.GCR.Utilities.DbUtils.DatabaseAsyncBuffer;

import android.content.Context;

import com.curt.TopNhotch.GCR.Utilities.Miscellaneous.LookUpContact;
import com.curt.TopNhotch.GCR.Models.Contact;

import java.util.ArrayList;

/**
 * Created by Kurt on 2/7/2018.
 */

public class QuickContactAsyncLookup extends AsyncBuffer<String,ArrayList<Contact>> {

    Context context;
    private ResultListener listener;
    public QuickContactAsyncLookup(int amount, ResultListener listener, Context context){

        super(amount);
        this.context = context;
        this.listener = listener;
    }

    public static interface ResultListener{

        void OnContactsRetrieved(ArrayList<Contact> contacts);
    }

    @Override
    public ArrayList<Contact> doLongOperation(String data) {

        ArrayList<Contact> contacts = LookUpContact.getContacts(data,context);
        return contacts;
    }

    @Override
    public void getResults(ArrayList<Contact> results) {

        l("getResults(ArrayList<Contact> results)");
        listener.OnContactsRetrieved(results);
        /*for(Contact result : results  ){

            Log.i(LOG_TAG, result.getName() != null ? result.getName() : result.getNumber());
        }*/
    }
}
