package com.somust.yyteam.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;

import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;

import com.somust.yyteam.bean.User;
import com.somust.yyteam.fragment.FriendFragment;
import com.somust.yyteam.fragment.MineFragment;
import com.somust.yyteam.fragment.TestFragment;
import com.somust.yyteam.popwindow.ActionItem;
import com.somust.yyteam.popwindow.TitlePopup;

import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.ChangeColorIconWithText;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.push.notification.PushNotificationMessage;

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

    private ImageView iv_add;
    private ImageView iv_search;

    private TitlePopup titlePopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");

        initView();

        //初始化数据
        initDatas();

        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        inint();  //初始化titlebar中的popwindow
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
    private void inint() {
        // 实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        titlePopup.setItemOnClickListener(new MyOnItemOnClickListener());
        // 给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, R.string.groupchat,
                R.mipmap.icon_menu_group));
        titlePopup.addAction(new ActionItem(this, R.string.addfriend,
                R.mipmap.icon_menu_addfriend));
        titlePopup.addAction(new ActionItem(this, R.string.qrcode,
                R.mipmap.icon_menu_sao));
        titlePopup.addAction(new ActionItem(this, R.string.money,
                R.mipmap.abv));
    }

    private void initDatas() {

        mConversationList = initConversationList();  //获取融云会话列表的对象
        mFragment.add(mConversationList);//加入会话列表（第一页）
        mFragment.add(FriendFragment.getInstance());//加入第2页,朋友列表
        mFragment.add(TestFragment.getInstance());//加入第3页 测试功能界面
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
                case 0:// 发起群聊
                    T.testShowShort(HomeActivity.this, "创建讨论组");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    intent.setClass(HomeActivity.this, GroupChatActivity.class);
                    startActivity(intent);
                    break;
                case 1:// 添加朋友
                    T.testShowShort(HomeActivity.this, "添加好友");
                    break;
                case 2:// 扫一扫
                    T.testShowShort(HomeActivity.this, "扫一扫");

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

    @Override
    protected void onPause() {
        super.onPause();

    }
}
