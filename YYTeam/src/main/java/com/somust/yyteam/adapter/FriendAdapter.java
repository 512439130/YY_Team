package com.somust.yyteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.PersonBean;

import java.util.List;

/**
 * Created by 13160677911 on 2017-4-9.
 */

public class FriendAdapter extends BaseAdapter {
    private Context context;
    private List<PersonBean> persons;
    private LayoutInflater inflater;

    public FriendAdapter(Context context, List<PersonBean> persons) {
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
        PersonBean person = persons.get(position);
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_friend, null);
            viewholder.tv_tag = (TextView) convertView.findViewById(R.id.tv_lv_item_tag);
            viewholder.tv_name = (TextView) convertView.findViewById(R.id.tv_lv_item_name);
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
    }

}
