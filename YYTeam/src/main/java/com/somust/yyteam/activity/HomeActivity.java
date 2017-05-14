package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;

import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;

import com.somust.yyteam.bean.User;
import com.somust.yyteam.fragment.FriendFragment;
import com.somust.yyteam.fragment.MineFragment;
import com.somust.yyteam.fragment.TeamNewsFragment;
import com.somust.yyteam.popwindow.ActionItem;
import com.somust.yyteam.popwindow.TitlePopup;

import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.ChangeColorIconWithText;
import com.somust.yyteam.view.popupwindow.SelectSearchPopupWindowDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
    private FragmentPagerAdapter mFragmentPagerAdapter;//将tab页面持久在内存中
    private Fragment mConversationList;  //会话界面Fragment
    private Fragment mConversationFragment = null;
    private List<Fragment> mFragment = new ArrayList<>();

    private static final String TAG = "HomeActivity:";

    private User user;    //登录用户的信息
    private ImageView iv_add;
    private ImageView iv_search;
    private TitlePopup addTitlePopup;
    private TitlePopup searchTitlePopup;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        immersiveStatusBar();


        mActivity = this;
        T.isShow = true;  //控制toast显示
        L.isDebug = true;  //控制Log显示

        L.e(TAG, "onCreate调用");

        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");
        initView();
        initDatas();

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(4);  //设置缓存的页面个数

        initAddPopwindow();  //初始化addTitlebar中的popwindow

    }

    /**
     * 沉浸式状态栏（伪）
     */
    private void immersiveStatusBar() {
        //沉浸式状态栏（伪）
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
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
    private void initAddPopwindow() {
        // 实例化标题栏弹窗
        addTitlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addTitlePopup.setItemOnClickListener(new MyAddOnItemOnClickListener());
        // 给标题栏弹窗添加子类
        addTitlePopup.addAction(new ActionItem(this, R.string.groupchat, R.mipmap.icon_menu_group));
        addTitlePopup.addAction(new ActionItem(this, R.string.addfriend, R.mipmap.icon_menu_addfriend));
        addTitlePopup.addAction(new ActionItem(this, R.string.addteam, R.mipmap.icon_menu_addteam));
        addTitlePopup.addAction(new ActionItem(this, R.string.money, R.mipmap.abv));
    }
    private void initDatas() {

        mConversationList = initConversationList();  //获取融云会话列表的对象
        mFragment.add(mConversationList);//加入会话列表（第一页）
        mFragment.add(FriendFragment.getInstance());//加入第2页,朋友列表
        // mFragment.add(TestFragment.getInstance());//加入第3页 测试功能界面
        // mFragment.add(CommunityFragment.getInstance());  //社团圈改变位置
        mFragment.add(TeamNewsFragment.getInstance());  //社团新闻页
        mFragment.add(MineFragment.getInstance());//加入第4页，我的页


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
                L.v("TAG", "点击会话列表");
                break;
            case R.id.id_indicator_two:  //好友列表
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);


                L.v("TAG", "点击好友列表");
                break;
            case R.id.id_indicator_three:  //社团圈列表
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                L.v("TAG", "点击社团圈列表");
                break;
            case R.id.id_indicator_four:  //我的页列表
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3, false);
                L.v("TAG", "点击我的页列表");
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
            changAlpha(position, positionOffset);
        }

    }

    @Override
    public void onPageSelected(int position) {
        //选择到某个fragment时
        L.v(TAG, "" + position);
        changAlpha(position);

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
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                    .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                    .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                    .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//系统
                    .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationFragment;
        }
    }


    /**
     * titlebar的监听事件
     */
    private class MyTitleBarOncliclListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.id_add:  //打开popwindow
                    addTitlePopup.showAddView(view);
                    break;
                case R.id.id_search: //打开查询页面
                    //调用例子
                    openSelectView(view);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 打开搜索域选择框
     *
     * @param view
     */
    private void openSelectView(View view) {
        SelectSearchPopupWindowDialog selectSearchPopupWindowDialog = new SelectSearchPopupWindowDialog(HomeActivity.this, view, mActivity, new SelectSearchPopupWindowDialog.Callback() {
            @Override
            public void selectUserClick(View v) {
                L.v(TAG, "选择查询用户");
                Intent intent = new Intent(HomeActivity.this, SearchUserActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("Own_id", user.getUserPhone());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void selectTeamClick(View v) {
                L.v(TAG, "选择查询社团");
                Intent intent = new Intent(HomeActivity.this, SearchTeamActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        selectSearchPopupWindowDialog.create();
    }

    /**
     * popwindow中item的点击事件监听
     */
    private class MyAddOnItemOnClickListener implements TitlePopup.OnItemOnClickListener {
        @Override
        public void onItemClick(ActionItem item, int position) {
            Intent intent;
            Bundle bundle;
            switch (position) {
                case 0:// 发起群聊
                    T.testShowShort(HomeActivity.this, "创建讨论组");
                    intent = new Intent();
                    bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    intent.setClass(HomeActivity.this, GroupChatActivity.class);
                    startActivity(intent);

                    break;
                case 1:// 添加朋友
                    T.testShowShort(HomeActivity.this, "添加好友");
                    intent = new Intent(HomeActivity.this, SearchUserActivity.class);
                    bundle = new Bundle();
                    intent.putExtra("Own_id", user.getUserPhone());
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case 2:// 创建大学社团
                    T.testShowShort(HomeActivity.this, "创建大学社团");
                    intent = new Intent(HomeActivity.this, CreateTeamActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    break;
                case 3:// 收钱
                    T.testShowShort(HomeActivity.this, "我的钱包");
                    //打开红包界面
                    JrmfClient.intentWallet(HomeActivity.this);
                    break;
                default:
                    break;

            }
        }
    }


    /**
     * 刷新用户缓存数据
     * 测试更换头像http://img.woyaogexing.com/2016/10/04/5da81cd8b7bc3f79!200x200.jpg
     *
     * @param userid   需要更换的用户Id
     * @param nickname 用户昵称
     * @param urlPath  头像图片地址
     *                 userInfo 需要更新的用户缓存数据。
     */
    public void refreshUserInfo(String userid, String nickname, String urlPath) {
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(userid, nickname, Uri.parse(urlPath)));
    }


    private boolean isExit = false;//定义是否退出程序的标记

    private Handler mKeyDownHandler = new Handler() {    //定义接受用户发送信息的handler
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //标记用户不退出状态
            isExit = false;
        }
    };


    //监听手机的物理按键点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击的是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果isExit标记为false，提示用户再次按键
            if (!isExit) {
                isExit = true;
                T.testShowShort(HomeActivity.this, "再按一次退出程序");
                //如果用户没有在2秒内再次按返回键的话，就发送消息标记用户为不退出状态
                mKeyDownHandler.sendEmptyMessageDelayed(0, 2000);
            }
            //如果isExit标记为true，退出程序
            else {
                //退出程序
                finish();
                System.exit(0);
            }
        }
        return false;
    }


    /**
     * 根据滑动设置透明度
     */
    private void changAlpha(int pos, float posOffset) {
        int nextIndex = pos + 1;
        if (posOffset > 0) {
            //设置tab的颜色渐变效果
           /* mButtonList.get(nextIndex).setAlpha(posOffset);
            mButtonList.get(pos).setAlpha(1 - posOffset);*/
            //设置fragment的颜色渐变效果
            mFragment.get(nextIndex).getView().setAlpha(posOffset);
            mFragment.get(pos).getView().setAlpha(1 - posOffset);
            //设置fragment滑动视图由大到小，由小到大的效果
            mFragment.get(nextIndex).getView().setScaleX(0.75F + posOffset / 4);
            mFragment.get(nextIndex).getView().setScaleY(0.75F + posOffset / 4);
            mFragment.get(pos).getView().setScaleX(1 - (posOffset / 4));
            mFragment.get(pos).getView().setScaleY(1 - (posOffset / 4));
        }
    }

    /**
     * 一开始运行、滑动和点击tab结束后设置tab的透明度，fragment的透明度和大小
     */
    private void changAlpha(int postion) {
        for (int i = 0; i < mFragment.size(); i++) {
            if (i == postion) {
                //mButtonList.get(i).setAlpha(1.0f);
                if (null != mFragment.get(i).getView()) {
                    mFragment.get(i).getView().setAlpha(1.0f);
                    mFragment.get(i).getView().setScaleX(1.0f);
                    mFragment.get(i).getView().setScaleY(1.0f);
                }
            } else {
                //mButtonList.get(i).setAlpha(0.0f);
                if (null != mFragment.get(i).getView()) {
                    mFragment.get(i).getView().setAlpha(0.0f);
                    mFragment.get(i).getView().setScaleX(0.0f);
                    mFragment.get(i).getView().setScaleY(0.0f);
                }
            }
        }
    }


    //测试保留
    /*@Override
    protected void onStart() {
        super.onStart();

        L.e(TAG,"onStart调用");
    }
    @Override
    protected void onResume() {
        super.onResume();
        L.e(TAG,"onResume调用");
    }


    @Override
    protected void onPause() {
        super.onPause();
        L.e(TAG,"onPause调用");

    }


    @Override
    protected void onStop() {
        super.onStop();
        L.e(TAG,"onStop调用");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.e(TAG,"onDestroy调用");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        L.e(TAG,"onRestart调用");
    }*/
}
