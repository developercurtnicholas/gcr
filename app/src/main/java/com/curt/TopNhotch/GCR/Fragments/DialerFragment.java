package com.curt.TopNhotch.GCR.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.curt.TopNhotch.GCR.SettingsAndConstants.GcrConstants;
import com.curt.TopNhotch.GCR.PermittedOperations.MakeCallOperation;
import com.curt.TopNhotch.GCR.Utilities.DbUtils.DatabaseAsyncBuffer.QuickContactAsyncLookup;
import com.curt.TopNhotch.GCR.Adapters.ContactsSuggestionListAdapter;
import com.curt.TopNhotch.GCR.Models.Contact;
import com.curt.TopNhotch.myapplication.R;
import com.curt.TopNhotch.GCR.CustomComponents.Dialer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.ArrayList;


public class DialerFragment extends Fragment implements Dialer.KeyPressListener,QuickContactAsyncLookup.ResultListener{

    private GridView dialPad;
    private EditText displayArea;
    private Dialer dialer;
    private AdView ad;
    private DialerFragment This;
    private RecyclerView contact_suggestions_list;
    private ContactsSuggestionListAdapter contactsSuggestionListAdapter;
    private ArrayList<Contact> suggestedContacts = new ArrayList<>();
    private QuickContactAsyncLookup quickContactAsyncLookup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        This = this;

        return inflater.inflate(R.layout.dialer_layout,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contact_suggestions_list = (RecyclerView) getActivity().findViewById(R.id.contact_suggestions_list);
        quickContactAsyncLookup = new QuickContactAsyncLookup(1,this,getActivity());
        createAdd();
        initializeDialer();
        disableSoftInputFromAppearing(displayArea);
    }

