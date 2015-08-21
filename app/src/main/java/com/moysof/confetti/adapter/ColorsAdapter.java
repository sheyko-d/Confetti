package com.moysof.confetti.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moysof.confetti.R;

import java.util.ArrayList;

public class ColorsAdapter extends
        RecyclerView.Adapter<ColorsAdapter.Holder> {

    private Context mContext;
    private String[] mColors;
    private int mSelectedPos = -1;
    private ArrayList<String> mOtherTeamColors;

    public ColorsAdapter(Context context,  ArrayList<String> colors,
                         ArrayList<String> otherTeamColors) {
        mContext = context;
        mColors = colors.toArray(new String[colors.size()]);
        mOtherTeamColors = otherTeamColors;
    }

    public ColorsAdapter(Context context, ArrayList<String> otherTeamColors) {
        mContext = context;
        mColors = context.getResources().getStringArray(R.array.colors);
        mOtherTeamColors = otherTeamColors;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View checkbox;
        public View colorBg;
        public View blockedImg;

        public Holder(View v) {
            super(v);
            colorBg = v.findViewById(R.id.color_bg_layout);
            checkbox = v.findViewById(R.id.color_checkbox_img);
            blockedImg = v.findViewById(R.id.color_blocked_img);

            colorBg.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            pickClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public Holder onCreateViewHolder(ViewGroup parent,
                                     int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_color, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.colorBg.setBackgroundColor(Color.parseColor("#" + mColors[position]));

        if (position == mSelectedPos) {
            holder.colorBg.setEnabled(false);
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.blockedImg.setVisibility(View.GONE);
        } else {
            holder.checkbox.setVisibility(View.GONE);

            if (mOtherTeamColors.contains(mColors[position])){
                holder.colorBg.setEnabled(false);
                holder.blockedImg.setVisibility(View.VISIBLE);
            } else {
                holder.colorBg.setEnabled(true);
                holder.blockedImg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mColors.length;
    }

    OnItemClickListener pickClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            if (mSelectedPos != -1) {
                int oldSelectedPos = mSelectedPos;
                mSelectedPos = position;
                notifyItemChanged(oldSelectedPos);
                notifyItemChanged(mSelectedPos);
            } else {
                mSelectedPos = position;
                notifyItemChanged(mSelectedPos);
            }
        }

    };

    public void setTeamColor(String color) {
        int colorsCount = getItemCount();
        for (int i = 0; i < colorsCount; i++) {
            if (color.equals(mColors[i])) {
                mSelectedPos = i;
                return;
            }
        }
    }

    public String getTeamColor() {
        return mColors[mSelectedPos];
    }
}