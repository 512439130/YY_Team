package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.utils.log.L;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

public class TeamNewsActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "TeamNewsActivity:";
    ImageView returnView;
    TextView titleName;

    TeamNews teamNews;


    //新闻内容相关
    private TextView news_title;  //新闻标题
    private TextView news_content;  //新闻内容
    private ImageView news_image;  //新闻图片

    private ImageView team_image;  //社团图标
    private TextView team_name;  //社团名
    private TextView news_time;  //新闻发表时间

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_news);

        //接收Intent传值
        Intent intent = this.getIntent();
        teamNews = (TeamNews) intent.getSerializableExtra("teamNews");
        user = (User) intent.getSerializableExtra("user");
        L.v(TAG, teamNews.toString());

        initView();
        initData();
        initListener();
    }


    private void initView() {
        returnView = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);
        news_title = (TextView) findViewById(R.id.news_title);
        news_content = (TextView) findViewById(R.id.news_content);
        news_image = (ImageView) findViewById(R.id.news_image);

        team_image = (ImageView) findViewById(R.id.team_image);
        team_name = (TextView) findViewById(R.id.team_name);
        news_time = (TextView) findViewById(R.id.news_time);


    }

    private void initData() {
        titleName.setText("社团新闻");


        news_title.setText(teamNews.getNewsTitle());
        news_content.setText("\u3000\u3000"+teamNews.getNewsContent());
        news_time.setText(teamNews.getNewsTime());
        team_name.setText(teamNews.getTeamId().getTeamName());

        obtainNewImage(teamNews.getNewsImage());
        obtainTeamImage(teamNews.getTeamId().getTeamImage());


    }


    private void initListener() {
        returnView.setOnClickListener(this);

        team_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:  //返回键
                finish();
                break;
            case R.id.team_image:
                //打开个人信息界面（个人信息界面包含发送消息）


                //打开社团信息界面（Activity）
                Intent intent = new Intent(TeamNewsActivity.this, TeamInformationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("teamNews",teamNews);
                bundle.putSerializable("user",user);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    /**
     * 请求新闻图片
     *
     * @param url
     */
    public void obtainNewImage(String url) {
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
                        news_image.setImageBitmap(bitmap);


                    }
                });

    }

    /**
     * 请求社团logo
     *
     * @param url
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


}
