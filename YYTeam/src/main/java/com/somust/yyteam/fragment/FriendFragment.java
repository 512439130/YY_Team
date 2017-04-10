package com.somust.yyteam.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongIM;
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

    private ListView friendListView;


    //SideBar相关
    private List<PersonBean> personBeenList;
    private SideBar sidebar;
    private TextView dialogTextView;
    private FriendAdapter friendAdapter;

    private static final String TAG = "FriendFragment:";

    private User user;    //登录用户信息
    private List<TeamFriend> friendlist;   //登录用户的好友信息
    private Bitmap[] portraitBitmaps;


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.friend_fragment, null);

        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        //发送网络请求，获取好友列表
        obtainFriendList(user.getUserPhone());


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

    /**
     * 回调
     */
    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {

        }

        @Override
        public void onAfter(int id) {

        }

        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(getActivity(), "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("")) {

                T.testShowShort(getActivity(), "您当前无好友");
            } else {

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

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
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
                        initView();
                        initEvent();
                    }
                });
    }


    private void initView() {
        HeadPortrait = (ImageView) mView.findViewById(R.id.img1);
        name = (TextView) mView.findViewById(R.id.name);
        friendListView = (ListView) mView.findViewById(R.id.id_lv_friend);

        //insert
        initSidebar();

        friendAdapter = new FriendAdapter(getActivity(), personBeenList);

        friendListView.setAdapter(friendAdapter);

    }

    private void initEvent() {
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //由于字幕排序引起item不对应list(不能使用排序前的friendlist，必须使用排序后的personBeanList)
                String userId = personBeenList.get(position).getPhone();
                String userNickname = personBeenList.get(position).getName();
                L.e(TAG, userId);
                L.e(TAG, userNickname);
                //打开个人信息界面（个人信息界面包含发送消息）

                //打开单聊界面（根据position）
                RongIM.getInstance().startPrivateChat(getActivity(), userId, userNickname);

            }
        });
    }


    private void initSidebar() {
        //insert
        sidebar = (SideBar) mView.findViewById(R.id.sidebar);
        dialogTextView = (TextView) mView.findViewById(R.id.dialog);
        sidebar.setTextView(dialogTextView);

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
        /*String[] itemDatas = getResources().getStringArray(R.array.listpersons);
        data = getData(itemDatas);  //模拟数据  getData是将String转化为List*/

        String[] names = new String[friendlist.size()];
        String[] phones = new String[friendlist.size()];
        for (int i = 0; i < friendlist.size(); i++) {
            names[i] = friendlist.get(i).getFriendPhone().getUserNickname();
            phones[i] = friendlist.get(i).getFriendPhone().getUserPhone();
        }


        personBeenList = getData(names, phones);  //真实数据

        // 数据在放在adapter之前需要排序
        Collections.sort(personBeenList, new PinyinComparator());


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
}
