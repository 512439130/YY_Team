package com.somust.yyteam.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.LoginActivity;
import com.somust.yyteam.activity.RePassActivity;
import com.somust.yyteam.activity.SubConversationListActivtiy;
import com.somust.yyteam.adapter.CommunityAdapter;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.refreshview.RefreshLayout;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by DELL on 2016/3/14.
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
        header = getActivity().getLayoutInflater().inflate(R.layout.refresh_header, null);
        swipeLayout = (RefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,R.color.color_bule,R.color.color_bule2,R.color.color_bule3);
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
