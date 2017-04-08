package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

/**
 * Created by Kurt on 4/26/2016.
 */
public class LookUpContact {
    public static String getNameByNumber(String number,Context context){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        //Columns to fetch
        String[] projection = {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID,
                ContactsContract.PhoneLookup.LOOKUP_KEY
        };

        //Value to match
        String[] selectionArgs = {number};

        //Sort order doesn't matter
        String sortOrder = ContactsContract.CommonDataKinds.Phone.NUMBER +" ASC";
        //what to select. Dictated by the selection args
        String selectionClause = ContactsContract.CommonDataKinds.Phone.NUMBER +" = ? ";

        //Query the contacts and return a Cursor with the values
        Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                selectionClause,
                selectionArgs,
                sortOrder);
        if(cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return  cursor.getString(0);
        }else{
            return null;
        }
    }
    public static String getNumberByName(String Name,Context context){
        String DISPLAYNAME = "";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            DISPLAYNAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
        }
        else{
            DISPLAYNAME = ContactsContract.Contacts.DISPLAY_NAME;
        }
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.LOOKUP_KEY
        };

        String[] selectionArgs = {Name};

        String sortOrder = DISPLAYNAME +" ASC";
        String selectionClause = DISPLAYNAME +"= ?";
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selectionClause,
                selectionArgs,
                sortOrder);
        if(cursor.getCount() > 0 ){
            cursor.moveToFirst();
            return  cursor.getString(0);
        }else{
            return null;
        }
    }
}
