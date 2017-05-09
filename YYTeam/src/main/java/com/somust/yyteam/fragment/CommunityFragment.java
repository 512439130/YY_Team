package com.somust.yyteam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.somust.yyteam.R;
import com.somust.yyteam.adapter.CommunityAdapter;
import com.somust.yyteam.view.refreshview.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DELL on 2016/3/14.
 */
/**
 * 社团圈
 */
public class CommunityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    public static CommunityFragment instance = null;//单例模式

    public static CommunityFragment getInstance() {
        if (instance == null) {
            instance = new CommunityFragment();
        }

        return instance;
    }

    private View mView;


    private RefreshLayout swipeLayout;
    private ListView listView;
    private CommunityAdapter communityAdapter;
    private ArrayList<HashMap<String, String>> list;   //测试数据（需要更换网络数据）
    private View header;



    private static final String TAG = "CommunityFragment:";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_community, null);
        initView();
        initDatas();
        initListener();
        return mView;

    }



    /**
     * 初始化数据
     */
    private void initView() {
        header = getActivity().getLayoutInflater().inflate(R.layout.community_header, null);
        swipeLayout = (RefreshLayout) mView.findViewById(R.id.swipe_container);

        swipeLayout.setColorSchemeResources( android.R.color.holo_red_light, android.R.color.holo_orange_dark,android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景
    }




    /**
     * 添加数据
     */
    private void initDatas() {
        list = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("itemImage", i+"默认");
            map.put("itemText", i+"默认");
            list.add(map);
        }
        listView = (ListView) mView.findViewById(R.id.list);
        listView.addHeaderView(header);
        communityAdapter = new CommunityAdapter(getActivity(), list);
        listView.setAdapter(communityAdapter);
    }

    /**
     * 设置监听
     */
    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
    }




    /**
     * 上拉刷新
     */
    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
                list.clear();
                for (int i = 0; i < 8; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("itemImage", i+"刷新");
                    map.put("itemText", i+"刷新");
                    list.add(map);
                }
                communityAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        }, 2000);
    }


    /**
     * 下拉加载更多
     */
    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
                swipeLayout.setLoading(false);
                for (int i = 1; i < 10; i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("itemImage", i+"更多");
                    map.put("itemText", i+"更多");
                    list.add(map);
                }
                communityAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

}
