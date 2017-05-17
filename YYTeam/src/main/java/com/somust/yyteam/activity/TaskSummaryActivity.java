package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.utils.photo.PhotoUtils;
import com.somust.yyteam.view.ImageViewPlus;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;


/**
 * Created by 13160677911 on 2017-5-13.
 * 活动总结
 */

public class TaskSummaryActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TaskSummaryActivity:";

    private ImageView iv_reutrn;
    private TextView titleName;


    private EditText et_taskSummary;
    private Button  btn_taskSummary;

    private ProgressDialog progressDialog;


    private String taskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_summary);
        immersiveStatusBar();
        Intent intent = getIntent();
        taskId = (String) intent.getSerializableExtra("taskId");

        initViews();
        initListeners();
        initDatas();

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



    private void initViews() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);

        et_taskSummary = (EditText) findViewById(R.id.task_summary_et);
        btn_taskSummary = (Button) findViewById(R.id.task_summary_btn);


    }

    private void initListeners() {
        iv_reutrn.setOnClickListener(this);
        btn_taskSummary.setOnClickListener(this);
    }

    private void initDatas() {
        titleName.setText("活动总结");
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:
                finish();
                break;
            case R.id.task_summary_btn:   //活动总结
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
        String taskSummary = et_taskSummary.getText().toString().trim();
        if(TextUtils.isEmpty(taskSummary)){
            T.testShowShort(TaskSummaryActivity.this,"活动总结不能为空");
        }else{
            showNullDialog(TaskSummaryActivity.this, "是否完成总结？",taskSummary);
        }

    }

    /**
     * 弹出无输入确认框
     */
    public void showNullDialog(final Context context, String dialogName, final String taskSummary) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_null_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        TextView dialog_name = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name.setText(dialogName);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog = ProgressDialog.show(context, "提示", Constant.mProgressDialog_message, true, true);

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        TaskSummaryRequest(taskId,taskSummary);
                                    }
                                }, 400);

                            }
                        })
                .setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 开启任务的网络请求
     * @param taskId
     * @param taskSummary
     */
    private void TaskSummaryRequest(String taskId,String taskSummary) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamTaskUrl + ConstantUrl.setTeamTaskSummary_interface)
                .addParams("taskId", taskId)
                .addParams("taskSummary", taskSummary)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        e.printStackTrace();
                        L.v(TAG, "请求失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        L.v(response);
                        L.v(TAG, "请求成功");
                        //跳回社团主界面
                        progressDialog.cancel();
                        startActivity(new Intent(TaskSummaryActivity.this, TeamHomeActivity.class));  //跳转回主页面

                    }
                });
    }


}
