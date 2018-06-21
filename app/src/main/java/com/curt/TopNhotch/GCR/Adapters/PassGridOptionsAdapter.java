package com.curt.TopNhotch.GCR.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 12/20/2016.
 */
public class PassGridOptionsAdapter extends OptionsAdapter {
    public PassGridOptionsAdapter(Context context, GridContent[] gridContents) {
        super(context, gridContents);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.pass_code_grid_cell,parent,false);
        ImageView cellImage = (ImageView)v.findViewById(R.id.pass_code_digit);
        cellImage.setImageResource(gridContents[position].resId);
        return v;
    }
}