    private  void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }
    private void initializeDialPadAndDisplayArea(){
        dialPad = (GridView) getActivity().findViewById(R.id.dial_pad);
        displayArea = (EditText) getActivity().findViewById(R.id.displayArea);
    }
    private void initializeRecordCallKey(ArrayList<Dialer.Key> keys){

        keys.add(new Dialer.Key("record", R.layout.dialer_image_key, R.drawable.record, new Dialer.Key.KeyListener() {
            @Override
            public void onKeyPressed(Dialer.Key key) {
                SharedPreferences.Editor p  = getActivity().getSharedPreferences(GcrConstants.DIALER_RECORDING_SETTINGS,Context.MODE_PRIVATE).edit();
                p.putBoolean(GcrConstants.DIALER_SHOULD_RECORD,true);
                p.putBoolean(GcrConstants.DIALER_CALL_FROM_DIALER,true);

                p.commit();

                Dialer d = key.getDialer();
                String phoneNumber = d.getDialedString();

                String finalNumber = "";
                for(char c : phoneNumber.toCharArray()){
                    if(c == '#'){
                        finalNumber += Uri.encode("#");
                    }else{
                        finalNumber += c;
                    }
                }

                if(finalNumber == "" || finalNumber == null){
                    return;
                }

                new MakeCallOperation(getContext(),This,finalNumber).execute();
            }

            @Override
            public void onKeyLongPress(Dialer.Key key) {

            }
        }));
    }
    private void initializeCallKey(ArrayList<Dialer.Key> keys){
        keys.add(new Dialer.Key("call", R.layout.dialer_image_key, R.drawable.regularcall, new Dialer.Key.KeyListener() {
            @Override
            public void onKeyPressed(Dialer.Key key) {

                SharedPreferences.Editor p  = getActivity().getSharedPreferences(GcrConstants.DIALER_RECORDING_SETTINGS,Context.MODE_PRIVATE).edit();
                p.putBoolean(GcrConstants.DIALER_SHOULD_RECORD,false);
                p.putBoolean(GcrConstants.DIALER_CALL_FROM_DIALER,true);
                p.commit();

                Dialer d = key.getDialer();
                String phoneNumber = d.getDialedString();


                String finalNumber = "";
                for(char c : phoneNumber.toCharArray()){
                    if(c == '#'){
                        finalNumber += Uri.encode("#");
                    }else{
                        finalNumber += c;
                    }
                }

                if(finalNumber == "" || finalNumber == null){
                    return;
                }

                new MakeCallOperation(getContext(),This,finalNumber).execute();
            }

            @Override
            public void onKeyLongPress(Dialer.Key key) {

            }
        }));
    }
    private void initializeBackKey(ArrayList<Dialer.Key> keys){
        keys.add(new Dialer.Key("back", R.layout.dialer_image_key, R.drawable.back, new Dialer.Key.KeyListener() {
            @Override
            public void onKeyPressed(Dialer.Key key) {

                Dialer dialer = key.getDialer();
                try{
                    dialer.removeFromDialString();
                }catch (Exception e){

                }
            }

            @Override
            public void onKeyLongPress(Dialer.Key key) {

                key.getDialer().clearDisplayArea();
            }
        }));
    }
    private void initializeDialPadKeys(ArrayList<Dialer.Key> keys){
        keys.add(new Dialer.Key("1",R.layout.dialer_key));
        keys.add(new Dialer.Key("2",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","ABC")));
        keys.add(new Dialer.Key("3",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","DEF")));
        keys.add(new Dialer.Key("4",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","GHI")));
        keys.add(new Dialer.Key("5",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","JKL")));
        keys.add(new Dialer.Key("6",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","MNO")));
        keys.add(new Dialer.Key("7",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","PQRS")));
        keys.add(new Dialer.Key("8",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","TUV")));
        keys.add(new Dialer.Key("9",R.layout.dialer_key,new Dialer.Key.Extras<String,String>().put("letters","WXYZ")));
        keys.add(new Dialer.Key("*",R.layout.dialer_key));
        keys.add(new Dialer.Key("0",R.layout.dialer_key));
        keys.add(new Dialer.Key("#",R.layout.dialer_key));
    }
    private void addKeysDialPadAndDisplayAreaToDialer(ArrayList<Dialer.Key> keys){

        this.dialer = new Dialer(keys, dialPad, displayArea, new Dialer.LayoutModifier() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent, Dialer.Key key) {

                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(key.getLayout(),parent,false);
                try{
                    TextView number = (TextView) v.findViewById(R.id.dial_key);

                    TextView letters = (TextView)v.findViewById(R.id.letters);
                    number.setText(key.getValue());

                    Dialer.Key.Extras<String,String> extras = key.getExtras();
                    if(extras != null){
                        letters.setText(extras.get("letters"));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    ImageView image = (ImageView)v.findViewById(R.id.dial_key);
                    image.setImageResource(key.getDrawableResource());
                }

                return v;
            }
        });
    }
    private void createAdd(){

        MobileAds.initialize(getContext(),"ca-app-pub-3940256099942544~3347511713");
        ad = (AdView)getActivity().findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().addTestDevice("E5C83A969E74CB982DC2FB8927FDEC4B").build();
        ad.loadAd(request);

    }
    private void initializeDialer(){

        ArrayList<Dialer.Key> keys = new ArrayList<>();
        this.initializeDialPadKeys(keys);
        this.initializeRecordCallKey(keys);
        this.initializeBackKey(keys);
        this.initializeCallKey(keys);
        this.initializeDialPadAndDisplayArea();
        this.addKeysDialPadAndDisplayAreaToDialer(keys);
        this.createContactsSuggestionSlider();
        this.dialer.setOnKeyPressListener(this);
    }

    private void createContactsSuggestionSlider(){

        this.contactsSuggestionListAdapter = new ContactsSuggestionListAdapter(this.suggestedContacts,this);
        this.contact_suggestions_list = (RecyclerView)getActivity().findViewById(R.id.contact_suggestions_list);
        this.contact_suggestions_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        this.contact_suggestions_list.setAdapter(contactsSuggestionListAdapter);
    }

    @Override
    public void OnContactsRetrieved(ArrayList<Contact> contacts) {

        this.contactsSuggestionListAdapter.setContacts(contacts);
    }

    @Override
    public void onDialerKeyPressed(Dialer.Key key) {

        String value = key.getValue();
        String dialString = key.getDialer().getDialedString();
        int amount = dialString.length();
        if(value != "record" && value != "call" && amount >= 2){
            quickContactAsyncLookup.addDataToBuffer(dialString);
        }
    }
}
