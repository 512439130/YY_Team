package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.somust.yyteam.R;


/**
 * Created by Bob on 15/8/21.
 */
public class FriendsAdapter extends BaseAdapter {

    private String[] mNameArray;
    private LayoutInflater mLayoutInflater;


    public FriendsAdapter(Context context, String[] names) {
        mNameArray = names;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mNameArray == null)
            return 0;
        return mNameArray.length;
    }

    @Override
    public Object getItem(int position) {
        if (mNameArray == null || mNameArray.length >= position)
            return null;
        return mNameArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null || convertView.getTag() == null) {

            convertView = mLayoutInflater.inflate(R.layout.item_friend, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.id_tv_nickname);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleTextView.setText(mNameArray[position]);
        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;
    }
}
