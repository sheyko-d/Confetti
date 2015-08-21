package com.moysof.confetti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moysof.confetti.R;
import com.moysof.confetti.util.Util;

public class HostTimeSpinnerAdapter extends ArrayAdapter {

    private int[] mTimeArray;
    private Context mContext;

    public HostTimeSpinnerAdapter(Context context, int[] timeArray) {
        super(context, 0);
        mContext = context;
        mTimeArray = timeArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewFromResource(convertView, R.layout.item_host_spinner, position);
    }

    static class ViewHolder {
        TextView titleTxt;
    }

    private View getViewFromResource(View convertView, int res, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(res, null);
            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.titleTxt = (TextView) convertView.findViewById(R.id.host_spinner_title_txt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTxt.setText(Util.formatTime(mTimeArray[position]));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewFromResource(convertView, R.layout.item_host_spinner_time_dropdown, position);
    }

    @Override
    public int getCount() {
        return mTimeArray.length;
    }
}