package com.somust.yyteam.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.TeamMessage;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.bean.TeamTaskMessage;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.view.ImageViewPlus;

import java.util.ArrayList;
import java.util.List;

/**
 * 社团活动
 */
public class TeamTaskAdapter extends BaseAdapter {
    public List<TeamTaskMessage> teamTaskMessages = new ArrayList<>();

    public Context context;
    public LayoutInflater layoutInflater;

    public TeamTaskAdapter(Context context, List<TeamTaskMessage> list) {
        this.context = context;

        this.teamTaskMessages = list;

        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return teamTaskMessages.size();
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
            view = layoutInflater.inflate(R.layout.item_team_task, null);

            holder.team_image = (ImageViewPlus) view.findViewById(R.id.team_image);
            holder.team_name = (TextView) view.findViewById(R.id.team_name);

            holder.team_task_title = (TextView) view.findViewById(R.id.team_task_title);
            holder.task_number = (TextView) view.findViewById(R.id.task_number);
            holder.team_task_state = (ImageView) view.findViewById(R.id.team_task_state);
            holder.team_task_time = (TextView) view.findViewById(R.id.team_task_time);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }


        holder.team_image.setImageBitmap(teamTaskMessages.get(position).getTeamImage());
        holder.team_name.setText(teamTaskMessages.get(position).getTeamName());
        holder.team_task_title.setText(teamTaskMessages.get(position).getTaskTitle());
        holder.task_number.setText(teamTaskMessages.get(position).getTaskMaxNumber());
        holder.team_task_time.setText(teamTaskMessages.get(position).getTaskCreateTime());


        //根据社团类型的value设置社团类型图片
        String taskState = teamTaskMessages.get(position).getTaskState();


        if (taskState.equals("正在报名")) {
            holder.team_task_state.setBackgroundResource(R.mipmap.ic_task_sign);
        } else if (taskState.equals("正在进行")) {
            holder.team_task_state.setBackgroundResource(R.mipmap.ic_task_run);
        } else if (taskState.equals("已结束")) {
            holder.team_task_state.setBackgroundResource(R.mipmap.ic_task_stop);
        }




        return view;
    }

    static class ViewHolder {


        ImageViewPlus team_image;  //社团icon
        TextView team_name;      //社团名称
        TextView team_task_title;  //活动标题
        TextView task_number;    //活动人数（5/50）
        ImageView team_task_state; //活动状态
        TextView team_task_time;  //活动创建时间
    }


}
