package com.moysof.whattheblank.adapter;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moysof.whattheblank.HostLobbyActivity;
import com.moysof.whattheblank.R;

public class HostTeamsAdapter extends
        RecyclerView.Adapter<HostTeamsAdapter.TeamsHolder> {

    private Context mContext;
    private SortedList<Team> teams;

    public HostTeamsAdapter(Context context, SortedList<Team> teams) {
        mContext = context;
        this.teams = teams;
    }

    public static class Team {

        public Integer number;
        public Integer assignedCount;
        public Integer color;

        public Team(Integer number, Integer assignedCount, Integer color) {
            this.number = number;
            this.assignedCount = assignedCount;
            this.color = color;
        }

        public Integer getNumber() {
            return number;
        }

        public Integer getAssignedCount() {
            return assignedCount;
        }

        public Integer getColor() {
            return color;
        }

    }

    public class TeamsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView titleTxt;
        public TextView countTxt;
        public View colorView;

        public TeamsHolder(View v) {
            super(v);
            titleTxt = (TextView) v.findViewById(R.id.host_team_title_txt);
            countTxt = (TextView) v.findViewById(R.id.host_team_count_txt);
            colorView = v.findViewById(R.id.host_team_color_view);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            joinClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TeamsHolder onCreateViewHolder(ViewGroup parent,
                                          int viewType) {
        return new TeamsHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_host_team, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TeamsHolder holder, int position) {
        Team team = teams.get(position);

        holder.titleTxt.setText("Team " + team.getNumber());
        holder.countTxt.setText(team.getAssignedCount() + " of "
                + HostLobbyActivity.sTotalPlayersCount + " players assigned");
        holder.colorView.setBackgroundColor(team.getColor());
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    OnItemClickListener joinClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
        }

    };
}