package com.curt.TopNhotch.GCR.Models;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.curt.TopNhotch.GCR.Utilities.Factories.DrawableFactory;
import com.curt.TopNhotch.myapplication.R;

import java.util.Random;


public class Contact {
    String name;
    String number;
    String contactsPhotoUriString;
    public Uri contactsPhotoUri;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public Contact(){

    }

    public Contact(String name, String number){
        this.name = name;
        this.number= number;
    }

    public void setContactPhotoLetter(TextView textView,String text){

        textView.setText(text);
    }

    public void setPhoto(Context context, View view){
        if(this.contactsPhotoUri == null){

            Random rand = new Random();
            int  n = rand.nextInt(5) + 1;
            switch (n){
                case 1: view.setBackgroundResource(R.drawable.color_shadow_green);
                    break;
                case 2: view.setBackgroundResource(R.drawable.circle_shadow_red);
                    break;
                case 3: view.setBackgroundResource(R.drawable.circle_shadow_blue);
                    break;
                case 4: view.setBackgroundResource(R.drawable.color_shadow_gold);
                    break;
                case 5: view.setBackgroundResource(R.drawable.color_shadow_purple);
            }
            TextView textView = (TextView)view;
            this.setContactPhotoLetter(textView,this.getName().charAt(0)+"");
        }else{
            try{
                view.setBackground(DrawableFactory.createRoundImageFromUri(context,this.contactsPhotoUri));
            }catch (Exception e){

            }
        }
    };

    public boolean hasPhoto(){

        if(this.contactsPhotoUri != null){
            return true;
        }else{
            return false;
        }
    }

    public Contact(String name, String number, String contactsPhotoUriString){
        this.name = name;
        this.number = number;
        this.contactsPhotoUriString = contactsPhotoUriString;
        if(contactsPhotoUriString != null || contactsPhotoUriString != ""){
            try{
                this.contactsPhotoUri = Uri.parse(contactsPhotoUriString);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public String toString(){
        return  " Name: " + this.name != null ? this.name : " null " +
                " Number: " + this.number != null ? this.number : " null " +
                " Uri: " + this.contactsPhotoUri != null ? this.contactsPhotoUri.toString() : " null ";
    }
}
