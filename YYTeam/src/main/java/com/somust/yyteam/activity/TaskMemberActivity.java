package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.TaskMemberAdapter;
import com.somust.yyteam.adapter.TeamMemberAdapter;
import com.somust.yyteam.bean.PersonBean;
import com.somust.yyteam.bean.TaskMember;
import com.somust.yyteam.bean.TeamTask;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.SideBar.PinyinComparator;
import com.somust.yyteam.utils.SideBar.PinyinUtils;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.SideBar;
import com.somust.yyteam.view.refreshview.RefreshLayout;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * Created by 13160677911 on 2016-12-4.
 * 活动成员列表
 */

public class TaskMemberActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String TAG = "TaskMemberActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;
    private ListView taskListView;


    //SideBar相关
    private List<PersonBean> personBeenList;

    private TaskMemberAdapter taskMemberAdapter;


    // private User user;    //登录用户信息
    private List<TaskMember> taskMembers;   //登录用户的好友信息
    private Bitmap[] portraitBitmaps;



    private RefreshLayout swipeLayout;
    private View header;

    private User user;    //登录用户信息


    private Integer taskId = 0;

    private TeamTask teamTask;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_member);
        immersiveStatusBar();
        initView();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        teamTask = (TeamTask) intent.getSerializableExtra("teamTask");
        taskId = teamTask.getTaskId();


        //发送网络请求，获取社团成员列表
        obtainTeamMemberList(taskId.toString());
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


        taskListView = (ListView) findViewById(R.id.id_lv_task_member);

        //头部布局
        header = getLayoutInflater().inflate(R.layout.task_member_header, null);
        taskListView.addHeaderView(header);


        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);

        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_dark, android.R.color.holo_orange_light, android.R.color.holo_green_light);//设置刷新圆圈颜色变化
        swipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);  //设置刷新圆圈背景

    }

    /**
     * 获取好友列表
     *
     * @param taskId 任务编号
     */
    private void obtainTeamMemberList(String taskId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.taskMemberUrl + ConstantUrl.obtainTaskMember_interface)
                .addParams("taskId", taskId)
                .build()
                .execute(new MyTeamMemberCallback());

    }


    /**
     * 回调
     */
    public class MyTeamMemberCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(TaskMemberActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {

                T.testShowShort(TaskMemberActivity.this, "当前活动无成员");

            } else {
                T.testShowShort(TaskMemberActivity.this, "活动成员获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new GsonBuilder().setDateFormat(Constant.formatType).create();
                taskMembers = gson.fromJson(response, new TypeToken<List<TaskMember>>() {
                }.getType());


                portraitBitmaps = new Bitmap[taskMembers.size()];
                for (int i = 0; i < taskMembers.size(); i++) {
                    obtainImage(taskMembers.get(i).getUserId().getUserImage(), i);
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
                        L.v("TAG", "onResponse：complete");
                        portraitBitmaps[i] = bitmap;
                        //网络请求成功后
                        initSidebarAndView();

                    }
                });
    }

    private void initDatas() {
        titleName.setText("活动成员列表");
    }


    private void initListener() {

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    //由于字幕排序引起item不对应list(不能使用排序前的friendlist，必须使用排序后的personBeanList)
                    String userId = personBeenList.get(position - 1).getPhone();
                    String userNickname = personBeenList.get(position - 1).getName();
                    L.e(TAG, userId);
                    L.e(TAG, userNickname);
                    //打开个人信息界面（个人信息界面包含发送消息）
                    Intent intent = new Intent(TaskMemberActivity.this, PersionInformationActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("userNickname", userNickname);
                    intent.putExtra("openState", "friend");  //好友
                    startActivity(intent);
                }
            }
        });

        //设置刷新监听
        swipeLayout.setOnRefreshListener(this);
        iv_reutrn.setOnClickListener(this);

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

    private void initSidebarAndView() {

        String[] names = new String[taskMembers.size()];
        String[] phones = new String[taskMembers.size()];
        for (int i = 0; i < taskMembers.size(); i++) {
            names[i] = taskMembers.get(i).getUserId().getUserNickname();
            phones[i] = taskMembers.get(i).getUserId().getUserPhone();
        }
        personBeenList = getData(names, phones);  //真实数据

        // 数据在放在adapter之前需要排序
        Collections.sort(personBeenList, new PinyinComparator());

        //对加入时间排序

        taskMemberAdapter = new TaskMemberAdapter(TaskMemberActivity.this, personBeenList);

        taskListView.setAdapter(taskMemberAdapter);
    }


    /**
     * 模拟数据获取（通过资源文件）
     * <p>
     * name : 昵称
     * image :
     *
     * @return
     */
    private List<PersonBean> getData(String[] names, String[] phones) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < names.length; i++) {
            String pinyin = PinyinUtils.getPingYin(names[i]);
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(names[i]);
            person.setImage(portraitBitmaps[i]);
            person.setPinYin(pinyin);
            person.setPhone(phones[i]);
            person.setTaskMemberJoinTime(taskMembers.get(i).getTaskMemberJoinTime());
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setFirstPinYin(Fpinyin);
            } else {
                person.setFirstPinYin("#");
            }

            listarray.add(person);
        }
        return listarray;

    }


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        swipeLayout.postDelayed(new Runnable() {

            @Override
            public void run() {
                // 更新数据  更新完后调用该方法结束刷新

                if (personBeenList != null) {
                    personBeenList.clear();
                }
                obtainTeamMemberList(taskId.toString());
                //请求是否有更新（在这个时间段后）
                swipeLayout.setRefreshing(false);
            }
        }, 1200);
    }


}
