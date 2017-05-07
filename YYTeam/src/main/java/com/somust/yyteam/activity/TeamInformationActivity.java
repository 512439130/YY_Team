package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.Team;
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


    private ProgressDialog dialog;

    private User user;

    //请求理由
    private String TeamMemberRequestReason;

    private String jsonString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_team);
        initView();
        //获取intent传值
        //接收Intent传值
        Intent intent = this.getIntent();
        teamNews = (TeamNews) intent.getSerializableExtra("teamNews");  //新闻列表跳转
        team = (Team) intent.getSerializableExtra("teams");  //社团列表跳转
        user = (User) intent.getSerializableExtra("user");
        //通过网络请求获取用户信息

        initDatas();
        initListener();

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
        if(teamNews != null){
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
        if(team != null){
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
        btn_add_team.setOnClickListener(this);
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
                if(teamNews != null){
                    intent.putExtra("userId", teamNews.getTeamId().getTeamPresident().getUserPhone());
                    intent.putExtra("userNickname", teamNews.getTeamId().getTeamPresident().getUserNickname());
                    intent.putExtra("openState","stranger");  //陌生人
                    intent.putExtra("Own_id",user.getUserPhone());
                }
                if(team != null){
                    intent.putExtra("userId", team.getTeamPresident().getUserPhone());
                    intent.putExtra("userNickname", team.getTeamPresident().getUserNickname());
                    intent.putExtra("openState","stranger");  //陌生人
                    intent.putExtra("Own_id",user.getUserPhone());
                }

                startActivity(intent);
                break;
            case R.id.btn_add_team:
                //执行添加社团的请求
                showEditDialog(TeamInformationActivity.this,"请输入加入社团理由");
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
                                TeamMemberRequestReason = userInput.getText().toString();
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
                jsonString = new Gson().toJson(new TeamMemberRequest(user, team.getTeamId(),TeamMemberRequestReason,"insert"));

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
