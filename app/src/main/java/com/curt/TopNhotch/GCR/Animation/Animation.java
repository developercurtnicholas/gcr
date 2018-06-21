package com.curt.TopNhotch.GCR.Animation;

import android.animation.ObjectAnimator;
import android.view.View;

public class Animation {

    public static void tap(View v,float from,float to){
        ObjectAnimator oa = ObjectAnimator.ofFloat(v,"alpha",from,to);
        oa.setDuration(50);
        oa.start();
    }

    public static void fade(View v,float from,float to,int duration){
        ObjectAnimator oa = ObjectAnimator.ofFloat(v,"alpha",from,to);
        oa.setDuration(duration);
        oa.start();
    }

    public static void fade(View v,float from,float to){

        ObjectAnimator oa = ObjectAnimator.ofFloat(v,"alpha",from,to);
        oa.setDuration(1000);
        oa.start();
    }
}
