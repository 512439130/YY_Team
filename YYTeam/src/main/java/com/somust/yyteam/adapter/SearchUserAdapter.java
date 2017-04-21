package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.somust.yyteam.R;

import com.somust.yyteam.bean.AllUser;
import com.somust.yyteam.bean.User;


import java.util.List;

/**
 * Created by yetwish on 2015-05-11
 */

public class SearchUserAdapter extends BaseAdapter {
    private Context context;
    private List<AllUser> allUsers;
    private LayoutInflater inflater;
    private static final String TAG = "SearchUserAdapter:";

    public SearchUserAdapter(Context context, List<AllUser> users) {
        this.context = context;
        this.allUsers = users;
        this.inflater = LayoutInflater.from(context);



    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return allUsers.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return allUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        final AllUser allUser = allUsers.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_search_user, null);
            viewholder.user_image = (ImageView) convertView.findViewById(R.id.user_image);

            viewholder.user_name = (TextView) convertView.findViewById(R.id.user_name);
            viewholder.user_phone = (TextView) convertView.findViewById(R.id.user_phone);

            viewholder.user_sex = (ImageView) convertView.findViewById(R.id.user_sex);

            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        viewholder.user_image.setImageBitmap(allUser.getUserImage());

        viewholder.user_name.setText(allUser.getUserNickname());
        viewholder.user_phone.setText(allUser.getUserPhone());

        if(allUser.getUserSex().equals("男")){
            viewholder.user_sex.setImageResource(R.mipmap.icon_man);
        }else if(allUser.getUserSex().equals("女")){
            viewholder.user_sex.setImageResource(R.mipmap.icon_woman);
        }



        return convertView;
    }



    class ViewHolder {
        ImageView user_image;
        TextView user_name;
        TextView user_phone;
        ImageView user_sex;
    }
}
