package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.utils.photo.BigPhotoUtils;
import com.somust.yyteam.utils.photo.PhotoUtils;
import com.somust.yyteam.view.NumberAddSubView;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;


/**
 * Created by 13160677911 on 2017-5-12.
 * 发布社团任务
 */

public class CreateTeamTaskActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CreateTeamTaskActivity:";

    private TeamTask teamTask = new TeamTask();


    private ImageView mImg_Background;   //动态背景


    //创建者
    private TextView createTaskRelease;
    //创建时间
    private TextView createTaskTime;
    //任务负责人
    private TextView createTaskResponsible;
    //任务标题
    private EditText taskTitle;
    //任务内容
    private TextView taskContent;
    //任务人数
    private NumberAddSubView taskMaxNumber;


    //创建button
    private Button btn_createTaskNews;
    private String jsonString;

    private ProgressDialog dialog;


    private User taskReleaseUser;
    private Integer taskResponsibleUserId;
    private Integer taskTeamId;

    private TeamMember teamMember;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team_task);

        immersiveStatusBar();
        Intent intent = getIntent();
        taskReleaseUser = (User) intent.getSerializableExtra("taskReleaseUser");
        taskResponsibleUserId = (Integer) intent.getSerializableExtra("taskResponsibleUserId");
        taskTeamId = (Integer) intent.getSerializableExtra("taskTeamId");

        L.v(TAG,taskResponsibleUserId.toString());
        initViews();
        dynamaicBackground();

        initListeners();

        requestDatas(taskResponsibleUserId.toString(),taskTeamId.toString());  //获取负责人信息


    }

    /**
     * 获取信息
     */
    private void requestDatas(String userId,String teamId) {
        final String url = ConstantUrl.teamMemberUrl + ConstantUrl.getTeamMemberByResponsible_interface;  //需要更换
        OkHttpUtils
                .post()
                .url(url)
                .addParams("userId",userId)
                .addParams("teamId",teamId)
                .build()
                .execute(new MyStringCallback());

    }
    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(CreateTeamTaskActivity.this, "所有用户获取失败");
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(CreateTeamTaskActivity.this, "所有用户为空");
            } else {
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                teamMember = gson.fromJson(response, TeamMember.class);
                L.v(TAG, teamMember.toString());

                initDatas();

            }
        }
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
     * 动态背景
     */
    private void dynamaicBackground() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(CreateTeamTaskActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }


    private void initViews() {
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);

        createTaskRelease = (TextView) findViewById(R.id.create_task_release_tv);
        createTaskTime = (TextView) findViewById(R.id.create_task_time_tv);
        createTaskResponsible = (TextView) findViewById(R.id.create_task_responsible_tv);
        taskTitle = (EditText) findViewById(R.id.create_task_title_et);
        taskContent = (EditText) findViewById(R.id.create_task_content_et);
        taskMaxNumber = (NumberAddSubView) findViewById(R.id.task_max_number);
        btn_createTaskNews = (Button) findViewById(R.id.create_task_btn);


        //活动人数
        taskMaxNumber.setOnButtonClickListenter(new MyAddNumberOnButtonClickListenter());

    }

    /**
     * 活动人数监听
     */
    class MyAddNumberOnButtonClickListenter implements NumberAddSubView.OnButtonClickListenter{

        @Override
        public void onButtonAddClick(View view, int value) {
            L.v(TAG, "活动人数=" + value);
            teamTask.setTaskMaxNumber(value);
        }

        @Override
        public void onButtonSubClick(View view, int value) {
            L.v(TAG, "活动人数=" + value);
            teamTask.setTaskMaxNumber(value);
        }
    }

    private void initListeners() {
        btn_createTaskNews.setOnClickListener(this);
    }

    private void initDatas() {
        //获取创建时间
        Date nowTime = new Date();
        String createTime = DateUtil.dateToString(nowTime, Constant.formatType);  //创建任务的时间
        teamTask.setTaskCreateTime(createTime);
        teamTask.setTaskReleaseId(taskReleaseUser.getUserId());
        teamTask.setTaskResponsibleId(teamMember);
        teamTask.setTaskState("正在报名");

        createTaskRelease.setText(taskReleaseUser.getUserNickname());  //创建者
        createTaskTime.setText(createTime);  //创建时间
        createTaskResponsible.setText(teamMember.getUserId().getUserNickname());  //负责人

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_task_btn:   //发布社团任务
                //发送创建社团请求
                //先获取et的值，再判空，再请求
                obtainEtValue();
                break;
            default:
                break;
        }
    }

    /**
     * 获取et的值，如果不为空则执行网络请求
     */
    private void obtainEtValue() {
        String teamTaskTitle = taskTitle.getText().toString().trim();
        String teamTaskContent = taskContent.getText().toString().trim();
        if (TextUtils.isEmpty(teamTaskTitle)) {
            T.testShowShort(CreateTeamTaskActivity.this, "任务标题不能为空");
        } else if (TextUtils.isEmpty(teamTaskContent)) {
            T.testShowShort(CreateTeamTaskActivity.this, "任务内容不能为空");
        } else if (!TextUtils.isEmpty(teamTaskTitle) && !TextUtils.isEmpty(teamTaskContent)) {
            teamTask.setTaskTitle(teamTaskTitle);
            teamTask.setTaskContent(teamTaskContent);
            CreateTeamNewsRequest(teamTask);
        }

    }


    public void CreateTeamNewsRequest(TeamTask teamTask) {
        jsonString = new Gson().toJson(teamTask);

        dialog = ProgressDialog.show(CreateTeamTaskActivity.this, "提示", Constant.mProgressDialog_success, true, true);
        //根据图片位置发送上传文件的网络请求
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.teamTaskUrl + ConstantUrl.addTeamTask_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonString)
                        .build()
                        .execute(new MyRequestCreateTaskCallback());
            }
        }, 2000);
        //发起添加请求

    }

    public class MyRequestCreateTaskCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dialog.cancel();//关闭圆形进度条
            e.printStackTrace();
            L.v(TAG, "请求失败");
            T.testShowShort(CreateTeamTaskActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(CreateTeamTaskActivity.this, Constant.mMessage_success);

            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(CreateTeamTaskActivity.this, TeamHomeActivity.class));  //跳转回主页面

        }
    }
}
