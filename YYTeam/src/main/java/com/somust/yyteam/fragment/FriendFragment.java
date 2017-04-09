package com.somust.yyteam.fragment;

import android.content.Intent;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.HomeActivity;
import com.somust.yyteam.adapter.FriendAdapter;
import com.somust.yyteam.bean.PersonBean;
import com.somust.yyteam.bean.TeamFriend;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.SideBar.PinyinComparator;
import com.somust.yyteam.utils.SideBar.PinyinUtils;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.SideBar;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by 13160677911 on 2016-12-4.
 */

public class FriendFragment extends Fragment {
    //首先查询当前用户id的好友表中的好友id，通过融云及时获取用户信息(昵称，id)

    public static FriendFragment instance = null;//单例模式

    public static FriendFragment getInstance() {
        if (instance == null) {
            instance = new FriendFragment();
        }
        return instance;
    }

    private View mView;
    private ImageView HeadPortrait;  //头像
    private TextView name;  //昵称

    private ListView FriendListView;


    //SideBar相关
    private List<PersonBean> data;
    private SideBar sidebar;
    private TextView dialog;
    private FriendAdapter friendAdapter;

    private static final String TAG = "FriendFragment:";

    private User user;    //登录用户信息
    private List<TeamFriend> friendlist;   //登录用户的好友信息


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        //获取好友列表
        obtainFriendList(user.getUserPhone());
        initView(inflater);
        initEvent();







        return mView;
    }

    /**
     * 获取好友列表
     *
     * @param phone 手机号
     */
    private void obtainFriendList(String phone) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.friendUrl + ConstantUrl.friend_interface)
                .addParams("user_id", phone)
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {

        }

        @Override
        public void onAfter(int id) {

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            //dialog.cancel();
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(getActivity(), "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("")) {
                //dialog.cancel();
                T.testShowShort(getActivity(), "无好友");
            } else {

                T.testShowShort(getActivity(), "好友获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                friendlist = gson.fromJson(response, new TypeToken<List<TeamFriend>>() {
                }.getType());


                L.v(TAG, "第1个好友信息" + friendlist.get(0));
                L.v(TAG, "第2个好友信息" + friendlist.get(1));
            }

        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
        }

    }

    private void initView(LayoutInflater inflater) {
        mView = inflater.inflate(R.layout.friend_fragment, null);

        HeadPortrait = (ImageView) mView.findViewById(R.id.img1);
        name = (TextView) mView.findViewById(R.id.name);


        FriendListView = (ListView) mView.findViewById(R.id.id_lv_friend);


        //insert
        initSidebar();

        friendAdapter = new FriendAdapter(getActivity(), data);
        FriendListView.setAdapter(friendAdapter);

    }

    private void initEvent() {
        FriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "你点击了第" + (position + 1) + "个好友", Toast.LENGTH_SHORT).show();
                //打开个人信息界面（根据position）
            }
        });
    }


    private void initSidebar() {
        //insert
        sidebar = (SideBar) mView.findViewById(R.id.sidebar);
        dialog = (TextView) mView.findViewById(R.id.dialog);
        sidebar.setTextView(dialog);

        // 设置字母导航触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = friendAdapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    FriendListView.setSelection(position);
                }
            }
        });
        data = getData(getResources().getStringArray(R.array.listpersons));  //模拟数据
        /*String[] name = new String[friendlist.size()];
        for (int i = 0; i < friendlist.size();i++){
            name[i] = friendlist.get(i).getFriendRemark();
        }

        data = getData(name);  //真实数据*/
        // 数据在放在adapter之前需要排序
        Collections.sort(data, new PinyinComparator());


    }


    /**
     * 模拟数据获取（通过资源文件）
     *
     * @param data
     * @return
     */
    private List<PersonBean> getData(String[] data) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < data.length; i++) {
            String pinyin = PinyinUtils.getPingYin(data[i]);
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(data[i]);
            person.setPinYin(pinyin);
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setFirstPinYin(Fpinyin);
            } else {
                person.setFirstPinYin("#");
            }

            listarray.add(person);
        }
        return listarray;

    }
}
