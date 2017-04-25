package com.somust.yyteam.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.TeamNewsActivity;
import com.somust.yyteam.adapter.TeamNewsAdapter;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.TeamNewsMessage;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.refreshview.RefreshLayout;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

import android.os.Handler;
/**
 * Created by DELL on 2016/3/14.
 */

/**
 * 社团新闻
 */
public class TeamNewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    public static TeamNewsFragment instance = null;//单例模式
    public static TeamNewsFragment getInstance() {
        if (instance == null) {
            instance = new TeamNewsFragment();
        }

        return instance;
    }

    private View mView;
    private RefreshLayout swipeLayout;
    private View header;

    private ListView teamNewsListView;
    private TeamNewsAdapter teamNewsAdapter;

    public List<TeamNews> teamNewsList = new ArrayList<>();   //存放网络接受的数据
    public List<TeamNewsMessage> teamNewsMessages = new ArrayList<>();   //将网络接收的数据装换为相应bean


    private Bitmap[] newsBitmaps;
    private Bitmap[] teamBitmaps;
    private Bitmap[] presidentBitmaps;


    private boolean newsFlag = false;
    private boolean teamFlag = false;
    private boolean presidentFlag = false;


    private static final String TAG = "TeamNewsFragment:";

    public List<TeamNews> intentDatas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team_news, null);
        initView();
        requestData();
        initListener();
        return mView;

    }

    /**
     * 初始化数据
     */
    private void initView() {
        //头部
        header = getActivity().getLayoutInflater().inflate(R.layout.team_news_header, null);

        swipeLayout = (RefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,R.color.color_bule,R.color.color_bule2,R.color.color_bule3);
        teamNewsListView = (ListView) mView.findViewById(R.id.list);
        teamNewsListView.addHeaderView(header);
    }


    private Handler imageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.getString("news_success") == "news_success") {  //新闻图片成功获取
                for(int i = 0;i<teamNewsList.size();i++){
                    teamNewsMessages.get(i).setNewsImage(newsBitmaps[i]);
                }
                newsFlag = true;
            }
            if (bundle.getString("team_success") == "team_success") {  //社团图片成功获取
                for(int i = 0;i<teamNewsList.size();i++){
                    teamNewsMessages.get(i).setTeamImage(teamBitmaps[i]);
                }
                teamFlag = true;
            }

            if(newsFlag&&teamFlag){   //三张图片都请求成功时

                //请求是否有更新（在这个时间段后）
                teamNewsAdapter.notifyDataSetChanged();

            }

        }
    };

    /**
     * 添加数据
     */
    private void initDatas() {

        teamNewsAdapter = new TeamNewsAdapter(getActivity(), teamNewsMessages);
        teamNewsListView.setAdapter(teamNewsAdapter);
    }
    /**
     * 设置监听
     */
    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);

        teamNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //新闻item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(intentDatas != null ){
                    L.v(TAG,"数据获取完成");

                    //intent传递新闻id
                    TeamNews teamNews = intentDatas.get(position-1);  //获取当前item的bean

                    Intent intent = new Intent(getActivity(), TeamNewsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("teamNews",teamNews);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 获取社团新闻
     */
    private void requestData() {
        OkHttpUtils
                .post()
                .url(ConstantUrl.TeamUrl + ConstantUrl.getTeamNews_interface)
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
                T.testShowShort(getActivity(), "当前无社团新闻");
            } else {

                T.testShowShort(getActivity(), "社团新闻获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                teamNewsList = gson.fromJson(response, new TypeToken<List<TeamNews>>() {
                }.getType());
                DateUtil.sortDate(teamNewsList);   //对结果排序
                intentDatas = teamNewsList;


                newsBitmaps= new Bitmap[teamNewsList.size()];
                teamBitmaps= new Bitmap[teamNewsList.size()];
                presidentBitmaps= new Bitmap[teamNewsList.size()];

                for (int i = 0; i < teamNewsList.size(); i++) {
                    TeamNewsMessage message = new TeamNewsMessage();
                    message.setNewsId(teamNewsList.get(i).getNewsId());
                    message.setNewsTitle(teamNewsList.get(i).getNewsTitle());
                    message.setNewsContent(teamNewsList.get(i).getNewsContent());
                    message.setNewsTime(teamNewsList.get(i).getNewsTime());

                    message.setTeamName(teamNewsList.get(i).getTeamId().getTeamName());
                    message.setTeamTime(teamNewsList.get(i).getTeamId().getTeamTime());
                    message.setTeamIntroduce(teamNewsList.get(i).getTeamId().getTeamIntroduce());
                    message.setTeamType(teamNewsList.get(i).getTeamId().getTeamType());

                    message.setUserPhone(teamNewsList.get(i).getTeamId().getTeamPresident().getUserPhone());
                    message.setUserNickname(teamNewsList.get(i).getTeamId().getTeamPresident().getUserNickname());
                    message.setUserSex(teamNewsList.get(i).getTeamId().getTeamPresident().getUserSex());

                    //通过网络请求获取图片
                    obtainNewImage(teamNewsList.get(i).getNewsImage(), i);
                    obtainTeamImage(teamNewsList.get(i).getTeamId().getTeamImage(), i);
                    //obtainPresidentImage(teamNewsList.get(i).getTeamId().getTeamPresident().getUserImage(), i);
                    teamNewsMessages.add(message);

                }

                initDatas();
            }

        }
    }


    /**
     * 请求新闻图片
     * @param url
     * @param i
     */
    public void obtainNewImage(String url, final int i) {
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
                        newsBitmaps[i]=bitmap;
                        UpdateUi(imageHandler, "news_success", "news_success");
                    }
                });

    }

    /**
     * 请求社团logo
     * @param url
     * @param i
     */
    public void obtainTeamImage(String url, final int i) {
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
                        teamBitmaps[i]=bitmap;
                        UpdateUi(imageHandler, "team_success", "team_success");
                    }
                });

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
                teamNewsMessages.clear();
                requestData();

                swipeLayout.setRefreshing(false);
            }
        }, 1500);
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
                //teamNewsAdapter.notifyDataSetChanged();
            }
        }, 1500);
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
