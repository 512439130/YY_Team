package com.somust.yyteam.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;

import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.bean.TeamFriend;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.fragment.FriendFragment;
import com.somust.yyteam.fragment.TestFragment;
import com.somust.yyteam.fragment.TabFragment;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.ChangeColorIconWithText;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Request;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private String[] mTitles = new String[]{   //定义四个Title用于显示四个Fragment的上边
            "Fourth Fragment"
    };
    private ViewPager mViewPager;
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
    private FragmentPagerAdapter mFragmentPagerAdapter;//将tab页面持久在内存中
    private Fragment mConversationList;  //会话界面Fragment
    private Fragment mConversationFragment = null;

    private List<Fragment> mFragment = new ArrayList<>();

    private static final String TAG = "HomeActivity:";

    private User user;    //登录用户的信息


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = this.getIntent();
        user=(User)intent.getSerializableExtra("user");

        initView();

        //初始化数据
        initDatas();

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);


    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
        mTabIndicators.add(one);

        ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
        mTabIndicators.add(two);

        ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
        mTabIndicators.add(three);

        ChangeColorIconWithText four = (ChangeColorIconWithText) findViewById(R.id.id_indicator_four);
        mTabIndicators.add(four);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);
        one.setIconAlpha(1.0f);
    }

    private void initDatas() {

        mConversationList = initConversationList();  //获取融云会话列表的对象
        mFragment.add(mConversationList);//加入会话列表（第一页）

        mFragment.add(FriendFragment.getInstance());//加入第2页,朋友列表
        mFragment.add(TestFragment.getInstance());//加入第3页 测试功能界面

        for (String title : mTitles) {
            TabFragment tabFragment = new TabFragment();

            //Bundle为Fragment传递参数
            Bundle bundle = new Bundle();
            bundle.putString(TabFragment.TITLE, title);
            tabFragment.setArguments(bundle);
            mFragment.add(tabFragment);
        }

        //初始化Adapter这里使用FragmentPagerAdapter
        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {  //括号里的参数需要FragmentManager
            @Override
            public int getCount() {//getCount()返回的是ViewPager页面的数量
                return mFragment.size();
            }

            @Override
            public Fragment getItem(int position) {//getItem()返回的是要显示的fragent对象
                return mFragment.get(position);
            }

        };

    }

    /**
     * 点击Tab按钮
     *
     * @param v
     */
    public void onClick(View v) {
        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_one:  //会话列表
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                L.v("TAG","点击会话列表");
                break;
            case R.id.id_indicator_two:  //好友列表
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);




                L.v("TAG","点击好友列表");
                break;
            case R.id.id_indicator_three:  //社团圈列表
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                L.v("TAG","点击社团圈列表");
                break;
            case R.id.id_indicator_four:  //我的页列表
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                L.v("TAG","点击我的页列表");
                break;
        }
    }

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }

    }
    @Override
    public void onPageSelected(int position) {
        //发送网络请求
        L.v(TAG,""+position);



    }



    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * appendQueryParameter对具体的会话列表做展示
     */
    private Fragment initConversationList() {
        if (mConversationFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置私聊会是否聚合显示
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationFragment;
        }
    }


}
