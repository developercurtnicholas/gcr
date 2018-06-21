package com.curt.TopNhotch.GCR.Utilities.AndroidHelpers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kurt on 2/2/2018.
 */

public class RecyclerViewBuilder {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private Activity activity;
    private int id;
    private int layoutManagerDirection;



    public static class LayoutManagerDirection{

        public static int VERTICAL = 1;
        public static int HORIZONTAL = 2;
    }

    public RecyclerViewBuilder(int recyclerViewId,Activity activity){

        this.id = recyclerViewId;
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }


    public RecyclerViewBuilder CreateBasicRecyclerView(int layoutManagerDirection,RecyclerView.Adapter adapter) throws Exception{

        this.layoutManagerDirection = layoutManagerDirection;
        this.adapter = adapter;

        this.createRecyclerView();
        this.setRecyclerViewLayoutManager();
        this.setBasicRecyclerViewAdapter(adapter);

        return this;
    }

    public RecyclerView create(){

        return this.recyclerView;
    }

    private void createRecyclerView() throws Exception{

        try{
            this.recyclerView = (RecyclerView) activity.findViewById(id);

        }catch (Exception e) {

            throw new Exception("Could Not Find View with that Id: " + id);
        }
    }

    private void setRecyclerViewLayoutManager() throws Exception{

        if(layoutManagerDirection == LayoutManagerDirection.VERTICAL){

            this.layoutManager = new LinearLayoutManager(this.context);

        }else if(layoutManagerDirection == LayoutManagerDirection.HORIZONTAL){

        }else{
            throw new Exception("Invalid Layout Manager Direction: Please use LayoutManagerDirection.HORIZONTAL or LayoutManagerDirection.VERTICAL");
        }
    }

    private void setBasicRecyclerViewAdapter(RecyclerView.Adapter adapter){

        this.adapter = adapter;
        this.recyclerView.setAdapter(this.adapter);
    }
}
