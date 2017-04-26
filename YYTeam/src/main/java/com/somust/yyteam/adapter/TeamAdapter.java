package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMessage;
import com.somust.yyteam.bean.TeamNewsMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 社团新闻
 */
public class TeamAdapter extends BaseAdapter {
    public List<TeamMessage> teamMessages = new ArrayList<>();

    public Context context;
    public LayoutInflater layoutInflater;

    public TeamAdapter(Context context, List<TeamMessage> list) {
        this.context = context;

        this.teamMessages = list;

        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return teamMessages.size();
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
            view = layoutInflater.inflate(R.layout.item_team, null);

            holder.team_image  = (ImageView) view.findViewById(R.id.team_image);

            holder.team_name = (TextView) view.findViewById(R.id.team_name);
            holder.team_type = (ImageView) view.findViewById(R.id.team_type);

            holder.team_president = (TextView) view.findViewById(R.id.team_president);
            holder.team_president_image = (ImageView) view.findViewById(R.id.team_president_image);

            holder.team_time  = (TextView) view.findViewById(R.id.team_time);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }


        holder.team_image.setImageBitmap(teamMessages.get(position).getTeamImage());
        holder.team_name.setText(teamMessages.get(position).getTeamName());


        holder.team_president.setText(teamMessages.get(position).getUserNickname()) ;

        holder.team_president_image.setImageBitmap(teamMessages.get(position).getUserImage());
        holder.team_time.setText(teamMessages.get(position).getTeamTime());

        //根据社团类型的value设置社团类型图片
        String teamType = teamMessages.get(position).getTeamType();
        if (teamType.equals("学习")) {
            holder.team_type.setBackgroundResource(R.mipmap.ic_team_study);
        } else if (teamType.equals("公益")) {
            holder.team_type.setBackgroundResource(R.mipmap.ic_team_welfare);
        } else if (teamType.equals("技术")) {
            holder.team_type.setBackgroundResource(R.mipmap.ic_team_technology);
        }
        return view;
    }

    static class ViewHolder {
        ImageView team_image;  //社团logo


        TextView team_name;  //社团名称
        ImageView team_type;   //社团类型

        TextView team_president; //社团创建人
        ImageView team_president_image;  //社团创建人头像

        TextView team_time;  //社团创建时间

    }



}
