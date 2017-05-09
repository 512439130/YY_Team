package com.somust.yyteam.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.dialog.BottomMenuDialog;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.photo.PhotoUtils;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;


/**
 * 暂时停用，改方案
 */
public class UserManagerActivity extends Activity implements View.OnClickListener {
    //Tab_Button
    private RelativeLayout user_message;
    private RelativeLayout user_repass;

    //title button
    private ImageView iv_reutrn;
    private TextView titleName;
    private User user;



    //data
    private ImageView iv_headPortrait;
    private TextView id_name;
    private TextView id_phone;
    private Bitmap portraitBitmap;


    private Intent intent;

    private static final String TAG = "UserManagerActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);
        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");
        obtainImage(user.getUserImage()); //通过数据库user表中的图片url地址发起网络请求
        initView();
        initData();
        initListener();
    }

    private void initView() {
        //Tab_Button
        user_message = (RelativeLayout) findViewById(R.id.user_message);
        user_repass = (RelativeLayout) findViewById(R.id.user_repass);

        //title button
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);

        //顶部信息
        iv_headPortrait = (ImageView)findViewById(R.id.id_head_portrait);
        id_name = (TextView) findViewById(R.id.id_name);
        id_phone = (TextView) findViewById(R.id.id_phone);
    }

    private void initListener() {
        user_message.setOnClickListener(this);
        user_repass.setOnClickListener(this);
        iv_reutrn.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_message:  //维护个人信息
                //修改个人信息
                break;

            case R.id.user_repass:   //修改密码
                intent = new Intent();
                intent.setClass(UserManagerActivity.this, RePassActivity.class);
                startActivity(intent);
                break;
            case R.id.id_title_back:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainImage(String url) {
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
                        portraitBitmap = bitmap;
                        //网络请求成功后
                        initData();
                    }
                });
    }
    private void initData() {
        titleName.setText("用户管理");
        iv_headPortrait.setImageBitmap(portraitBitmap); //设置用户头像
        id_name.setText(user.getUserNickname());
        id_phone.setText(user.getUserPhone());
    }



}
