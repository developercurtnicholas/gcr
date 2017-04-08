package com.curt.TopNhotch.GoldCallRecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 5/30/2016.
 */
public class OptionsAdapter extends BaseAdapter {
    protected Context context;
    protected GridContent[] gridContents;

    public OptionsAdapter(Context context,GridContent[] gridContents){
        this.context = context;
        this.gridContents = gridContents;
    }
    @Override
    public int getCount() {
        return this.gridContents.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.option_grid_cell,parent,false);
        TextView cellText =(TextView) v.findViewById(R.id.cell_text);
        ImageView cellImage = (ImageView)v.findViewById(R.id.cell_image);
        cellText.setText(gridContents[position].text);
        cellImage.setImageResource(gridContents[position].resId);

        return v;
    }
}
