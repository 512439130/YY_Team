package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.ImageViewPlus;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by MMZB-YY on 2017/4/11.
 * 个人信息页
 */

public class TeamInformationActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TeamInformationActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;
    private TeamNews teamNews;


    private ImageView team_image;  //社团logo
    private ImageView team_type;   //社团类型
    private ImageView team_president_image;  //社团创建人头像

    private TextView team_name;  //社团名称
    private TextView team_president; //社团创建人
    private TextView team_time;  //社团创建时间
    private TextView team_introduce;  //社团简介

    private Button btn_add_team;  //加入社团


    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_team);
        initView();
        //获取intent传值
        //接收Intent传值
        Intent intent = this.getIntent();
        teamNews = (TeamNews) intent.getSerializableExtra("teamNews");
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

    private void initListener() {
        //监听事件
        iv_reutrn.setOnClickListener(this);
        team_president_image.setOnClickListener(this);
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
                intent.putExtra("userId", teamNews.getTeamId().getTeamPresident().getUserPhone());
                intent.putExtra("userNickname", teamNews.getTeamId().getTeamPresident().getUserNickname());
                intent.putExtra("openState","stranger");  //陌生人
                startActivity(intent);
                break;
            default:
                break;
        }
    }


}
