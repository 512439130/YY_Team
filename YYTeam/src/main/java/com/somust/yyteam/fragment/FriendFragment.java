package com.somust.yyteam.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.adapter.FriendsAdapter;

/**
 * Created by 13160677911 on 2016-12-4.
 */

public class FriendFragment extends Fragment {
    //首先查询当前用户id的好友表中的好友id，通过融云及时获取用户信息(昵称，id)

    public static FriendFragment instance = null;//单例模式
    public static FriendFragment getInstance(){
        if (instance == null) {
            instance = new FriendFragment();
        }

        return instance;
    }

    private View mView;
    private ImageView HeadPortrait;  //头像
    private TextView name;  //昵称

    private String[] nicknames = {"杨阳","李冰红","卢桂安","钟良洁","杨阳","李冰红","卢桂安","钟良洁","杨阳","李冰红","卢桂安","钟良洁","杨阳","李冰红","卢桂安","钟良洁"};
    private ListView FriendListView;
    private FriendsAdapter mAdapter;



    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView(inflater);
        initEvent();
        return mView;
    }



    private void initView(LayoutInflater inflater) {
        mView = inflater.inflate(R.layout.friend_fragment, null);

        HeadPortrait = (ImageView) mView.findViewById(R.id.img1);
        name = (TextView) mView.findViewById(R.id.name);
        FriendListView = (ListView) mView.findViewById(R.id.id_lv_friend);

        mAdapter = new FriendsAdapter(getActivity(),nicknames);
        FriendListView.setAdapter(mAdapter);

    }
    private void initEvent() {
        FriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "你点击了第"+(position+1)+"个好友", Toast.LENGTH_SHORT).show();
                //打开个人信息界面（根据position）
            }
        });
    }
}
