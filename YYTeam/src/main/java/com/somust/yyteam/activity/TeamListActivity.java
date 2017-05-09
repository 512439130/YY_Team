package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.TeamAdapter;
import com.somust.yyteam.adapter.TeamListAdapter;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamMemberMessage;
import com.somust.yyteam.bean.TeamMessage;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
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
 * 我的大学社团列表
 */

public class TeamListActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = "TeamListActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;


    private RefreshLayout swipeLayout;


    private ListView teamNewsListView;
    private TeamListAdapter teamListAdapter;





    private Bitmap[] teamBitmaps;




    private User user;
    private List<TeamMember> teamMembers;

    private List<TeamMemberMessage> teamMemberMessages  = new ArrayList<TeamMemberMessage>();

    private Intent intent;

    private boolean teamFlag = false;
    private boolean presidentFlag = false;


    private Handler teamMemberHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();


            if (bundle.getString("team_success") == "team_success") {  //社团图片成功获取
                for(int i = 0;i<teamMemberMessages.size();i++){
                    teamMemberMessages.get(i).setTeamImage(teamBitmaps[i]);
                }
                teamFlag = true;
            }


            if(teamFlag){   //2张图片都请求成功时

                //请求是否有更新（在这个时间段后）
                teamListAdapter.notifyDataSetChanged();

            }

        }
    };











    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        //接收用户的登录信息user
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        teamMembers = (List<TeamMember>) intent.getSerializableExtra("teamMembers");


        initView();
        obtainTeamInfo(user.getUserId().toString());
        initListener();
    }

    /**
     * 初始化数据
     */
    private void initView() {
        teamMemberMessages.clear();
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);


        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources( android.R.color.holo_red_light, android.R.color.holo_orange_dark,android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景
        teamNewsListView = (ListView) findViewById(R.id.list);
    }




    /**
     * 添加数据
     */
    private void initDatas() {
        titleName.setText("我的社团列表");
        teamListAdapter = new TeamListAdapter(TeamListActivity.this, teamMemberMessages);
        teamNewsListView.setAdapter(teamListAdapter);
    }
    /**
     * 设置监听
     */
    private void initListener() {
        swipeLayout.setOnRefreshListener(this);
        iv_reutrn.setOnClickListener(this);

        teamNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //新闻item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    L.v(TAG,"数据获取完成");
                    TeamMember teamMember = teamMembers.get(position);  //获取当前item的bean

                    Intent intent = new Intent(TeamListActivity.this, TeamHomeActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("teamMember", teamMember);
                    startActivity(intent);
                    finish();

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
     * 获取我的所有社团
     *
     * @param userId
     */
    private void obtainTeamInfo(String userId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamUrl + ConstantUrl.getMyTeam_interface)
                .addParams("userId", userId)
                .build()
                .execute(new MyTeamIdCallback());
    }


    public class MyTeamIdCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(TeamListActivity.this, Constant.mMessage_error);
            } else {
                Gson gson = new GsonBuilder().setDateFormat(Constant.formatType).create();
                teamMembers = gson.fromJson(response, new TypeToken<List<TeamMember>>() {
                }.getType());

                teamBitmaps= new Bitmap[teamMembers.size()];

                for (int i = 0; i < teamMembers.size(); i++) {
                    TeamMemberMessage teamMemberMessage = new TeamMemberMessage();

                    teamMemberMessage.setTeamMemberId(teamMembers.get(i).getTeamMemberId());
                    teamMemberMessage.setTeamId(teamMembers.get(i).getTeamId());
                    teamMemberMessage.setUserId(teamMembers.get(i).getUserId());
                    teamMemberMessage.setTeamMemberJoinTime(teamMembers.get(i).getTeamMemberJoinTime());
                    teamMemberMessage.setTeamMemberPosition(teamMembers.get(i).getTeamMemberPosition());

                    teamMemberMessages.add(teamMemberMessage);
                    //通过网络请求获取图片
                    obtainTeamImage(teamMembers.get(i).getTeamId().getTeamImage(), i);
                    //teamMemberMessage.setTeamImage();

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
                        UpdateUi(teamMemberHandler, "team_success", "team_success");
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

                teamMemberMessages.clear();
                obtainTeamInfo(user.getUserId().toString());
                swipeLayout.setRefreshing(false);
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
