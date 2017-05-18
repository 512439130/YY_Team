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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamFriend;
import com.somust.yyteam.bean.TeamMemberRequest;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by MMZB-YY on 2017/4/11.
 * 个人信息页
 */

public class TeamInformationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TeamInformationActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;
    private TeamNews teamNews;
    private Team team;


    private ImageView team_image;  //社团logo
    private ImageView team_type;   //社团类型
    private ImageView team_president_image;  //社团创建人头像

    private TextView team_name;  //社团名称
    private TextView team_president; //社团创建人
    private TextView team_time;  //社团创建时间
    private TextView team_introduce;  //社团简介

    private Button btn_add_team;  //加入社团

    private String openTeamState;  //是否加入社团


    private ProgressDialog dialog;

    private User user;

    private List<TeamFriend> friendlist;   //登录用户的好友信息


    //请求理由
    private String TeamMemberRequestReason;

    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_team);
        immersiveStatusBar();
        initView();
        //获取intent传值
        //接收Intent传值
        Intent intent = this.getIntent();
        teamNews = (TeamNews) intent.getSerializableExtra("teamNews");  //新闻列表跳转
        team = (Team) intent.getSerializableExtra("teams");  //社团列表跳转
        user = (User) intent.getSerializableExtra("user");

        openTeamState = intent.getStringExtra("openTeamState");
        obtainFriendList(user.getUserPhone());
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
        team_type = (ImageView) findViewById(R.id.team_type);
        team_president_image = (ImageView) findViewById(R.id.team_president_image);

        team_name = (TextView) findViewById(R.id.team_name);
        team_president = (TextView) findViewById(R.id.team_president);
        team_time = (TextView) findViewById(R.id.team_time);
        team_introduce = (TextView) findViewById(R.id.team_introduce);

        btn_add_team = (Button) findViewById(R.id.btn_add_team);  //申请加入按钮
    }


    private void initDatas() {
        titleName.setText("社团信息");



        if (teamNews != null) {
            //社团类型转换图标
            if (teamNews.getTeamId().getTeamType().equals("学习")) {
                team_type.setBackgroundResource(R.mipmap.ic_team_study);
            } else if (teamNews.getTeamId().getTeamType().equals("公益")) {
                team_type.setBackgroundResource(R.mipmap.ic_team_welfare);
            } else if (teamNews.getTeamId().getTeamType().equals("技术")) {
                team_type.setBackgroundResource(R.mipmap.ic_team_technology);
            }

            team_name.setText(teamNews.getTeamId().getTeamName());
            team_president.setText(teamNews.getTeamId().getTeamPresident().getUserNickname());
            team_time.setText(teamNews.getTeamId().getTeamTime());
            team_introduce.setText(teamNews.getTeamId().getTeamIntroduce());

            //通过网络获取社团logo
            obtainTeamImage(teamNews.getTeamId().getTeamImage());
            //获取社长头像
            obtainPresidentImage(teamNews.getTeamId().getTeamPresident().getUserImage());
        }
        if (team != null) {
            //社团类型转换图标
            if (team.getTeamType().equals("学习")) {
                team_type.setBackgroundResource(R.mipmap.ic_team_study);
            } else if (team.getTeamType().equals("公益")) {
                team_type.setBackgroundResource(R.mipmap.ic_team_welfare);
            } else if (team.getTeamType().equals("技术")) {
                team_type.setBackgroundResource(R.mipmap.ic_team_technology);
            }

            team_name.setText(team.getTeamName());
            team_president.setText(team.getTeamPresident().getUserNickname());
            team_time.setText(team.getTeamTime());
            team_introduce.setText(team.getTeamIntroduce());

            //通过网络获取社团logo
            obtainTeamImage(team.getTeamImage());
            //获取社长头像
            obtainPresidentImage(team.getTeamPresident().getUserImage());
        }

    }

    private void initListener() {
        //监听事件
        iv_reutrn.setOnClickListener(this);
        team_president_image.setOnClickListener(this);
        if(openTeamState.equals("team_member")){  //社团成员
            btn_add_team.setEnabled(false);
            btn_add_team.setText("您已经加入社团");
            btn_add_team.setTextColor(Color.RED);
        }else if(openTeamState.equals("no_team_member")){//非社团成员
            btn_add_team.setEnabled(true);
            btn_add_team.setOnClickListener(this);
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

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainPresidentImage(String url) {

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
                        team_president_image.setImageBitmap(bitmap);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:
                finish();
                break;
            case R.id.team_president_image:
                //社长信息（Activity）
                Intent intent = new Intent(TeamInformationActivity.this, PersionInformationActivity.class);
                int flag = -1;
                //如果是好友，则不显示添加好友，如果不是好友，则显示添加好友按钮有
                if (friendlist != null) {
                    for (int i = 0; i < friendlist.size(); i++) {
                        if (teamNews.getTeamId().getTeamPresident().getUserPhone().equals(friendlist.get(i).getFriendPhone().getUserPhone()) || teamNews.getTeamId().getTeamPresident().getUserPhone().equals(user.getUserPhone())) {  //模糊查询
                            flag = 0; //相同
                            break;
                        } else {  //模糊查询
                            flag = 1; //不同
                        }
                    }
                }
                if (flag == -1) {
                    intent.putExtra("openState", "stranger");  //陌生人
                }
                if (flag == 0) {  //相同
                    intent.putExtra("openState", "friend");  //好友
                } else if (flag == 1) {
                    intent.putExtra("openState", "stranger");  //陌生人
                }
                if (teamNews != null) {
                    intent.putExtra("userId", teamNews.getTeamId().getTeamPresident().getUserPhone());
                    intent.putExtra("userNickname", teamNews.getTeamId().getTeamPresident().getUserNickname());
                    intent.putExtra("Own_id", user.getUserPhone());
                }
                if (team != null) {
                    intent.putExtra("userId", team.getTeamPresident().getUserPhone());
                    intent.putExtra("userNickname", team.getTeamPresident().getUserNickname());
                    intent.putExtra("Own_id", user.getUserPhone());
                }

                startActivity(intent);
                break;
            case R.id.btn_add_team:
                //执行添加社团的请求
                showEditDialog(TeamInformationActivity.this, "请输入加入社团理由");
                break;
            default:
                break;
        }
    }


    /**
     * 获取好友列表
     *
     * @param phone 手机号
     */
    private void obtainFriendList(String phone) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.friendUrl + ConstantUrl.friend_interface)
                .addParams("user_id", phone)
                .build()
                .execute(new MyStringCallback());

    }


    /**
     * 回调
     */
    public class MyStringCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(TeamInformationActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("[]")) {
                T.testShowShort(TeamInformationActivity.this, "您当前无好友");
            } else {

                T.testShowShort(TeamInformationActivity.this, "好友获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                friendlist = gson.fromJson(response, new TypeToken<List<TeamFriend>>() {
                }.getType());


            }
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
                            public void onClick(DialogInterface dialog, int id) {
                                TeamMemberRequestReason = userInput.getText().toString();
                                requestAddTeam();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
        L.v(TAG, "申请加入社团");
        dialog = ProgressDialog.show(this, "提示", Constant.mProgressDialog_success, true, true);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonString = new Gson().toJson(new TeamMemberRequest(user, team.getTeamId(), TeamMemberRequestReason, "insert"));

                //发起添加请求
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.teamMemberUrl + ConstantUrl.addTeamMemberRequest_interface)
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
            T.testShowShort(TeamInformationActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(TeamInformationActivity.this, Constant.mMessage_success);

            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(TeamInformationActivity.this, HomeActivity.class));  //跳转回主页面


        }
    }


}
