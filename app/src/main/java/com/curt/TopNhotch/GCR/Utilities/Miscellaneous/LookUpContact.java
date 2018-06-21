package com.curt.TopNhotch.GCR.Utilities.Miscellaneous;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.curt.TopNhotch.GCR.Models.Contact;

import java.util.ArrayList;

/**
 * Created by Kurt on 4/26/2016.
 */
public class LookUpContact {

    public static Cursor queryContacts(String phoneNumber,Context context,boolean like){

        //Uri contentUri = ContactsContract.Data.CONTENT_URI;
        //Uri contentUri = ContactsContract.Contacts.CONTENT_URI;
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        /*Uri contentUri =
                Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));*/

        String[] columns = {
                ContactsContract.RawContacts._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER

        };
        String comparison = "";
        String[] args;
        if(like){
            args = new String[]{"%"+phoneNumber+"%"};
            comparison = " LIKE ? ";
        }else{
            args = new String[]{phoneNumber};
            comparison = " = ?";
        }
        Cursor c = context.getContentResolver().query(
                contentUri,
                columns,
                ContactsContract.CommonDataKinds.Phone.NUMBER + comparison,
                args,
                null
        );
        return c;
    }
    public static Contact getContact(String phoneNumber, Context context) {

        Cursor cursor = queryContacts(phoneNumber,context,false);

        if (checkCursorNullOrEmpty(cursor)) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            String photouri = cursor.getString(2);
            String name = cursor.getString(1);
            cursor.close();
            return new Contact(name,phoneNumber,photouri);
        }
    }
    public static ArrayList<Contact> getContacts(String phoneNumber,Context context){

        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor cursor = queryContacts(phoneNumber,context,true);

        if (checkCursorNullOrEmpty(cursor)) {
            cursor.close();
            return contacts;
        } else {

            while (cursor.moveToNext()){
                String photouri = cursor.getString(2);
                String name = cursor.getString(1);
                String number = cursor.getString(3);
                contacts.add(new Contact(name,number,photouri));
            }
            cursor.close();
        }
        cursor.close();
        return contacts;
    }
    public static boolean checkCursorNullOrEmpty(Cursor cursor){

        if(cursor == null){
            return true;
        }
        else if(cursor.getCount() < 1){
            return true;
        }else{
            return false;
        }
    }
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
        if(cursor != null){
            if(cursor.getCount() > 0 ){
                cursor.moveToFirst();
                return  cursor.getString(0);
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
}
