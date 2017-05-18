package com.somust.yyteam.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.CommunityMessage;
import com.somust.yyteam.bean.TeamNewsMessage;

/**
 * 社团圈
 */
public class CommunityAdapter extends BaseAdapter {
    public List<CommunityMessage> communityMessages;
    public Context context;
    public LayoutInflater layoutInflater;

    public CommunityAdapter(Context context, List<CommunityMessage> communityMessages) {
        this.context = context;
        this.communityMessages = communityMessages;
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return communityMessages.size();
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
            view = layoutInflater.inflate(R.layout.item_community, null);

           // holder.user_background = (ImageView) view.findViewById(R.id.user_background);
            holder.release_image  = (ImageView) view.findViewById(R.id.release_image);
            holder.release_name = (TextView) view.findViewById(R.id.release_name);
            holder.release_content = (TextView) view.findViewById(R.id.release_content);
            holder.release_time = (TextView)view.findViewById(R.id.release_time);
            holder.release_picture  = (ImageView) view.findViewById(R.id.release_picture);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.release_image.setImageBitmap(communityMessages.get(position).getUserImage());
        holder.release_name.setText(communityMessages.get(position).getUserNickname());
        holder.release_content.setText(communityMessages.get(position).getCommunityContent());
        holder.release_time.setText(communityMessages.get(position).getCommunityTime());
        holder.release_picture.setImageBitmap(communityMessages.get(position).getCommunityImage());

        return view;
    }

    static class ViewHolder {
        //ImageView user_background;  //社团圈顶部背景
        ImageView release_image;   //发步人的头像


        TextView release_name;     //发布人的昵称
        TextView release_content;  //发表内容

        ImageView release_picture;  //发表图片

        TextView release_time;    //发表时间

    }

}
