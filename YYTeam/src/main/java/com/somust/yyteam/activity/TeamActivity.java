package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.TeamAdapter;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMessage;
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
import java.util.List;

import okhttp3.Call;

/**
 * Created by 13160677911 on 2017-5-5.
 * 大学社团列表
 */

public class TeamActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener{
    private static final String TAG = "TeamActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;


    private RefreshLayout swipeLayout;


    private ListView teamNewsListView;
    private TeamAdapter teamAdapter;

    public List<Team> teams = new ArrayList<>();   //存放网络接受的数据
    public List<TeamMessage> teamMessages = new ArrayList<>();   //将网络接收的数据装换为相应bean


    private Bitmap[] teamBitmaps;
    private Bitmap[] presidentBitmaps;


    private boolean teamFlag = false;
    private boolean presidentFlag = false;

    public List<Team> intentDatas;

    private User user;

    private Intent intent;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        immersiveStatusBar();
        //接收用户的登录信息user
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        initView();
        requestData();
        initListener();
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
    /**
     * 初始化数据
     */
    private void initView() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);


        teamMessages.clear();


        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources( android.R.color.holo_red_light, android.R.color.holo_orange_dark,android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景

        teamNewsListView = (ListView) findViewById(R.id.list);

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
        titleName.setText("大学社团列表");
        teamAdapter = new TeamAdapter(TeamActivity.this, teamMessages);
        teamNewsListView.setAdapter(teamAdapter);
    }
    /**
     * 设置监听
     */
    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        iv_reutrn.setOnClickListener(this);

        teamNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //新闻item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(intentDatas != null ){
                    L.v(TAG,"数据获取完成");

                    Team teams = intentDatas.get(position);  //获取当前item的bean
                    Intent intent = new Intent(TeamActivity.this, TeamInformationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("teams",teams);
                    bundle.putSerializable("user",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 获取全部社团
     */
    private void requestData() {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamUrl + ConstantUrl.getTeam_interface)
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
            T.testShowShort(TeamActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {
                T.testShowShort(TeamActivity.this, "当前无社团");
            } else {

                T.testShowShort(TeamActivity.this, "所有社团获取成功");
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
