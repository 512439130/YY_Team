package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.ExammineTeamTaskAdapter;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.bean.TeamTaskMessage;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
/**
 * Created by DELL on 2016/3/14.
 */

/**
 * 社团任务列表
 */
public class ExamineTeamTaskActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,ExammineTeamTaskAdapter.ExamineTaskCallback{
    private static final String TAG = "ExamineTeamTaskActivity:";

    public static ExamineTeamTaskActivity instance = null;//单例模式

    public static ExamineTeamTaskActivity getInstance() {
        if (instance == null) {
            instance = new ExamineTeamTaskActivity();
        }
        return instance;
    }

    private RefreshLayout swipeLayout;
    private View header;

    private ListView teamTasksListView;
    private ExammineTeamTaskAdapter exammineTeamTaskAdapter;

    public List<TeamTask> teamTasks = new ArrayList<>();   //存放网络接受的数据
    public List<TeamTaskMessage> teamTaskMessages = new ArrayList<>();   //将网络接收的数据装换为相应bean


    private Bitmap[] teamBitmaps;

    private boolean teamFlag = false;


    private User user;
    private Team team;
    private Integer teamId = 0;

    private Intent intent;

    private TextView noDataTextView;  //无数据展示内容


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine_team_task);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        team = (Team) intent.getSerializableExtra("team");
        teamId = team.getTeamId();  //获取社团Id

        initView();
        requestData();
        initListener();
    }


    /**
     * 初始化数据
     */
    private void initView() {
        teamTaskMessages.clear();

        //头部
        header = getLayoutInflater().inflate(R.layout.exammine_team_task_header, null);

        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);

        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_dark, android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景
        teamTasksListView = (ListView) findViewById(R.id.list);
        teamTasksListView.addHeaderView(header);
        noDataTextView = (TextView) findViewById(R.id.null_data_tv);


    }


    private Handler teamTaskHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();


            if (bundle.getString("team_success") == "team_success") {  //社团图片成功获取
                for (int i = 0; i < teamTaskMessages.size(); i++) {
                    teamTaskMessages.get(i).setTeamImage(teamBitmaps[i]);

                }
                teamFlag = true;
            }
            if (teamFlag) {   //2张图片都请求成功时
                //请求是否有更新（在这个时间段后）
                exammineTeamTaskAdapter.notifyDataSetChanged();
            }

        }
    };

    /**
     * 添加数据
     */
    private void initDatas() {

        exammineTeamTaskAdapter = new ExammineTeamTaskAdapter(ExamineTeamTaskActivity.this, teamTaskMessages,this);
        teamTasksListView.setAdapter(exammineTeamTaskAdapter);
    }

    /**
     * 设置监听
     */
    private void initListener() {
        swipeLayout.setOnRefreshListener(this);

        teamTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  //任务item的点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (teamTasks != null) {
                    L.v(TAG, "数据获取完成");

                    if (position != 0) {
                        TeamTask teamTask = teamTasks.get(position - 1);  //获取当前item的bean
                        Intent intent = new Intent(ExamineTeamTaskActivity.this, TaskInformationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("teamTask", teamTask);
                        bundle.putSerializable("user", user);
                        bundle.putString("taskState","responsible");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }


                }
            }
        });
    }
    @Override
    public void ExamineTaskClick(View view) {
        //弹出确认对话框
        showNullDialog(view,ExamineTeamTaskActivity.this, "是否通过审核？");
    }

    /**
     * 弹出无输入确认框
     */
    public void showNullDialog(final View view, Context context, String dialogName) {
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
                                endTaskRequest(teamTasks.get((Integer) view.getTag()).getTaskId().toString(),"已结束");
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
     * @param taskState
     */
    private void endTaskRequest(String taskId,String taskState) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamTaskUrl + ConstantUrl.auditTeamTaskSummary_interface)
                .addParams("taskId", taskId)
                .addParams("taskState", taskState)
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
                        //刷新界面
                        teamTaskMessages.clear();
                        requestData();
                        swipeLayout.setRefreshing(false);

                    }
                });
    }







    /**
     * 获取全部活动
     */
    private void requestData() {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamTaskUrl + ConstantUrl.getTeamMemberTaskByTeamId_interface)
                .addParams("teamId", teamId.toString())
                .build()
                .execute(new MyTeamTaskRequestCallback());
    }




    /**
     * 请求回调
     */
    public class MyTeamTaskRequestCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(ExamineTeamTaskActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {
                T.testShowShort(ExamineTeamTaskActivity.this, "当前无活动");
                noDataTextView.setVisibility(View.VISIBLE);
                noDataTextView.setText("当前无任务分配");
            } else {
                noDataTextView.setVisibility(View.INVISIBLE);
                T.testShowShort(ExamineTeamTaskActivity.this, "所有社团活动获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                teamTasks = gson.fromJson(response, new TypeToken<List<TeamTask>>() {
                }.getType());
                DateUtil.TeamTaskSortDate(teamTasks);   //对社团结果排序


                teamBitmaps = new Bitmap[teamTasks.size()];

                for (int i = 0; i < teamTasks.size(); i++) {
                    TeamTaskMessage message = new TeamTaskMessage();
                    message.setTaskId(teamTasks.get(i).getTaskId());
                    message.setTaskTitle(teamTasks.get(i).getTaskTitle());
                    message.setTaskContent(teamTasks.get(i).getTaskContent());
                    message.setTaskReleaseId(teamTasks.get(i).getTaskReleaseId().toString());
                    message.setTeamMemberId(teamTasks.get(i).getTaskResponsibleId().getTeamMemberId().toString());
                    message.setTaskState(teamTasks.get(i).getTaskState());
                    Date date;//转换为只有年月日的日期（Date）
                    String nullTimeDate = null;   //转换为只有年月日的日期（String）
                    try {
                        date = DateUtil.stringToDate(teamTasks.get(i).getTaskCreateTime(), Constant.formatTypeNoTile);
                        nullTimeDate = DateUtil.dateToString(date, Constant.formatTypeNoTile);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    message.setTaskCreateTime(nullTimeDate);
                    message.setTaskMaxNumber(teamTasks.get(i).getTaskMaxNumber().toString());
                    message.setTaskSummary(teamTasks.get(i).getTaskSummary());
                    message.setTeamName(teamTasks.get(i).getTaskResponsibleId().getTeamId().getTeamName());


                    //通过网络请求获取图片
                    obtainTeamImage(teamTasks.get(i).getTaskResponsibleId().getTeamId().getTeamImage(), i);
                    teamTaskMessages.add(message);


                }


            }
            initDatas();

        }
    }


    /**
     * 请求社团logo
     *
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

                        teamBitmaps[i] = bitmap;
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
                requestData();

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
    private void UpdateUi(Handler handler, String key, String value) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }


}
