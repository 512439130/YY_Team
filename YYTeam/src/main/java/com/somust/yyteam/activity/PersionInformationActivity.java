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

public class PersionInformationActivity extends Activity {
    private static final String TAG = "PersionInformationActivity:";
    private ImageView iv_reutrn;
    private TextView titleName;

    private Button btn_add;
    private Button btn_send;

    private String userPhone;
    private String userNickname;
    private String openState;


    private ImageViewPlus iv_headPortrait;
    private TextView id_name;
    private TextView id_phone;
    private TextView id_sex;
    private Bitmap portraitBitmap;
    private ImageView iv_sex;

    private ProgressDialog dialog;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_persion);
        initView();
        //获取intent传值
        Intent intent = getIntent();
        userPhone = intent.getStringExtra("userId");
        userNickname = intent.getStringExtra("userNickname");
        openState = intent.getStringExtra("openState");
        //通过网络请求获取用户信息
        getUserInfo(userPhone);
    }

    private void initView() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName  = (TextView) findViewById(R.id.actionbar_name);
        btn_add = (Button) findViewById(R.id.btn_add_friend);
        btn_send = (Button) findViewById(R.id.btn_send_message);

        //监听事件
        btn_add.setOnClickListener(new MyOnClickListener());
        btn_send.setOnClickListener(new MyOnClickListener());
        iv_reutrn.setOnClickListener(new MyOnClickListener());


        iv_headPortrait = (ImageViewPlus) findViewById(R.id.id_head_portrait);
        id_name = (TextView) findViewById(R.id.id_name);
        id_phone = (TextView) findViewById(R.id.id_phone);
        id_sex = (TextView) findViewById(R.id.id_sex);
        iv_sex = (ImageView) findViewById(R.id.iv_sex);
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(final String userPhone) {
        final String url = ConstantUrl.userUrl + ConstantUrl.getUserInfo_interface;
        if (TextUtils.isEmpty(userPhone)) {
            T.testShowShort(PersionInformationActivity.this, "用户名密码不能为空");
        } else {
            dialog = ProgressDialog.show(this, "提示", Constant.mProgressDialog_message, true, true);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils
                            .post()
                            .url(url)
                            .addParams("userPhone", userPhone)
                            .build()
                            .execute(new MyStringCallback());
                }
            }, 600);



        }
    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {

        }

        @Override
        public void onAfter(int id) {
            setTitle("okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(PersionInformationActivity.this, Constant.mProgressDialog_error);
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(PersionInformationActivity.this, Constant.mMessage_error);
            } else {
                Gson gson = new Gson();
                user = gson.fromJson(response, User.class);
                System.out.println(user.toString());
                T.testShowShort(PersionInformationActivity.this, Constant.mMessage_success);
                L.v(TAG, "onResponse:" + response);

                //通过用户信息获取个人信息中的头像
                obtainImage(user.getUserImage());
            }

        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
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
                        //网络请求成功后，更新数据
                        initDatas();
                        dialog.cancel();
                    }
                });
    }


    private void initDatas() {
        titleName.setText("个人信息");
        if(openState.equals("friend")){
            btn_send.setVisibility(View.VISIBLE);
        }else if(openState.equals("stranger")){
            btn_add.setVisibility(View.VISIBLE);
            btn_send.setText("匿名消息");
            btn_send.setVisibility(View.VISIBLE);
        }
        iv_headPortrait.setImageBitmap(portraitBitmap); //设置用户头像
        id_name.setText(user.getUserNickname());
        id_phone.setText(user.getUserPhone());
        id_sex.setText(user.getUserSex());

        if(id_sex.getText().equals("男")){
            iv_sex.setBackgroundResource(R.mipmap.icon_man);
        }else if(id_sex.getText().equals("女")){
            iv_sex.setBackgroundResource(R.mipmap.icon_woman);
        }

    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_add_friend:  //添加好友

                    break;
                case R.id.btn_send_message:  //发消息
                    //打开单聊界面（根据position）
                    RongIM.getInstance().startPrivateChat(PersionInformationActivity.this, userPhone, userNickname);
                    finish();
                    break;
                case R.id.id_title_back:
                    finish();
                default:
                    break;
            }
        }
    }




}
