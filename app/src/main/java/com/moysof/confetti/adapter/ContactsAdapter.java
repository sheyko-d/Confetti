package com.moysof.confetti.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moysof.confetti.R;

import java.util.ArrayList;

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.Holder> {

    private Context mContext;
    private ArrayList<Contact> mContacts;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts) {
        mContacts = contacts;
        mContext = context;
    }

    public static class Contact {

        public String name;
        public String phone;

        public Contact(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView nameTxt;
        public TextView phoneTxt;
        public ImageButton addBtn;

        public Holder(View v) {
            super(v);
            nameTxt = (TextView) v.findViewById(R.id.contacts_name_txt);
            phoneTxt = (TextView) v.findViewById(R.id.contacts_phone_txt);
            addBtn = (ImageButton) v.findViewById(R.id.contacts_add_btn);

            addBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            inviteClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Holder onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(
                R.layout.item_contacts, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Contact contact = mContacts.get(position);

        holder.nameTxt.setText(contact.getName());
        holder.phoneTxt.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    OnItemClickListener inviteClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", mContacts.get(position).getPhone());
            smsIntent.putExtra("sms_body", "Hey " + mContacts.get(position).getName() + ", let's " +
                    "play Confetti?\n\nhttp://moyersoftware.com/blank/play.php");
            mContext.startActivity(smsIntent);
        }

    };

}