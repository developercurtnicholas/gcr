package com.curt.TopNhotch.GCR.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.curt.TopNhotch.GCR.Animation.Animation;
import com.curt.TopNhotch.GCR.ApplicationEvents.RepopulationEventListeners;
import com.curt.TopNhotch.GCR.AsyncTasks.RepopulateTask;
import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 6/15/2017.
 */

public class RepopulationActivity extends AppCompatActivity implements RepopulationEventListeners,RepopulateTask.RepopulationEvents {

    private RepopulateTask task;
    private Button repopComlete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.repopulating_db_layout);
        initUi();
        onStartRepopulation();
    }

    private void initUi(){
        this.repopComlete = (Button)findViewById(R.id.repopulation_complete_button);
        this.repopComlete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation.tap(repopComlete,0f,.7f);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void cancelTask(){
        if(task != null){
            task.cancel(true);
        }
    }

    @Override
    public void onStartRepopulation() {
        task = new RepopulateTask(this,this);
        task.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTask();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTask();
    }

    @Override
    public void onRepopulationComplete() {
        Animation.fade(repopComlete,0f,.7f);
    }

    @Override
    public void onRepopulationStarted() {

    }
}
