package com.curt.TopNhotch.GCR.CustomComponents.Sliders;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SliderAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<T> sliderAdapterItems;
    private Slider.DataViewBinder binder;
    private Slider.ViewHolderCreator creator;
    public SliderAdapter(ArrayList<T> dataSet,Slider.ViewHolderCreator creator,Slider.DataViewBinder binder){

        this.creator = creator;
        this.sliderAdapterItems = dataSet;
        this.binder = binder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return this.creator.createViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        binder.bind(this.sliderAdapterItems.get(position),holder);
    }

    @Override
    public int getItemCount() {
        return sliderAdapterItems.size();
    }
}
