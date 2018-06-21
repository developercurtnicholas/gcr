package com.curt.TopNhotch.GCR.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.curt.TopNhotch.GCR.PermittedOperations.MakeCallOperation;
import com.curt.TopNhotch.GCR.Utilities.Factories.DrawableFactory;
import com.curt.TopNhotch.GCR.Models.Contact;
import com.curt.TopNhotch.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Kurt on 2/4/2018.
 */

public class ContactsSuggestionListAdapter extends RecyclerView.Adapter {

    private ArrayList<Contact> contacts;
    private Context context;



    private Fragment fragment;
    public ContactsSuggestionListAdapter(ArrayList<Contact> contacts, Fragment fragment){
        this.contacts = contacts;
        this.context = fragment.getActivity().getApplicationContext();
        this.fragment = fragment;
    }

    public void setContacts(ArrayList<Contact> contacts){

        this.contacts = contacts;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View parentView;
        public TextView photo;
        public TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            this.parentView = itemView;
            this.photo = (TextView) parentView.findViewById(R.id.contact_suggestion_photo);
            this.name = (TextView)parentView.findViewById(R.id.contact_suggestion_name);
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_suggestion_list_layout,parent,false);
        ViewHolder holder = new ViewHolder(layout);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        ViewHolder viewHolder = (ViewHolder)holder;
        final Contact contact = contacts.get(position);
       // contact.setContactPhotoLetter(viewHolder.photo,"");
        if(contact != null){

            try{
                createContactImage(contact,viewHolder.photo);
            }catch (Exception e){
                contact.setPhoto(context,viewHolder.photo);
                //viewHolder.photo.setBackground(null);
            }
            if(contact.getName() != null){

                viewHolder.name.setText(contact.getName());
            }
        }

        viewHolder.photo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new MakeCallOperation(context,fragment,contact.getNumber()).execute();
                return false;
            }
        });

    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.photo.setText("");
    }

    private void createContactImage(Contact contact, TextView photo) throws Exception{

        RoundedBitmapDrawable contactImage = DrawableFactory.createRoundImageFromUri(context,contact.contactsPhotoUri);
        photo.setBackground(contactImage);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
