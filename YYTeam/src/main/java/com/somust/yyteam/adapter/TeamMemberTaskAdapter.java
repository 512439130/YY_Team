package com.somust.yyteam.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.TeamTaskMessage;
import com.somust.yyteam.view.ImageViewPlus;

import java.util.ArrayList;
import java.util.List;

/**
 * 社团活动
 */
public class TeamMemberTaskAdapter extends BaseAdapter implements View.OnClickListener {
    public List<TeamTaskMessage> teamTaskMessages = new ArrayList<>();

    public Context context;
    public LayoutInflater layoutInflater;
    private TaskMemberRequestCallback mCallback; //注：所有listview的item共用同一个

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     *
     * @author Ivan Xu
     *         2014-11-26
     */
    public interface TaskMemberRequestCallback {
        void runTaskClick(View v); //开始任务的点击事件

        void taskMemberClick(View v);  //活动人员按钮的点击事件

        void addAuditClick(View v); //审核报名按钮的点击事件

        void summaryClick(View v); //活动总结的点击事件
    }

    public TeamMemberTaskAdapter(Context context, List<TeamTaskMessage> list, TaskMemberRequestCallback mCallback) {
        this.context = context;

        this.teamTaskMessages = list;

        layoutInflater = LayoutInflater.from(context);
        this.mCallback = mCallback;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_task_member:
                mCallback.taskMemberClick(v);
                break;
            case R.id.btn_add_audit:
                mCallback.addAuditClick(v);
                break;
            case R.id.btn_summary:
                mCallback.summaryClick(v);
                break;
            case R.id.btn_run:
                mCallback.runTaskClick(v);
                break;

        }
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
            view = layoutInflater.inflate(R.layout.item_team_member_task, null);

            holder.task_id = (TextView) view.findViewById(R.id.task_id);
            holder.team_image = (ImageViewPlus) view.findViewById(R.id.team_image);
            holder.team_task_title = (TextView) view.findViewById(R.id.team_task_title);
            holder.task_number = (TextView) view.findViewById(R.id.task_number);
            holder.team_task_state = (ImageView) view.findViewById(R.id.team_task_state);
            holder.team_task_time = (TextView) view.findViewById(R.id.team_task_time);

            holder.btn_task_member = (Button) view.findViewById(R.id.btn_task_member);
            holder.btn_add_audit = (Button) view.findViewById(R.id.btn_add_audit);
            holder.btn_summary = (Button) view.findViewById(R.id.btn_summary);
            holder.btn_run = (Button) view.findViewById(R.id.btn_run);


            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.task_id.setText(position + 1 + "");
        holder.team_image.setImageBitmap(teamTaskMessages.get(position).getTeamImage());
        holder.team_task_title.setText(teamTaskMessages.get(position).getTaskTitle());
        holder.task_number.setText(teamTaskMessages.get(position).getTaskMaxNumber());
        holder.team_task_time.setText(teamTaskMessages.get(position).getTaskCreateTime());


        //根据社团类型的value设置社团类型图片
        String taskState = teamTaskMessages.get(position).getTaskState();

        if (taskState.equals("正在报名")) {
            holder.btn_run.setEnabled(true);
            holder.btn_add_audit.setEnabled(true);
            holder.btn_summary.setEnabled(false);
            holder.btn_run.setTextColor(Color.WHITE);

            holder.team_task_state.setBackgroundResource(R.mipmap.ic_task_sign);
        } else if (taskState.equals("正在进行")) {
            holder.btn_run.setEnabled(false);
            holder.btn_add_audit.setEnabled(false);
            holder.btn_summary.setEnabled(true);
            holder.btn_run.setTextColor(Color.RED);
            holder.btn_run.setText("任务已开始");

            holder.team_task_state.setBackgroundResource(R.mipmap.ic_task_run);
        } else if (taskState.equals("已结束")) {
            holder.btn_run.setEnabled(false);
            holder.btn_add_audit.setEnabled(false);
            holder.btn_summary.setEnabled(false);
            holder.btn_task_member.setEnabled(false);

            holder.team_task_state.setBackgroundResource(R.mipmap.ic_task_stop);
        }



        holder.btn_task_member.setTag(position);
        holder.btn_task_member.setOnClickListener(this);
        holder.btn_add_audit.setTag(position);
        holder.btn_add_audit.setOnClickListener(this);
        holder.btn_summary.setTag(position);
        holder.btn_summary.setOnClickListener(this);
        holder.btn_run.setTag(position);
        holder.btn_run.setOnClickListener(this);


        return view;
    }

    static class ViewHolder {

        TextView task_id;
        ImageViewPlus team_image;  //社团icon
        TextView team_task_title;  //活动标题
        TextView task_number;    //活动人数（5/50）
        ImageView team_task_state; //活动状态
        TextView team_task_time;  //活动创建时间

        Button btn_task_member;
        Button btn_add_audit;
        Button btn_summary;
        Button btn_run;
    }


}
