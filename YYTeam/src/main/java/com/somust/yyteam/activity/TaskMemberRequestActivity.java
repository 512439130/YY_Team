package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.TaskMemberRequestAdapter;
import com.somust.yyteam.bean.AllTask;
import com.somust.yyteam.bean.TaskMember;
import com.somust.yyteam.bean.TaskMemberRequest;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamTask;
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
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;


/**
 * 报名活动请求Activity
 */
public class TaskMemberRequestActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, TaskMemberRequestAdapter.TaskMemberRequestCallback {
    ImageView returnView;
    TextView titleName;


    private static final String TAG = "TaskMemberRequestActivity:";
    private RefreshLayout refresh_view;
    private ListView taskMemberRequestListview;


    private List<TaskMemberRequest> taskMemberRequests;   //添加好友的请求列表

    /**
     * 保存网络获取的图片集合
     */
    private Bitmap[] portraitBitmaps;

    /**
     * 全部用户的转化数据
     */
    private List<AllTask> allTasks;  //通过 friendRequests portraitBitmaps合并的数据
    private User user;

    private TeamTask teamTask;

    private ProgressDialog dialog;
    /**
     * 搜索结果列表adapter
     */
    private TaskMemberRequestAdapter taskMemberRequestAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_member_request);
        immersiveStatusBar();

        Intent intent = this.getIntent();
        teamTask = (TeamTask) intent.getSerializableExtra("teamTask");
        user = (User) intent.getSerializableExtra("user");

        //获取请求列表（加请求用户的个人信息）
        obtainTeamMemberRequest(teamTask.getTaskId().toString(), "insert");

        initView();
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

    private void initView() {
        returnView = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);
        titleName.setText("活动报名管理");

        //刷新相关初始化
        taskMemberRequestListview = (ListView) findViewById(R.id.request_team_member_list);

        //初始化刷新
        refresh_view = (RefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setColorSchemeResources( android.R.color.holo_red_light, android.R.color.holo_orange_dark,android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        refresh_view.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景

    }

    private void initData() {

        taskMemberRequestAdapter = new TaskMemberRequestAdapter(this, allTasks, this);
        taskMemberRequestListview.setAdapter(taskMemberRequestAdapter);

    }

    private void initListener() {
        returnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refresh_view.setOnRefreshListener(this);
    }

    /**
     * 同意按钮点击事件
     * @param v
     */
    @Override
    public void agreeClick(View v) {
        //改变请求表状态
        UpdateRequestTaskMember(taskMemberRequests.get((Integer) v.getTag()).getRequestId().getUserId().toString(),teamTask.getTaskId().toString(), "agree");
        //加入社团成员
        TaskMember taskMember = new TaskMember();
        taskMember.setUserId(taskMemberRequests.get((Integer) v.getTag()).getRequestId());
        taskMember.setTaskId(teamTask);
        Date nowTime = new Date();
        String time = DateUtil.dateToString(nowTime, Constant.formatType);  //创建社团的时间

        taskMember.setTaskMemberJoinTime(time);
        addTaskMember(taskMember);
    }

    /**
     * 拒绝按钮点击事件
     * @param v
     */
    @Override
    public void refuseClick(View v) {
        //改变请求表状态
        UpdateRequestTaskMember(taskMemberRequests.get((Integer) v.getTag()).getRequestId().getUserId().toString(),teamTask.getTaskId().toString(), "refuse");
    }


    /**
     * 改变报名活动请求状态的网络请求（同意或拒绝）
     */
    private void UpdateRequestTaskMember(final String requestUserId, final String receiveTaskId, final String taskMemberRequestState) {
        L.v(TAG, "同意添加");
        dialog = ProgressDialog.show(this, "提示", Constant.mProgressDialog_success, true, true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //发起同意请求
                OkHttpUtils
                        .post()
                        .url(ConstantUrl.taskMemberUrl + ConstantUrl.updateTaskMemberRequest_interface)
                        .addParams("requestUserId", requestUserId)
                        .addParams("receiveTaskId", receiveTaskId)
                        .addParams("taskMemberRequestState", taskMemberRequestState)
                        .build()
                        .execute(new MyUpdateRequestTeamMemberCallback());
            }
        }, 600);//2秒后执行Runnable中的run方法

    }

    /**
     * 改变报名活动请求状态的网络回调
     */
    public class MyUpdateRequestTeamMemberCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dialog.cancel();//关闭圆形进度条
            e.printStackTrace();
            L.v(TAG, "请求失败");
            T.testShowShort(TaskMemberRequestActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(TaskMemberRequestActivity.this, Constant.mMessage_success);

            //刷新
            if (allTasks != null){
                allTasks.clear();
                obtainTeamMemberRequest(teamTask.getTaskId().toString(), "insert");
                taskMemberRequestAdapter.notifyDataSetChanged();
            }

            //startActivity(new Intent(FriendRequestActivity.this, HomeActivity.class));  //跳转回主页面


        }
    }

    /**
     * 加入社团的网络请求
     *
     */
    private void addTaskMember(TaskMember taskMember) {
        String jsonString = new Gson().toJson(taskMember);
        OkHttpUtils
                .postString()
                .url(ConstantUrl.taskMemberUrl + ConstantUrl.addTask_interface)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(jsonString)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.v(TAG, "请求失败");
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.v(TAG, "请求成功");
                        L.v(response);
                    }
                });
    }





    /**
     * 通过自己receivePhone获取请求列表
     */
    private void obtainTeamMemberRequest(String receiveId, String taskMemberRequestState) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.taskMemberUrl + ConstantUrl.obtainTaskMemberRequest_interface)
                .addParams("receiveId", receiveId)
                .addParams("taskMemberRequestState", taskMemberRequestState)
                .build()
                .execute(new MyTaskMemberRequestBack());
    }


    /**
     * 回调
     */
    public class MyTaskMemberRequestBack extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(TaskMemberRequestActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {
                L.e(TAG,"无好友请求");
            } else {
                L.e(TAG,"社团添加请求获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                taskMemberRequests = gson.fromJson(response, new TypeToken<List<TaskMemberRequest>>() {
                }.getType());

                portraitBitmaps = new Bitmap[taskMemberRequests.size()];
                //获取请求用户的头像
                for (int i = 0; i < taskMemberRequests.size(); i++) {
                    obtainImage(taskMemberRequests.get(i).getRequestId().getUserImage(), i);
                }


            }
        }
    }


    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     * @param i   需要保存在bitmaps的对应位置
     */
    public void obtainImage(String url, final int i) {
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
                        L.v("TAG", "请求图片");
                        portraitBitmaps[i] = bitmap;
                        //网络请求成功后
                        //初始化搜索结果数据
                        allTasks = new ArrayList<>();
                        Transformation(allTasks, portraitBitmaps);

                        initData();
                    }
                });
    }


    /**
     * 转换
     */
    public void Transformation(List<AllTask> allTasks, Bitmap[] portraitBitmaps) {
        for (int i = 0; i < portraitBitmaps.length; i++) {
            AllTask allTask = new AllTask();
            allTask.setUserId(taskMemberRequests.get(i).getRequestId().getUserId().toString());
            allTask.setUserPhone(taskMemberRequests.get(i).getRequestId().getUserPhone());
            allTask.setUserNickname(taskMemberRequests.get(i).getRequestId().getUserNickname());
            allTask.setUserPassword(taskMemberRequests.get(i).getRequestId().getUserPassword());
            allTask.setUserSex(taskMemberRequests.get(i).getRequestId().getUserSex());
            allTask.setUserToken(taskMemberRequests.get(i).getRequestId().getUserToken());
            allTask.setTaskMemberRequestReason(taskMemberRequests.get(i).getTaskMemberRequestReason());
            allTask.setUserImage(portraitBitmaps[i]);
            allTasks.add(allTask);
        }
        L.v(TAG, this.allTasks.toString());

    }


    /**
     * 下拉刷新的回调
     */
    @Override
    public void onRefresh() {
        refresh_view.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新
                obtainTeamMemberRequest(teamTask.getTaskId().toString(), "insert");
                if (allTasks != null){
                    allTasks.clear();
                    obtainTeamMemberRequest(teamTask.getTaskId().toString(), "insert");
                    refresh_view.setRefreshing(false);
                    taskMemberRequestAdapter.notifyDataSetChanged();
                }else {
                    refresh_view.setRefreshing(false);
                }
            }
        }, 1200);
    }


}
