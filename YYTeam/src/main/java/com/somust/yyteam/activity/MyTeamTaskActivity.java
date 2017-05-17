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
import com.somust.yyteam.adapter.MyTeamTaskAdapter;
import com.somust.yyteam.bean.TaskMember;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.bean.TeamTaskMessage;
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
 * Created by DELL on 2016/3/14.
 */

/**
 * 社团任务列表
 */
public class MyTeamTaskActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MyTeamTaskActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;
    private View header;

    private RefreshLayout swipeLayout;

    private ListView teamNewsListView;
    private MyTeamTaskAdapter myTeamTaskAdapter;

    public List<TaskMember> taskMembers = new ArrayList<>();   //存放网络接受的数据
    public List<TeamTaskMessage> teamTaskMessages = new ArrayList<>();   //将网络接收的数据装换为相应bean


    private Bitmap[] teamBitmaps;

    private boolean teamFlag = false;


    private User user;
    private Intent intent;

    private TextView nullDataTextView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team_task);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        immersiveStatusBar();
        initView();
        requestData(user.getUserId().toString());
        initListener();
    }

    /**
     * 沉浸式状态栏（伪）
     */
    private void immersiveStatusBar() {
        //沉浸式状态栏（伪）
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 初始化数据
     */
    private void initView() {
        teamTaskMessages.clear();

        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);

        //头部
        header = getLayoutInflater().inflate(R.layout.my_team_task_header, null);


        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources( android.R.color.holo_red_light, android.R.color.holo_orange_dark,android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景
        teamNewsListView = (ListView) findViewById(R.id.list);
        teamNewsListView.addHeaderView(header);

        nullDataTextView = (TextView) findViewById(R.id.null_data_tv);

    }


    private Handler teamTaskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.getString("team_success") == "team_success") {  //社团图片成功获取
                for(int i = 0;i<teamTaskMessages.size();i++){
                    teamTaskMessages.get(i).setTeamImage(teamBitmaps[i]);
                }
                teamFlag = true;
            }
            if(teamFlag){   //2张图片都请求成功时
                //请求是否有更新（在这个时间段后）
                myTeamTaskAdapter.notifyDataSetChanged();
            }

        }
    };

    /**
     * 添加数据
     */
    private void initDatas() {
        titleName.setText("我的社团活动");
        myTeamTaskAdapter = new MyTeamTaskAdapter(MyTeamTaskActivity.this, teamTaskMessages);
        teamNewsListView.setAdapter(myTeamTaskAdapter);
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
                if(taskMembers != null ){
                    L.v(TAG,"数据获取完成");
                    if (position != 0) {
                        //因为加头部，必须-1
                        TeamTask teamTask = taskMembers.get(position - 1).getTaskId();  //获取当前item的bean
                        Intent intent = new Intent(MyTeamTaskActivity.this, TaskInformationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("teamTask", teamTask);
                        bundle.putSerializable("user", user);
                        bundle.putString("taskState","register");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            }
        });
    }


    /**
     * 获取全部活动
     */
    private void requestData(String userId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.taskMemberUrl + ConstantUrl.getTaskByUserId_interface)
                .addParams("userId",userId)
                .build()
                .execute(new MyTeamTaskRequestCallback());
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.id_title_back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 请求回调
     */
    public class MyTeamTaskRequestCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(MyTeamTaskActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {


            if (response.equals("[]")) {
                T.testShowShort(MyTeamTaskActivity.this, "当前无参与活动");
                nullDataTextView.setVisibility(View.VISIBLE);
                nullDataTextView.setText("当前无参与活动");

            } else {
                nullDataTextView.setVisibility(View.INVISIBLE);
                T.testShowShort(MyTeamTaskActivity.this, "我的社团活动获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                taskMembers = gson.fromJson(response, new TypeToken<List<TaskMember>>() {
                }.getType());

                DateUtil.TaskMemberSortDate(taskMembers);   //对社团活动结果排序

                teamBitmaps= new Bitmap[taskMembers.size()];


                for (int i = 0; i < taskMembers.size(); i++) {
                    TeamTaskMessage message = new TeamTaskMessage();
                    message.setTaskId(taskMembers.get(i).getTaskId().getTaskId());
                    message.setTaskTitle(taskMembers.get(i).getTaskId().getTaskTitle());
                    message.setTaskContent(taskMembers.get(i).getTaskId().getTaskContent());
                    message.setTaskReleaseId(taskMembers.get(i).getTaskId().getTaskReleaseId().toString());
                    message.setTeamMemberId(taskMembers.get(i).getTaskId().getTaskResponsibleId().getTeamMemberId().toString());
                    message.setTaskState(taskMembers.get(i).getTaskId().getTaskState());
                    message.setTaskCreateTime(taskMembers.get(i).getTaskId().getTaskCreateTime());
                    message.setTaskMaxNumber(taskMembers.get(i).getTaskId().getTaskMaxNumber().toString());
                    message.setTaskSummary(taskMembers.get(i).getTaskId().getTaskSummary());
                    message.setTeamName(taskMembers.get(i).getTaskId().getTaskResponsibleId().getTeamId().getTeamName());
                    message.setTaskResponsibleNickname(taskMembers.get(i).getTaskId().getTaskResponsibleId().getUserId().getUserNickname());

                    //通过网络请求获取图片
                    obtainTeamImage(taskMembers.get(i).getTaskId().getTaskResponsibleId().getTeamId().getTeamImage(), i);
                    teamTaskMessages.add(message);

                }


            }
            initDatas();

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
                        UpdateUi(teamTaskHandler, "team_success", "team_success");
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
                teamTaskMessages.clear();
                requestData(user.getUserId().toString());

                swipeLayout.setRefreshing(false);
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
