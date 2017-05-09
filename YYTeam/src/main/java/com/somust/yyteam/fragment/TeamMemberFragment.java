package com.somust.yyteam.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.PersionInformationActivity;
import com.somust.yyteam.activity.SearchUserActivity;
import com.somust.yyteam.adapter.FriendAdapter;
import com.somust.yyteam.adapter.TeamMemberAdapter;
import com.somust.yyteam.bean.PersonBean;
import com.somust.yyteam.bean.TeamFriend;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.SideBar.PinyinComparator;
import com.somust.yyteam.utils.SideBar.PinyinUtils;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.SideBar;
import com.somust.yyteam.view.refreshview.RefreshLayout;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * Created by 13160677911 on 2016-12-4.
 * 社团成员列表
 */

public class TeamMemberFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    //首先查询当前用户id的好友表中的好友id，通过融云及时获取用户信息(昵称，id)

    public static TeamMemberFragment instance = null;//单例模式

    public static TeamMemberFragment getInstance() {
        if (instance == null) {
            instance = new TeamMemberFragment();
        }
        return instance;
    }

    private View mView;

    private ListView friendListView;


    //SideBar相关
    private List<PersonBean> personBeenList;
    private SideBar sidebar;
    private TextView dialogTextView;
    private TeamMemberAdapter teamMemberAdapter;

    private static final String TAG = "TeamMemberFragment:";

    // private User user;    //登录用户信息
    private List<TeamMember> teamMembers;   //登录用户的好友信息
    private Bitmap[] portraitBitmaps;



    private Button addButton;  //添加好友按钮


    private RefreshLayout swipeLayout;
    private View header;

    // private List<User> allUser;
    private User user;    //登录用户信息

    private TeamMember teamMember;
    private Integer teamId = 0;

    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team_member, null);
        initView();
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        teamMember = (TeamMember) intent.getSerializableExtra("teamMember");
        teamId = teamMember.getTeamId().getTeamId();


        //发送网络请求，获取社团成员列表
        obtainTeamMemberList(teamId.toString());
        initListener();

        return mView;
    }

    private void initView() {


        sidebar = (SideBar) mView.findViewById(R.id.sidebar);
        dialogTextView = (TextView) mView.findViewById(R.id.dialog);
        sidebar.setTextView(dialogTextView);
        addButton = (Button) mView.findViewById(R.id.id_add_friend);


        friendListView = (ListView) mView.findViewById(R.id.id_lv_friend);
        //头部布局
        header = getActivity().getLayoutInflater().inflate(R.layout.team_member_header, null);
        friendListView.addHeaderView(header);


        swipeLayout = (RefreshLayout) mView.findViewById(R.id.swipe_container);

        swipeLayout.setColorSchemeResources( android.R.color.holo_red_light, android.R.color.holo_orange_dark,android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景

    }

    /**
     * 获取好友列表
     *
     * @param teamId 手机号
     */
    private void obtainTeamMemberList(String teamId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamMemberUrl + ConstantUrl.obtainTeamMember_interface)
                .addParams("teamId", teamId)
                .build()
                .execute(new MyTeamMemberCallback());

    }


    /**
     * 回调
     */
    public class MyTeamMemberCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(getActivity(), "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {

                T.testShowShort(getActivity(), "您当前无社团成员");

            } else {
                T.testShowShort(getActivity(), "社团成员获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new GsonBuilder().setDateFormat(Constant.formatType).create();
                teamMembers = gson.fromJson(response, new TypeToken<List<TeamMember>>() {
                }.getType());


                portraitBitmaps = new Bitmap[teamMembers.size()];
                for (int i = 0; i < teamMembers.size(); i++) {
                    obtainImage(teamMembers.get(i).getUserId().getUserImage(), i);
                }
            }
        }
    }

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     * @param i   需要保存在bitmaps的对应位置
     */
    public void obtainImage(String url, final int i) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        L.v("TAG", "onResponse：complete");
                        portraitBitmaps[i] = bitmap;
                        //网络请求成功后
                        initSidebarAndView();

                    }
                });
    }


    private void initListener() {
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //由于字幕排序引起item不对应list(不能使用排序前的friendlist，必须使用排序后的personBeanList)
                String userId = personBeenList.get(position - 1).getPhone();
                String userNickname = personBeenList.get(position - 1).getName();
                L.e(TAG, userId);
                L.e(TAG, userNickname);
                //打开个人信息界面（个人信息界面包含发送消息）
                Intent intent = new Intent(getActivity(), PersionInformationActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userNickname", userNickname);
                intent.putExtra("openState", "friend");  //好友
                startActivity(intent);

            }
        });


        //设置刷新监听
        swipeLayout.setOnRefreshListener(this);



    }


    private void initSidebarAndView() {
        // 设置字母导航触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = teamMemberAdapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    friendListView.setSelection(position);
                }
            }
        });


        String[] names = new String[teamMembers.size()];
        String[] phones = new String[teamMembers.size()];
        for (int i = 0; i < teamMembers.size(); i++) {
            names[i] = teamMembers.get(i).getUserId().getUserNickname();
            phones[i] = teamMembers.get(i).getUserId().getUserPhone();
        }
        personBeenList = getData(names, phones);  //真实数据

        // 数据在放在adapter之前需要排序
        Collections.sort(personBeenList, new PinyinComparator());

        teamMemberAdapter = new TeamMemberAdapter(getActivity(), personBeenList);

        friendListView.setAdapter(teamMemberAdapter);
    }


    /**
     * 模拟数据获取（通过资源文件）
     * <p>
     * name : 昵称
     * image :
     *
     * @return
     */
    private List<PersonBean> getData(String[] names, String[] phones) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < names.length; i++) {
            String pinyin = PinyinUtils.getPingYin(names[i]);
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(names[i]);
            person.setImage(portraitBitmaps[i]);
            person.setPinYin(pinyin);
            person.setPhone(phones[i]);
            person.setTeamMemberPosition(teamMembers.get(i).getTeamMemberPosition());
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


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新

                if (personBeenList != null) {
                    personBeenList.clear();
                }
                obtainTeamMemberList(teamId.toString());
                //请求是否有更新（在这个时间段后）
                swipeLayout.setRefreshing(false);
            }
        }, 1200);
    }


}
