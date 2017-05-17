package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.TaskMemberRequest;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by MMZB-YY on 2017/5/11.
 * 活动详情页
 */

public class TaskInformationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TaskInformationActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;
    private TeamTask teamTask;


    private ImageView team_image;  //社团logo

    private TextView task_title;  //活动名称
    private TextView task_responsible; //活动负责人
    private TextView task_create_time;  //社团创建时间
    private TextView task_state;  //活动状态
    private TextView task_number;  //活动人数   5/50
    private TextView task_content;  //活动内容

    private LinearLayout taskContent_ll;
    private LinearLayout taskSummary_ll;


    private Button btn_add_task;  //参加活动


    private ProgressDialog dialog;

    private User user;

    private String taskState;

    //请求理由
    private String TaskMemberRequestReason;

    private String jsonString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_task);
        immersiveStatusBar();
        initView();
        //获取intent传值
        Intent intent = getIntent();
        teamTask = (TeamTask) intent.getSerializableExtra("teamTask");  //活动列表跳转
        user = (User) intent.getSerializableExtra("user");
        taskState = intent.getStringExtra("taskState");
        //通过网络请求获取用户信息



        initDatas();

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
    private void initView() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);


        team_image = (ImageView) findViewById(R.id.team_image);


        task_title = (TextView) findViewById(R.id.task_title);
        task_responsible = (TextView) findViewById(R.id.task_responsible);
        task_create_time = (TextView) findViewById(R.id.task_create_time);
        task_state = (TextView) findViewById(R.id.task_state);
        task_number = (TextView) findViewById(R.id.task_number);
        task_content = (TextView) findViewById(R.id.task_content);

        taskContent_ll = (LinearLayout) findViewById(R.id.task_content_ll);
        taskSummary_ll= (LinearLayout) findViewById(R.id.task_summary_ll);


        btn_add_task = (Button) findViewById(R.id.btn_add_task);  //申请加入按钮
    }


    private void initDatas() {
        titleName.setText("活动信息");
        if(teamTask != null){
            task_title.setText(teamTask.getTaskTitle());
            task_responsible.setText(teamTask.getTaskResponsibleId().getUserId().getUserNickname());
            task_create_time.setText(teamTask.getTaskCreateTime());
            task_state.setText(teamTask.getTaskState());
            if(teamTask.getTaskState().equals("正在报名")){
                task_state.setTextColor(Color.parseColor("#dc5d0a"));
                taskContent_ll.setVisibility(View.VISIBLE);
                taskSummary_ll.setVisibility(View.GONE);
            }else if(teamTask.getTaskState().equals("正在进行")){
                task_state.setTextColor(Color.parseColor("#19fa28"));
                taskContent_ll.setVisibility(View.VISIBLE);
                taskSummary_ll.setVisibility(View.GONE);
                btn_add_task.setText("活动正在进行");
                btn_add_task.setTextColor(Color.parseColor("#19fa28"));
                btn_add_task.setEnabled(false);
            }else if(teamTask.getTaskState().equals("已结束")){
                task_state.setTextColor(Color.parseColor("#d81e06"));
                taskContent_ll.setVisibility(View.GONE);
                taskSummary_ll.setVisibility(View.VISIBLE);
                btn_add_task.setText("活动已结束");
                btn_add_task.setTextColor(Color.parseColor("#d81e06"));
                btn_add_task.setEnabled(false);
            }


            task_content.setText(teamTask.getTaskContent());


            //通过网络获取社团logo
            obtainTeamImage(teamTask.getTaskResponsibleId().getTeamId().getTeamImage());
            getTeamTaskCount(teamTask.getTaskId());

        }


    }

    /**
     * 获取活动报名人数
     * @param taskId
     */
    private void getTeamTaskCount(Integer taskId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.taskMemberUrl+ConstantUrl.getTeamTaskCount_interface)
                .addParams("taskId", taskId.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e(TAG, "onError:" + e.getMessage());
                        T.testShowShort(TaskInformationActivity.this, Constant.mProgressDialog_error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.e(TAG, "onResponse:" + response);
                        if (response.equals("")) {
                            T.testShowShort(TaskInformationActivity.this, Constant.mMessage_error);
                        } else {
                            task_number.setText(response+"/"+teamTask.getTaskMaxNumber().toString());

                        }
                    }
                });
    }


    private void initListener() {
        //监听事件
        iv_reutrn.setOnClickListener(this);

         if(taskState.equals("responsible")){  //负责人查看
            btn_add_task.setVisibility(View.INVISIBLE);
        }else {
            btn_add_task.setOnClickListener(this);
            btn_add_task.setVisibility(View.VISIBLE);
        }


    }


    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainTeamImage(String url) {

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
                        team_image.setImageBitmap(bitmap);
                    }
                });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:
                finish();
                break;
            case R.id.btn_add_task:
                //报名活动
                if(teamTask.getTaskState().equals("正在报名")){
                    showEditDialog(TaskInformationActivity.this,"输入想说的话，完成报名~");
                }else if(teamTask.getTaskState().equals("正在进行")){
                    T.testShowShort(TaskInformationActivity.this,"活动正在进行，不能报名~");
                }else if(teamTask.getTaskState().equals("已结束")){
                    T.testShowShort(TaskInformationActivity.this,"活动已经结束，不能报名~");
                }

                break;
            default:
                break;
        }
    }





    /**
     * 弹出输入提示框
     */
    private void showEditDialog(final Context context, String dialogName) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_name_tv = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name_tv.setText(dialogName);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                TaskMemberRequestReason = userInput.getText().toString();
                                requestAddTeam();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * 申请加入社团的网络请求
     */
    private void requestAddTeam() {
        L.v(TAG,"申请加入社团");
        dialog = ProgressDialog.show(this, "提示", Constant.mProgressDialog_success, true, true);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonString = new Gson().toJson(new TaskMemberRequest(user, teamTask.getTaskId(), TaskMemberRequestReason,"insert"));

                //发起添加请求
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.taskMemberUrl + ConstantUrl.addTaskMemberRequest_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonString)
                        .build()
                        .execute(new MyRequestAddFriendCallback());
            }
        }, 600);//2秒后执行Runnable中的run方法



    }
    public class MyRequestAddFriendCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dialog.cancel();//关闭圆形进度条
            e.printStackTrace();
            L.v(TAG, "请求失败");
            T.testShowShort(TaskInformationActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(TaskInformationActivity.this, Constant.mMessage_success);

            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(TaskInformationActivity.this, HomeActivity.class));  //跳转回主页面


        }
    }




}
