package com.curt.TopNhotch.GCR.Utilities.Factories;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

/**
 * Created by Kurt on 2/4/2018.
 */

public class DrawableFactory {

    public static RoundedBitmapDrawable createRoundImageFromUri(Context context,Uri uri) throws  Exception{

        try{
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),context.getContentResolver().openInputStream(uri));

            float width = drawable.getIntrinsicWidth();
            float heigh = drawable.getIntrinsicHeight();
            drawable.setCornerRadius(Math.max(width,heigh) / 2.0f);
            return drawable;

        }catch (Exception e){
            throw new Exception("Could Not Create Drawable: " + e.getMessage());
        }
    }
}
