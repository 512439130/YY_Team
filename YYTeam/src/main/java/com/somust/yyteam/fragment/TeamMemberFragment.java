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
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.PersionInformationActivity;
import com.somust.yyteam.activity.SearchUserActivity;
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
 */

public class TeamMemberFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener{
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
    private FriendAdapter friendAdapter;

    private static final String TAG = "TeamMemberFragment:";

   // private User user;    //登录用户信息
    private List<TeamFriend> friendlist;   //登录用户的好友信息
    private Bitmap[] portraitBitmaps;

    private RelativeLayout addRelativeLayout;  //界面无好友，显示添加好友界面

    private Button addButton;  //添加好友按钮


    private RefreshLayout swipeLayout;
    private View header;

   // private List<User> allUser;


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team_member, null);
        initView();
        Intent intent = getActivity().getIntent();
        //user = (User) intent.getSerializableExtra("user");
        //allUser = (List<User>) intent.getSerializableExtra("allUser");

        //发送网络请求，获取好友列表
        //obtainFriendList(user.getUserPhone());
        initListener();

        return mView;
    }

    private void initView() {


        addRelativeLayout = (RelativeLayout) mView.findViewById(R.id.id_rl_add_friend);



        sidebar = (SideBar) mView.findViewById(R.id.sidebar);
        dialogTextView = (TextView) mView.findViewById(R.id.dialog);
        sidebar.setTextView(dialogTextView);
        addButton = (Button) mView.findViewById(R.id.id_add_friend);


        friendListView = (ListView) mView.findViewById(R.id.id_lv_friend);
        //头部布局
        header = getActivity().getLayoutInflater().inflate(R.layout.team_member_header, null);
        friendListView.addHeaderView(header);


        //初始化刷新
        swipeLayout = (RefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,R.color.color_bule,R.color.color_bule2,R.color.color_bule3);




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



    /**
     * 回调
     */
    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(getActivity(), "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {

                T.testShowShort(getActivity(), "您当前无好友");
                //friendlistLinearLayout.setVisibility(View.INVISIBLE);
                addRelativeLayout.setVisibility(View.VISIBLE);
            } else {
               // friendlistLinearLayout.setVisibility(View.VISIBLE);
                addRelativeLayout.setVisibility(View.INVISIBLE);
                T.testShowShort(getActivity(), "好友获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                friendlist = gson.fromJson(response, new TypeToken<List<TeamFriend>>() {
                }.getType());

                for (int i = 0; i < friendlist.size(); i++) {
                    L.v(TAG, "第1个好友信息" + friendlist.get(i));
                }
                portraitBitmaps = new Bitmap[friendlist.size()];
                for (int i = 0; i < friendlist.size(); i++) {
                    obtainImage(friendlist.get(i).getFriendPhone().getUserImage(), i);
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
                String userId = personBeenList.get(position-1).getPhone();
                String userNickname = personBeenList.get(position-1).getName();
                L.e(TAG, userId);
                L.e(TAG, userNickname);
                //打开个人信息界面（个人信息界面包含发送消息）
                Intent intent = new Intent(getActivity(), PersionInformationActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userNickname", userNickname);
                intent.putExtra("openState","friend");  //好友
                startActivity(intent);

            }
        });


         //设置刷新监听
        swipeLayout.setOnRefreshListener(this);

       /* addButton.setOnClickListener(new View.OnClickListener() {  //添加好友按钮
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SearchUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("allUser", (Serializable) allUser);
                intent.putExtra("Own_id",user.getUserPhone());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });*/

    }


    private void initSidebarAndView() {
        // 设置字母导航触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = friendAdapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    friendListView.setSelection(position);
                }
            }
        });


        String[] names = new String[friendlist.size()];
        String[] phones = new String[friendlist.size()];
        for (int i = 0; i < friendlist.size(); i++) {
            names[i] = friendlist.get(i).getFriendPhone().getUserNickname();
            phones[i] = friendlist.get(i).getFriendPhone().getUserPhone();
        }
        personBeenList = getData(names, phones);  //真实数据

        // 数据在放在adapter之前需要排序
        Collections.sort(personBeenList, new PinyinComparator());

        friendAdapter = new FriendAdapter(getActivity(), personBeenList);

        friendListView.setAdapter(friendAdapter);
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

        addRelativeLayout.setVisibility(View.INVISIBLE);


        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新

                if(personBeenList != null){
                    personBeenList.clear();
                }
                //obtainFriendList(user.getUserPhone());
                //请求是否有更新（在这个时间段后）
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }






}
