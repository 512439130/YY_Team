package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.TeamNewsMessage;
import com.somust.yyteam.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 社团新闻
 */
public class TeamNewsAdapter extends BaseAdapter {
    public List<TeamNewsMessage> teamNewsMessages = new ArrayList<>();

    public Context context;
    public LayoutInflater layoutInflater;

    public TeamNewsAdapter(Context context, List<TeamNewsMessage> list) {
        this.context = context;

        this.teamNewsMessages = list;

        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return teamNewsMessages.size();
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
            view = layoutInflater.inflate(R.layout.item_teamnews, null);

            holder.news_title = (TextView) view.findViewById(R.id.news_title);
            holder.news_content = (TextView) view.findViewById(R.id.news_content);
            holder.news_image = (ImageView) view.findViewById(R.id.team_news_image);
            holder.news_time  = (TextView) view.findViewById(R.id.news_time);

            holder.team_name = (TextView) view.findViewById(R.id.team_name);
            holder.team_image  = (ImageView) view.findViewById(R.id.team_image);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.news_title.setText(teamNewsMessages.get(position).getNewsTitle());
        holder.news_content.setText(teamNewsMessages.get(position).getNewsContent());

        holder.news_image.setImageBitmap(teamNewsMessages.get(position).getNewsImage());
        holder.news_time.setText(teamNewsMessages.get(position).getNewsTime()) ;

        holder.team_name.setText(teamNewsMessages.get(position).getTeamName());
        holder.team_image.setImageBitmap(teamNewsMessages.get(position).getTeamImage());



        return view;
    }

    static class ViewHolder {

        TextView news_title;     //新闻标题
        TextView news_content;  //新闻内容
        ImageView news_image;  //新闻图片

        ImageView team_image;   //社团头像
        TextView team_name;  //社团名称

        TextView news_time;    //发表时间

    }



}
