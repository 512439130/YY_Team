package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yyteam.R;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by MMZB-YY on 2017/4/11.
 */

public class InformationActivity extends Activity {
    private ImageView iv_reutrn;
    private Button btn_add;
    private Button btn_send;

    private String userId;

    private String userNickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initView();
        initData();
    }


    private void initView() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        btn_add = (Button) findViewById(R.id.btn_add_friend);
        btn_send = (Button) findViewById(R.id.btn_send_message);

        //监听事件
        btn_add.setOnClickListener(new MyOnClickListener());
        btn_send.setOnClickListener(new MyOnClickListener());
        iv_reutrn.setOnClickListener(new MyOnClickListener());
    }
    private void initData() {
        //获取intent传值
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userNickname = intent.getStringExtra("userNickname");

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_add_friend:  //添加好友

                    break;
                case R.id.btn_send_message:  //发消息
                    //打开单聊界面（根据position）
                    RongIM.getInstance().startPrivateChat(InformationActivity.this, userId, userNickname);
                    finish();
                    break;
                case R.id.id_title_back:
                    finish();
                default:
                    break;
            }
        }
    }


    /**
     * 刷新用户缓存数据
     * 测试更换头像http://img.woyaogexing.com/2016/10/04/5da81cd8b7bc3f79!200x200.jpg
     *
     * @param userid   需要更换的用户Id
     * @param nickname 用户昵称
     * @param urlPath  头像图片地址
     *                 userInfo 需要更新的用户缓存数据。
     */
    public void refreshUserInfo(String userid, String nickname, String urlPath) {
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(userid, nickname, Uri.parse(urlPath)));
    }
}
