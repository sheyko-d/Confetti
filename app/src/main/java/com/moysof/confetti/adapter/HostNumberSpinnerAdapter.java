package com.moysof.confetti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.moysof.confetti.R;
import com.moysof.confetti.util.Util;

public class HostNumberSpinnerAdapter extends ArrayAdapter {

    private Context mContext;

    public HostNumberSpinnerAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewFromResource(convertView, R.layout.item_host_spinner, position);
    }

    static class ViewHolder {
        TextView titleTxt;
        View proTxt;
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
            holder.proTxt = convertView.findViewById(R.id.host_spinner_pro_txt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTxt.setText((position + 1) + "");

        if (holder.proTxt != null) {
            if (/*TODO: If PRO*/position > 2) {
                holder.proTxt.setVisibility(View.VISIBLE);
                holder.titleTxt.setAlpha(0.5f);
            } else {
                holder.proTxt.setVisibility(View.GONE);
                holder.titleTxt.setAlpha(1f);
            }
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewFromResource(convertView, R.layout.item_host_spinner_number_dropdown, position);
    }

    @Override
    public int getCount() {
        return Util.MAX_NUMBER;
    }
}