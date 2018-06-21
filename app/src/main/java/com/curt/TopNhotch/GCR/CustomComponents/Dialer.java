package com.curt.TopNhotch.GCR.CustomComponents;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.curt.TopNhotch.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Dialer {

    private ArrayList<Key> keys;
    private HashMap<String,Key> key;
    private GridView dialGrid;
    private EditText displayArea;
    private DialerAdapter adapter;
    private LayoutModifier modifier;
    private ArrayList<String> dialedString = new ArrayList<>();
    private KeyPressListener keyPressListener;

    /**
     * Keys: A list of keys that the dialer will have, keys can be defined using Dialer.Key<br>
     * gridview: A gridView that the keys will be displayed in, default is a 3 column grid<br>
     * displayarea: An edittext that will display the value for a key that does not have a keylistener defined<br>
     * layoutmodifier: A callback that will be used to change the layout to your suiting, the get view method
     * of the layout modifier is called in the getview method of the dialers adapter*/
    public Dialer(ArrayList<Key> keys, GridView gridView, EditText displayArea, LayoutModifier modifier){

        if(gridView == null){
            throw new NullPointerException("The grid view you passed in is null");
        }else{
            this.keys = keys;
            this.modifier = modifier;
            dialGrid = gridView;
            this.displayArea = displayArea;
            dialGrid.setNumColumns(3);
            this.adapter = new DialerAdapter(keys,modifier);
            dialGrid.setAdapter(adapter);
            this.adapter.notifyDataSetChanged();
            setDialerForKeys();
        }
    }


    public void setOnKeyPressListener(KeyPressListener listener){

        this.keyPressListener = listener;
    }

    private void setDialerForKeys(){

        for(Key k : keys){
            k.setDialer(this);
        }
    }

    public void addToDialString(String value){
        this.dialedString.add(value);
        this.displayArea.setText(this.getDialedString());
    }
    public void removeFromDialString(String s){
        this.dialedString.remove(s);
        this.displayArea.setText(this.getDialedString());
    }

    /**
     * Removes one value from the display area*/
    public void removeFromDialString(){
        this.dialedString.remove(getDialedString().length()-1);
        this.displayArea.setText(this.getDialedString());
    }
    /**
     * Clears the display area*/
    public void clearDisplayArea(){
        this.dialedString.removeAll(dialedString);
        this.displayArea.setText("");
    }
    public String getDialedString(){
        String dialed = "";
        for(String s : dialedString ){
            dialed += s;
        }
        return dialed;
    }

    public interface LayoutModifier{
        View getView(int position, View convertView, ViewGroup parent,Key key);
    }

    public interface KeyPressListener{

        public void onDialerKeyPressed(Key key);
    }

    private static class DialerAdapter extends BaseAdapter{

        private ArrayList<Key> keys;
        private LayoutModifier modifier;

        public DialerAdapter(ArrayList<Key> keys,LayoutModifier modifier){
            this.keys = keys;
            this.modifier = modifier;
        }

        @Override
        public int getCount() {
            return keys.size();
        }

        @Override
        public Object getItem(int position) {
            return keys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){

            convertView = modifier.getView(position,convertView,parent,keys.get(position));

            if(keys.get(position).listener == null){

                setDefaultListeners(convertView,position);
            }else{

                convertView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        keys.get(position).onKeyLongPress(v.getContext(),v);
                        return false;
                    }
                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        keys.get(position).onKeyPressed(v.getContext(),v);
                    }
                });
            }


            return convertView;
        }

        /**
         * If no key listener is specified for the key then we set these*/
        protected void setDefaultListeners(View convertView,final int position){
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()){

                        case MotionEvent.ACTION_CANCEL:{
                            keys.get(position).onKeyReleased(v.getContext(),v);
                            return false;
                        }
                        case MotionEvent.ACTION_UP:{
                            keys.get(position).onKeyReleased(v.getContext(),v);
                            return false;
                        }
                        case MotionEvent.ACTION_DOWN:{

                            keys.get(position).onKeyPressed(v.getContext(),v);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public static class Key {

        private String value;
        private KeyListener listener;
        private int layout = -1;
        private Extras extras = null;
        private int drawableResource = -1;
        private int soundResource = -1;
        private Dialer dialer;

        /**
         * value: the value that will be shown on the dial pad if the key is pressed
         * layoutresource: the layout resource that will be rendered for the key
         *
         *
         * <br><br>NOTE: No listener supplied to this method therefore the default key press is used which will add the value
         * of the key to the display area and highlight the key yellow*/
        public Key(String value,int layoutResource){
            this.value = value;
            this.layout = layoutResource;
        }
        /**
         * value: the value that will be shown on the dial pad if the key is pressed
         * layoutresource: the layout resource that will be rendered for the key
         * extras: anything extra in the form of ket value pairs
         *
         * NOTE: No listener supplied to this method therefore the default key press is used which will add the value
         * of the key to the display area and highlight the key yellow*/
        public Key(String value,int layoutResource,Extras extras){
            this.value = value;
            this.layout = layoutResource;
            this.extras = extras;
        }

        /**
         * value: the value that will be shown on the dial pad if the key is pressed
         * layoutresource: the layout resource that will be rendered for the key
         * drawableresource: the resource that will be displayed as an image in the layoutresource
         * listener: the listener to listen out for press and long press on the key*/
        public Key(String value,int layoutResource,int drawableResource,KeyListener listener){
            this.value = value;
            this.layout = layoutResource;
            this.drawableResource = drawableResource;
            this.listener = listener;
        }

        public Extras getExtras(){
            return this.extras;
        }

        public void setDialer(Dialer dialer){
            this.dialer = dialer;
        }

        public Dialer getDialer(){
            return this.dialer;
        }

        public interface KeyListener{

            void onKeyPressed(Key key);
            void onKeyLongPress(Key key);
        }

        public static class Extras<K,V>{

            private HashMap<K,V> extras;

            public <K,V> Extras(){

                this.extras = new HashMap<>();
            }

            public Extras<K,V> put(K key,V value){

                this.extras.put(key,value);
                return this;
            }

            public V get(K key){

                return extras.get(key);
            }
        }

        protected void onKeyReleased(Context context,View view){

                view.setBackgroundColor(Color.WHITE);
        }

        protected void onKeyLongPress(Context context,View view){

            try{
                this.listener.onKeyLongPress(this);
            }catch (Exception e){

            }
        }

        protected void onKeyPressed(Context context,View view){

            if(this.listener == null){
                view.setBackgroundColor(view.getResources().getColor(R.color.actualsandstorm));
                this.dialer.addToDialString(this.value);
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1);
            }else{
                //Listener is not null do custom action
                this.listener.onKeyPressed(this);
            }

            if(this.getDialer().keyPressListener != null){
                this.getDialer().keyPressListener.onDialerKeyPressed(this);
            }
        }


        /**
         * Gets the value supplied to the keys constructor
         * */
        public String getValue(){
            return this.value;
        }
        public int getLayout(){
            return this.layout;
        }
        public int getDrawableResource(){
            return this.drawableResource;
        }
    }
}
