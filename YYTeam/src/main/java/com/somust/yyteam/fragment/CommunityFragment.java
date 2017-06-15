package com.somust.yyteam.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.CommunityAdapter;
import com.somust.yyteam.bean.Community;
import com.somust.yyteam.bean.CommunityMessage;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.TeamNewsMessage;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.refreshview.RefreshLayout;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

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
    private View header;


    public List<Community> communities = new ArrayList<>();   //存放网络接受的数据
    public List<CommunityMessage> communityMessages = new ArrayList<>();   //将网络接收的数据装换为相应bean


    private Bitmap[] userBitmaps;
    private Bitmap[] communityBitmaps;



    private boolean userFlag = false;
    private boolean communityFlag = false;

    public List<Community> intentDatas;

    private User user;

    private static final String TAG = "CommunityFragment:";

    private TextView noDataTextView;  //无数据展示内容



    private Handler communityImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.getString("community_success") == "community_success") {  //社团圈图片刷新
                for(int i = 0;i<communities.size();i++){
                    if(communityMessages.size() != 0) {
                        communityMessages.get(i).setCommunityImage(communityBitmaps[i]);
                    }
                }
                communityFlag = true;
            }
            if (bundle.getString("user_success") == "user_success") {  //用户头像刷新
                for(int i = 0;i<communities.size();i++){
                    if(communityMessages.size() != 0){
                        communityMessages.get(i).setUserImage(userBitmaps[i]);
                    }
                }
                userFlag = true;
            }

            if(communityFlag&&userFlag){   //三张图片都请求成功时

                //请求是否有更新（在这个时间段后）
                communityAdapter.notifyDataSetChanged();

            }

        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_community, null);
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");

        initView();
        requestData();
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
        listView = (ListView) mView.findViewById(R.id.list);
        listView.addHeaderView(header);
        noDataTextView = (TextView) mView.findViewById(R.id.null_data_tv);
    }




    /**
     * 添加数据
     */
    private void initDatas() {

        communityAdapter = new CommunityAdapter(getActivity(), communityMessages);
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
     * 获取社团新闻
     */
    private void requestData() {
        OkHttpUtils
                .post()
                .url(ConstantUrl.communityUrl + ConstantUrl.getCommunity_interface)
                .build()
                .execute(new MyStringCallback());
    }

    /**
     * 请求回调
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
                T.testShowShort(getActivity(), "当前无人分享社团圈");
                noDataTextView.setVisibility(View.VISIBLE);
                noDataTextView.setText("当前无人分享社团圈");
            } else {
                noDataTextView.setVisibility(View.INVISIBLE);
                T.testShowShort(getActivity(), "社团圈获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                communities = gson.fromJson(response, new TypeToken<List<Community>>() {
                }.getType());
                DateUtil.CommunitySortDate(communities);   //对结果排序
                intentDatas = communities;


                userBitmaps= new Bitmap[communities.size()];
                communityBitmaps= new Bitmap[communities.size()];

                for (int i = 0; i < communities.size(); i++) {
                    CommunityMessage communityMessage = new CommunityMessage();
                    communityMessage.setCommunityId(communities.get(i).getCommunityId());
                    communityMessage.setCommunityContent(communities.get(i).getCommunityContent());
                    communityMessage.setCommunityTime(communities.get(i).getCommunityTime());
                    communityMessage.setUserNickname(communities.get(i).getTeamMemberId().getUserId().getUserNickname());
                    //通过网络请求获取图片
                    obtainUserImage(communities.get(i).getTeamMemberId().getUserId().getUserImage(), i);
                    obtainCommunityImage(communities.get(i).getCommunityImage(), i);

                    communityMessages.add(communityMessage);

                }


            }
            initDatas();
        }
    }


    /**
     * 请求新闻图片
     * @param url
     * @param i
     */
    public void obtainCommunityImage(String url, final int i) {
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
                        communityBitmaps[i]=bitmap;
                        UpdateUi(communityImageHandler, "community_success", "community_success");
                    }
                });

    }

    /**
     * 请求社团logo
     * @param url
     * @param i
     */
    public void obtainUserImage(String url, final int i) {
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
                        userBitmaps[i]=bitmap;
                        UpdateUi(communityImageHandler, "user_success", "user_success");
                    }
                });

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

                if(communityMessages != null){
                    communityMessages.clear();
                }
                if(communities != null){
                    communities.clear();
                }
                requestData();
                swipeLayout.setRefreshing(false);
            }
        }, 1200);
    }


    /**
     * 上啦拉加载更多
     */
    @Override
    public void onLoad() {
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
                swipeLayout.setLoading(false);
            }
        }, 1200);
    }


    /**
     * 更新UI和控制子线程设置图片
     *
     * @param handler
     * @param key
     * @param value
     */
    private void UpdateUi(Handler handler, String key, String value ) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }




}
