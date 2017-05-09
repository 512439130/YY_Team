package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamMemberMessage;
import com.somust.yyteam.bean.TeamMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的大学社团
 */
public class TeamListAdapter extends BaseAdapter {
    public List<TeamMemberMessage> teamMemberMessages = new ArrayList<>();

    public Context context;
    public LayoutInflater layoutInflater;

    public TeamListAdapter(Context context, List<TeamMemberMessage> list) {
        this.context = context;

        this.teamMemberMessages = list;

        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return teamMemberMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.item_team_list, null);
            holder.team_image  = (ImageView) view.findViewById(R.id.team_image);
            holder.team_name = (TextView) view.findViewById(R.id.team_name);
            holder.team_position = (TextView) view.findViewById(R.id.team_position);
            holder.team_join_time  = (TextView) view.findViewById(R.id.team_join_time);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.team_image.setImageBitmap(teamMemberMessages.get(position).getTeamImage());
        holder.team_name.setText(teamMemberMessages.get(position).getTeamId().getTeamName());
        holder.team_position.setText(teamMemberMessages.get(position).getTeamMemberPosition()) ;
        holder.team_join_time.setText(teamMemberMessages.get(position).getTeamMemberJoinTime());

        return view;
    }

    static class ViewHolder {
        ImageView team_image;  //社团logo


        TextView team_name;  //社团名称

        TextView team_position; //当前职责

        TextView team_join_time;  //加入时间

    }



}
