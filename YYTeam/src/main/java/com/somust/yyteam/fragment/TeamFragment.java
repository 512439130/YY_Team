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
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.TeamInformationActivity;
import com.somust.yyteam.activity.TeamNewsActivity;
import com.somust.yyteam.adapter.TeamAdapter;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMessage;
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
/**
 * Created by DELL on 2016/3/14.
 */

/**
 * 社团新闻
 */
public class TeamFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {
    private static final String TAG = "TeamFragment:";

    public static TeamFragment instance = null;//单例模式
    public static TeamFragment getInstance() {
        if (instance == null) {
            instance = new TeamFragment();
        }
        return instance;
    }

    private View mView;
    private RefreshLayout swipeLayout;
    private View header;

    private ListView teamNewsListView;
    private TeamAdapter teamAdapter;

    public List<Team> teams = new ArrayList<>();   //存放网络接受的数据
    public List<TeamMessage> teamMessages = new ArrayList<>();   //将网络接收的数据装换为相应bean


    private Bitmap[] teamBitmaps;
    private Bitmap[] presidentBitmaps;


    private boolean teamFlag = false;
    private boolean presidentFlag = false;




    public List<Team> intentDatas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team, null);
        initView();
        requestData();
        initListener();
        return mView;

    }

    /**
     * 初始化数据
     */
    private void initView() {
        teamMessages.clear();
        //头部
        header = getActivity().getLayoutInflater().inflate(R.layout.team_header, null);

        swipeLayout = (RefreshLayout) mView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,R.color.color_bule,R.color.color_bule2,R.color.color_bule3);
        teamNewsListView = (ListView) mView.findViewById(R.id.list);
        teamNewsListView.addHeaderView(header);
    }


    private Handler teamHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();


            if (bundle.getString("team_success") == "team_success") {  //社团图片成功获取
                for(int i = 0;i<teams.size();i++){
                    teamMessages.get(i).setTeamImage(teamBitmaps[i]);
                }
                teamFlag = true;
            }
            if (bundle.getString("president_success") == "president_success") {  //创建人图片成功获取
                for(int i = 0;i<teams.size();i++){
                    teamMessages.get(i).setUserImage(presidentBitmaps[i]);
                }
                presidentFlag = true;
            }

            if(teamFlag&&presidentFlag){   //2张图片都请求成功时

                //请求是否有更新（在这个时间段后）
                teamAdapter.notifyDataSetChanged();

            }

        }
    };

    /**
     * 添加数据
     */
    private void initDatas() {

        teamAdapter = new TeamAdapter(getActivity(), teamMessages);
        teamNewsListView.setAdapter(teamAdapter);
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

                    Team teams = intentDatas.get(position-1);  //获取当前item的bean
                    Intent intent = new Intent(getActivity(), TeamInformationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("teams",teams);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }


    /**
     * 获取全部社团
     */
    private void requestData() {
        OkHttpUtils
                .post()
                .url(ConstantUrl.TeamUrl + ConstantUrl.getTeam_interface)
                .build()
                .execute(new MyTeamRequestCallback());
    }

    /**
     * 请求回调
     */
    public class MyTeamRequestCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(getActivity(), "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {
                T.testShowShort(getActivity(), "当前无社团");
            } else {

                T.testShowShort(getActivity(), "所有社团获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                teams = gson.fromJson(response, new TypeToken<List<Team>>() {
                }.getType());
                DateUtil.TeamSortDate(teams);   //对社团结果排序
                intentDatas = teams;


                teamBitmaps= new Bitmap[teams.size()];
                presidentBitmaps= new Bitmap[teams.size()];

                for (int i = 0; i < teams.size(); i++) {
                    TeamMessage message = new TeamMessage();

                    message.setTeamName(teams.get(i).getTeamName());
                    message.setTeamTime(teams.get(i).getTeamTime());
                    message.setTeamType(teams.get(i).getTeamType());

                    message.setUserNickname(teams.get(i).getTeamPresident().getUserNickname());

                    //通过网络请求获取图片
                    obtainTeamImage(teams.get(i).getTeamImage(), i);
                    obtainPresidentImage(teams.get(i).getTeamPresident().getUserImage(), i);
                    teamMessages.add(message);

                }

                initDatas();
            }

        }
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
                        UpdateUi(teamHandler, "team_success", "team_success");
                    }
                });

    }




    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainPresidentImage(String url,final int i) {

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
                        presidentBitmaps[i] = bitmap;
                        UpdateUi(teamHandler, "president_success", "president_success");
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
                teamMessages.clear();
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
