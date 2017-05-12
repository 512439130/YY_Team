package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.AllTask;

import java.util.List;

/**
 * Created by yetwish on 2015-05-11
 */

public class TaskMemberRequestAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "TeamMemberRequestAdapter:";
    private Context context;
    private List<AllTask> allTasks;
    private LayoutInflater inflater;



    private TaskMemberRequestCallback mCallback; //注：所有listview的item共用同一个

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     *
     * @author Ivan Xu
     *         2014-11-26
     */
    public interface TaskMemberRequestCallback {
        void agreeClick(View v);  //同意按钮的点击事件
        void refuseClick(View v); //拒绝按钮的点击事件
    }

    public TaskMemberRequestAdapter(Context context, List<AllTask> tasks, TaskMemberRequestCallback callback) {
        this.context = context;
        this.allTasks = tasks;
        this.inflater = LayoutInflater.from(context);
        this.mCallback = callback;


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return allTasks.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return allTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        final AllTask allTask = allTasks.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_task_member_request, null);
            viewholder.user_image = (ImageView) convertView.findViewById(R.id.user_image);

            viewholder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewholder.user_phone = (TextView) convertView.findViewById(R.id.user_phone);
            viewholder.request_reason = (TextView) convertView.findViewById(R.id.request_reason);


            viewholder.btn_agree = (Button) convertView.findViewById(R.id.btn_agree);
            viewholder.btn_refuse = (Button) convertView.findViewById(R.id.btn_refuse);



            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        viewholder.user_image.setImageBitmap(allTask.getUserImage());

        viewholder.user_name.setText(allTask.getUserNickname());
        viewholder.user_phone.setText(allTask.getUserPhone());

        if(allTask.getTaskMemberRequestReason().equals("")){
            allTask.setTaskMemberRequestReason("我想撩你~");
        }
        viewholder.request_reason.setText(allTask.getTaskMemberRequestReason());

        viewholder.btn_agree.setTag(position);
        viewholder.btn_agree.setOnClickListener(this);
        viewholder.btn_refuse.setTag(position);
        viewholder.btn_refuse.setOnClickListener(this);


        return convertView;
    }


    class ViewHolder {
        ImageView user_image;
        TextView user_name;
        TextView user_phone;
        TextView request_reason;
        Button btn_agree;
        Button btn_refuse;

    }

    //响应按钮点击事件,调用子定义接口，并传入View
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_agree:
                mCallback.agreeClick(v);
                break;
            case R.id.btn_refuse:
                mCallback.refuseClick(v);
                break;
        }
    }
}
