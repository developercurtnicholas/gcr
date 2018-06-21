package com.curt.TopNhotch.GCR.CustomComponents.Sliders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.curt.TopNhotch.GCR.Utilities.AndroidHelpers.RecyclerViewBuilder;

import java.util.ArrayList;

public class Slider<T> {

    private ArrayList<T> dataSet;
    private DataViewBinder<T> binder;
    private RecyclerView recyclerView;
    private SliderAdapter adapter;
    private ViewHolderCreator creator;
    public Slider(Activity activity,int recyclerViewId,ArrayList<T> dataSet,ViewHolderCreator creator, DataViewBinder binder) throws Exception{

        this.dataSet = dataSet;
        this.binder = binder;
        this.creator = creator;
        this.adapter = new SliderAdapter(this.dataSet,this.creator,this.binder);

        try{
            this.recyclerView = new RecyclerViewBuilder(recyclerViewId,activity)
                    .CreateBasicRecyclerView(RecyclerViewBuilder.LayoutManagerDirection.HORIZONTAL,adapter)
                    .create();
        }catch (Exception e){
            throw new Exception("Could Not Create Slider: " + e.getMessage());
        }


    }

    public static interface DataViewBinder<T>{

        public void bind(T dataSet, RecyclerView.ViewHolder holder);
    }

    public static interface ViewHolderCreator<T>{

        public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType);
    }
}
