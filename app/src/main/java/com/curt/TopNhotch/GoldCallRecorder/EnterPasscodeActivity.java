package com.curt.TopNhotch.GoldCallRecorder;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.curt.TopNhotch.myapplication.R;

/**
 * Created by Kurt on 12/20/2016.
 */
public class EnterPasscodeActivity extends AppCompatActivity{

    private EditText passCodeEditText;
    private GridView passCodeNumberGrid;
    private String passCodeString = "";
    private TextView forgotPin;
    private ImageView lockImage;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_pass_code);

        final String passcode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
                getString(GcrConstants.SECURITY_PASSWORD,"----");
        lockImage = (ImageView)findViewById(R.id.pass_code_pad_lock_img);
        passCodeNumberGrid = (GridView)findViewById(R.id.pas_code_dial_grid);
        passCodeEditText = (EditText)findViewById(R.id.pass_code_edit_text);
        passCodeEditText.setKeyListener(null);
        passCodeEditText.setTextColor(getResources().getColor(R.color.actualsandstorm));
        forgotPin = (TextView)findViewById(R.id.forgot_pin);
        forgotPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EnterPasscodeActivity.this);
                builder.setMessage(getResources().getText(R.string.forgot_pass));
                AlertDialog d = builder.create();
                d.show();
            }
        });

        GridContent[] contents = {
                new GridContent("Play",R.drawable.num_one_state_list),
                new GridContent("Call",R.drawable.number_two_state_list),
                new GridContent("ahss",R.drawable.num_three_state_list),
                new GridContent("ahss",R.drawable.num_four_state_list),
                new GridContent("ahss",R.drawable.num_five_state_list),
                new GridContent("ahss",R.drawable.num_six_state_list),
                new GridContent("ahss",R.drawable.num_seven_state_list),
                new GridContent("ahss",R.drawable.num_eight_state_list),
                new GridContent("ahss",R.drawable.num_nine_state_list),
                new GridContent("ahss",R.drawable.backspace_state_list),
                new GridContent("ahss",R.drawable.num_zero_state_list),
                new GridContent("ahss",R.drawable.forward_state_list)

        };
        OptionsAdapter adapter = new PassGridOptionsAdapter(getApplicationContext(),contents);
        passCodeNumberGrid.setAdapter(adapter);

        passCodeNumberGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 9) {
                    passCodeString = "";
                    passCodeEditText.setText(passCodeString);
                }
                return false;
            }
        });
        passCodeNumberGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                ObjectAnimator on = ObjectAnimator.ofFloat(view,"alpha",0f,1f);
                on.setDuration(300);
                on.start();

                switch (position) {
                    case 0: {
                        //1
                        passCodeString = passCodeString + "1";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 1: {
                        //2
                        passCodeString = passCodeString + "2";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 2: {
                        //3
                        passCodeString = passCodeString + "3";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 3: {
                        //4
                        passCodeString = passCodeString + "4";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 4: {
                        //5
                        passCodeString = passCodeString + "5";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 5: {
                        //6
                        passCodeString = passCodeString + "6";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 6: {
                        //7
                        passCodeString = passCodeString + "7";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 7: {
                        //8
                        passCodeString = passCodeString + "8";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 8: {
                        //9
                        passCodeString = passCodeString + "9";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 9: {
                        //backsapce
                        if (passCodeString != null && passCodeString.length() > 0) {
                            passCodeString = passCodeString.substring(0, passCodeString.length() - 1);
                            passCodeEditText.setText(passCodeString);
                        }
                        break;
                    }
                    case 10: {
                        //zero
                        passCodeString = passCodeString + "0";
                        passCodeEditText.setText(passCodeString);
                        break;
                    }
                    case 11: {
                        //forward
                        if(passCodeString.equals(passcode)){
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("passcode_correct",true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else{
                            ObjectAnimator oa = ObjectAnimator.ofFloat(lockImage,"alpha",0f,1f);
                            oa.setDuration(1000);
                            oa.start();
                        }
                        break;
                    }
                }
            }
        });
    }
}
