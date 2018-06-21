package com.curt.TopNhotch.GCR.Playaudio;

import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.curt.TopNhotch.GCR.SettingsAndConstants.RecordingsContract;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.RecordingsDbHelper;
import com.curt.TopNhotch.myapplication.R;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Kurt on 4/27/2016.
 */
public class RecordingPlayer extends AppCompatActivity {

    private SeekBar seek;
    private TextView playerTime;
    private TextView nameOrNumber;
    private TextView recordingDate;
    private MediaPlayer player;
    private String FILE_PATH = "";
    private String nameNumber;
    private String date;
    private String titleText;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private EditText addTitle;

    private Handler handler;
    private int hrs;
    private String totalDuration;
    private int mins;
    private int secs;
    private boolean startTouch = false;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_layout);

        processIntent();
        initializeComponents();
        registerListeners();
        setUpMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlaying();
        //Update the description when the person leaves the activity
        if(!addTitle.getText().toString().equals("") && addTitle.getText().toString() != null){
            ContentValues values = new ContentValues();
            values.put(RecordingsContract.Recordings.COL_NOTE, addTitle.getText().toString());
            String selection = RecordingsContract.Recordings.COL_RECORDING_PATH + " = ?";
            SQLiteDatabase db = new RecordingsDbHelper(getApplicationContext()).getWritableDatabase();
            String[] args = {FILE_PATH};
            db.update(
                    RecordingsContract.Recordings.TABLE_NAME,
                    values,
                    selection,
                    args
            );
            db.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlaying();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    public void processIntent(){
        Intent intent = this.getIntent();

        //Get the file path of the recording from the intent
        this.FILE_PATH = intent.getStringExtra("path");

        //Get the persons name/number
        this.nameNumber = intent.getStringExtra("nameOrNumber");

        //Get the description/note for the recording if there is one
        String n = intent.getStringExtra("note");
        if(n !=  null){
            this.titleText = intent.getStringExtra("note");
        }

        //Get the color of the circle
        String color = intent.getStringExtra("color");

        //Get the date
        date = intent.getStringExtra("date");

        //Intialize the colored circle with the color recieved and set the letter in the center
        TextView circle = (TextView)findViewById(R.id.play_recording_image);
        switch (color){
            case "green": circle.setBackgroundResource(R.drawable.color_shadow_green);
                break;
            case "red": circle.setBackgroundResource(R.drawable.circle_shadow_red);
                break;
            case "blue": circle.setBackgroundResource(R.drawable.circle_shadow_blue);
                break;
            case "gold": circle.setBackgroundResource(R.drawable.color_shadow_gold);
                break;
            case "purple": circle.setBackgroundResource(R.drawable.color_shadow_purple);
        }
        circle.setText(nameNumber.charAt(0)+"");
    }

    //Self explanitory
    public void initializeComponents(){
        seek = (SeekBar)findViewById(R.id.seekbar);
        playButton = (Button)findViewById(R.id.playButton);
        playButton.setClickable(false);
        addTitle = (EditText)findViewById(R.id.play_recording_title_desc);
        addTitle.setText(titleText);
        pauseButton = (Button)findViewById(R.id.pauseButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        playerTime = (TextView)findViewById(R.id.playerTime);
        playerTime.setText("00:00:00 | ");
        nameOrNumber = (TextView)findViewById(R.id.nameOrNumber);
        nameOrNumber.setText(nameNumber);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        recordingDate = (TextView)findViewById(R.id.play_recording_date);
        recordingDate.setText(date);
    }


    public void registerListeners(){
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Animation for whe the play button is clicked
                ObjectAnimator on = ObjectAnimator.ofFloat(v,"alpha",0f,1f);
                on.setDuration(500);
                on.start();

                if(player != null){

                    player.start();
                    seek.setMax(player.getDuration() / 1000);
                    Log.i("##INFO: Duration: ", (player.getDuration() / 1000) + "");
                    handler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (player != null) {
                                int pos = player.getCurrentPosition();
                                int timeInS = pos / 1000;
                                seek.setProgress(pos / 1000);

                                secs = timeInS % 60;
                                mins = (timeInS / 60) % 60;
                                hrs = (timeInS / 60) / 60;
                                String hr = hrs < 10 ? "0" + hrs : "" + hrs;
                                String min = mins < 10 ? "0" + mins : "" + mins;
                                String sec = secs < 10 ? "0" + secs : "" + secs;
                                playerTime.setText(hr + ":" + min + ":" + sec + " | " + totalDuration);
                            }
                            handler.postDelayed(this, 1000);
                        }
                    });
                }

            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator on = ObjectAnimator.ofFloat(v,"alpha",0f,1f);
                on.setDuration(500);
                on.start();
                if (player != null) {
                    if (player.isPlaying()) {
                        player.pause();
                    }
                }
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ObjectAnimator on = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
                on.setDuration(500);
                on.start();
                stopPlaying();
                setUpMediaPlayer();
            }
        });
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("##INFO:", progress + "");
                if (startTouch) {
                    player.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                startTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startTouch = false;
            }
        });
    }

    public void stopPlaying() {

        if (player != null) {
            player.stop();
            player.release();
            player = null;
            setUpMediaPlayer();
        }
    }

    public void pause(){
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            }
        }
    }

    public void startPlaying(){

    }

    public void setPlayingNotification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.playbutton)
                .setContentTitle(this.nameOrNumber.getText())
                .setContentText("Playing...");

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1,builder.build());
    }
    public void setUpMediaPlayer(){
        player = new MediaPlayer();
        try {
            player.setDataSource(FILE_PATH);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playButton.setClickable(true);
                int timeInSecs = player.getDuration() / 1000;
                int s = timeInSecs % 60;
                int m = (timeInSecs/60)%60;
                int h = (timeInSecs/60)/60;
                String hh = h < 10 ? "0"+h : ""+h;
                String mm = m < 10 ? "0"+m : ""+m;
                String ss = s < 10 ? "0"+s : ""+s;
                totalDuration = hh + ":"+mm+":"+ss;
                playerTime.setText(totalDuration);
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.release();
                player = null;
                setUpMediaPlayer();
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getApplicationContext(),"Could not play the select file",Toast.LENGTH_LONG).show();
                player.release();
                player = null;
                playerTime.setText("--");
                return false;
            }
        });
    }
}
