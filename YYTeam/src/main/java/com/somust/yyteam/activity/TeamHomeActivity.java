package com.somust.yyteam.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.fragment.CommunityFragment;
import com.somust.yyteam.fragment.FriendFragment;
import com.somust.yyteam.fragment.MineFragment;
import com.somust.yyteam.fragment.TeamFragment;
import com.somust.yyteam.fragment.TeamMemberFragment;
import com.somust.yyteam.fragment.TeamMineFragment;
import com.somust.yyteam.fragment.TeamNewsFragment;
import com.somust.yyteam.popwindow.ActionItem;
import com.somust.yyteam.popwindow.TitlePopup;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.ChangeColorIconWithText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class TeamHomeActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {


    private ViewPager mViewPager;
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
    private FragmentPagerAdapter mFragmentPagerAdapter;//将tab页面持久在内存中

    private List<Fragment> mFragment = new ArrayList<>();

    private static final String TAG = "TeamHomeActivity:";


    private ImageView iv_add;
    private ImageView iv_search;

    private TitlePopup titlePopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_team);

        T.isShow = true;  //关闭toast
        L.isDebug = true;  //关闭Log

        //接收用户的登录信息user

        initView();
        //初始化数据
        initDatas();

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(4);  //设置缓存的页面个数

        initPopwindow();  //初始化titlebar中的popwindow
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

        iv_add = (ImageView) findViewById(R.id.id_add);
        iv_search = (ImageView) findViewById(R.id.id_search);
        iv_add.setOnClickListener(new MyTitleBarOncliclListener());
        iv_search.setOnClickListener(new MyTitleBarOncliclListener());
    }

    /**
     * 实例化标题栏弹窗
     */
    private void initPopwindow() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        titlePopup.setItemOnClickListener(new MyOnItemOnClickListener());
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, R.string.team_create,
                R.mipmap.icon_menu_group));
        titlePopup.addAction(new ActionItem(this, R.string.send_team_task,
                R.mipmap.icon_menu_addfriend));
        titlePopup.addAction(new ActionItem(this, R.string.see_tem_task,
                R.mipmap.icon_menu_sao));
    }

    private void initDatas() {
        //4个Fragment
        mFragment.add(TeamFragment.getInstance());//加入第1页,大学社团列表
        mFragment.add(TeamMemberFragment.getInstance());  //社团成员列表
        mFragment.add(CommunityFragment.getInstance());//社团圈列表
        mFragment.add(TeamMineFragment.getInstance());//我的社团页（社团任务通知item）


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
            case R.id.id_indicator_one:  //大学社团列表
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                L.v("TAG", "点击大学社团列表");
                break;
            case R.id.id_indicator_two:  //社团成员列表
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                L.v("TAG", "点击社团成员列表");
                break;
            case R.id.id_indicator_three:  //社团圈列表
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                L.v("TAG", "点击社团圈列表");
                break;
            case R.id.id_indicator_four:  //社团页列表
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                L.v("TAG", "点击社团页列表");
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
        L.v("position" + position);  //滑动结束后的页面位置
        L.v("positionOffset" + positionOffset);  //为精确滑动的值
        if (positionOffset > 0) {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }

    }

    @Override
    public void onPageSelected(int position) {
        //选择到某个fragment时
        L.v(TAG, "" + position);
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }




    /**
     * titlebar的监听事件
     */
    private class MyTitleBarOncliclListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_add:  //打开popwindow
                    titlePopup.show(findViewById(R.id.id_add));
                    break;
                case R.id.id_search: //打开查询页面
                    Intent intent = new Intent(TeamHomeActivity.this,SearchTeamActivity.class);
                    startActivity(intent);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * popwindow中item的点击事件监听
     */
    private class MyOnItemOnClickListener implements TitlePopup.OnItemOnClickListener {
        @Override
        public void onItemClick(ActionItem item, int position) {
            // mLoadingDialog.show();
            switch (position) {
                case 0:// 创建大学社团
                    T.testShowShort(TeamHomeActivity.this, "创建大学社团");
                    break;
                case 1:// 发布任务安排
                    T.testShowShort(TeamHomeActivity.this, "发布任务安排");
                    break;
                case 2:// 查看任务通知
                    T.testShowShort(TeamHomeActivity.this, "查看任务通知");
                    break;
                default:
                    break;

            }
        }
    }

}
