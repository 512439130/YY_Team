package com.somust.yyteam.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.PersonBean;

import java.util.List;

/**
 * Created by 13160677911 on 2017-4-11.
 */

public class GroupChatAdapter extends BaseAdapter {
    private Context context;
    private List<PersonBean> persons;
    private LayoutInflater inflater;
    private static final String TAG = "FriendAdapter:";

    public GroupChatAdapter(Context context, List<PersonBean> persons) {
        this.context = context;
        this.persons = persons;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewholder = null;
        final PersonBean person = persons.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_groupchat, null);
            viewholder.tv_tag = (TextView) convertView.findViewById(R.id.tv_lv_item_tag);
            viewholder.tv_name = (TextView) convertView.findViewById(R.id.tv_lv_item_name);
            viewholder.iv_portrait = (ImageView) convertView.findViewById(R.id.iv_lv_item_head);
            viewholder.cb_select = (CheckBox) convertView.findViewById(R.id.id_cb_checkbox);


            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }
        // 获取首字母的assii值
        int selection = person.getFirstPinYin().charAt(0);
        // 通过首字母的assii值来判断是否显示字母
        int positionForSelection = getPositionForSelection(selection);
        if (position == positionForSelection) {// 相等说明需要显示字母
            viewholder.tv_tag.setVisibility(View.VISIBLE);
            viewholder.tv_tag.setText(person.getFirstPinYin());
        } else {
            viewholder.tv_tag.setVisibility(View.GONE);

        }
        viewholder.tv_name.setText(person.getName());
        viewholder.iv_portrait.setImageBitmap(person.getImage());
        return convertView;
    }

    public int getPositionForSelection(int selection) {
        for (int i = 0; i < persons.size(); i++) {
            String Fpinyin = persons.get(i).getFirstPinYin();
            char first = Fpinyin.toUpperCase().charAt(0);
            if (first == selection) {
                return i;
            }
        }
        return -1;
    }

    class ViewHolder {
        TextView tv_tag;
        TextView tv_name;
        ImageView iv_portrait;
        CheckBox cb_select;
    }
}
