package com.somust.yy.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.somust.yy.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class TabLayoutActivity extends AppCompatActivity {


    private TabLayout tab;
    private ViewPager viewpager;
    private TabAdapter adapter;
    private CheckBox checkbox;
    private LinearLayout tabsLl, viewpagerLl;
    List<Fragment> fragments = new ArrayList<>();
    public static List<String> tabTitle = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        //隐藏标题栏,有效
        getSupportActionBar().hide();
        EventBus.getDefault().register(this);
        initviews();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initviews() {
        tab = (TabLayout) findViewById(R.id.tab);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tabsLl = (LinearLayout) findViewById(R.id.tabs_ll);
        viewpagerLl = (LinearLayout) findViewById(R.id.viewpager_ll);


        initData();

        for (int i = 0; i < tabTitle.size(); i++) {
            fragments.add(TabLayoutFragment.newInstance(i + 1));
        }
        adapter = new TabAdapter(getSupportFragmentManager(), fragments);
        adapter.setTabTitle(tabTitle);
        //给ViewPager设置适配器
        viewpager.setAdapter(adapter);
        //将TabLayout和ViewPager关联起来。
        tab.setupWithViewPager(viewpager);
        //设置可以滑动
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);


        checkbox = (CheckBox) findViewById(R.id.tab_check);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkbox.setBackground(getResources().getDrawable(R.drawable.up));
                    tabsLl.setVisibility(View.VISIBLE);
                    viewpagerLl.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.tabs_ll, TabLayoutFragment.newInstance(TabLayoutFragment.TABS)).commit();

                } else {
                    setDown();
                    adapter.setTabTitle(tabTitle);
                    tab.setupWithViewPager(viewpager);
                }
            }
        });


    }

    private void initData() {
        tabTitle.add("最新");
        tabTitle.add("移动开发");
        tabTitle.add("前端");
        tabTitle.add("系统&安全");
        tabTitle.add("数据库");
        tabTitle.add("业界");
        tabTitle.add("语言");
        tabTitle.add("云计算");
        tabTitle.add("游戏&图像");
        tabTitle.add("大数据");
        tabTitle.add("软件工程");
        tabTitle.add("程序人生");
    }


    @Subscribe
    public void onEventMainThread(Event item) {
        setDown();
        adapter.setTabTitle(tabTitle);
        tab.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(item.getPosition());

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setDown() {
        checkbox.setBackground(getResources().getDrawable(R.drawable.down));
        tabsLl.setVisibility(View.GONE);
        viewpagerLl.setVisibility(View.VISIBLE);
    }

}
