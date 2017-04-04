package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.basetest.BaseTestActivity;
import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Request;


public class LoginActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private TextView tv_register;
    private TextView tv_testLogin;
    private Button btn_login;
    private Button btn_exit;
    private Intent intent;
    private EditText et_phone;
    private EditText et_password;

    private ProgressDialog dialog;

    private List<Friend> userIdList;
    //base
    private TextView tv_repassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userIdList = new ArrayList<Friend>();
        initView();
        initEvent();
        setUserInfoProvider();
    }


    private void initView() {
        tv_register = (TextView) findViewById(R.id.id_tv_register);
        tv_testLogin = (TextView) findViewById(R.id.id_tv_testlogin);
        btn_login = (Button) findViewById(R.id.id_bt_login);
        btn_exit = (Button) findViewById(R.id.id_bt_exit);
        et_phone = (EditText) findViewById(R.id.id_login_phone);
        et_password = (EditText) findViewById(R.id.id_login_password);

        tv_repassword = (TextView) findViewById(R.id.id_t_repassword);


    }

    private void initEvent() {
        tv_register.setOnClickListener(this);
        tv_testLogin.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        tv_repassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_register:
                intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.id_tv_testlogin:
                intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.id_bt_login:
                L.v(TAG, "登录");
                Login();
                break;
            case R.id.id_bt_exit:
                L.v(TAG, "退出");
                finish();
                break;
            case R.id.id_t_repassword:
                startActivity(new Intent(LoginActivity.this,BaseTestActivity.class));
            default:
                break;
        }
    }

    private void Login() {
        final String url = ConstantUrl.userUrl + "yy_login?";
        final String phone = et_phone.getText().toString().trim();
        final String password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            T.testShowShort(LoginActivity.this, "用户名密码不能为空");
        }else{
            // 方式四 使用静态方式创建并显示，这种进度条只能是圆形条,这里最后一个参数boolean cancelable 设置是否进度条是可以取消的
            dialog = ProgressDialog.show(this, "提示", "正在登陆中", true, true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    OkHttpUtils
                            .post()
                            .url(url)
                            .addParams("phone", phone)
                            .addParams("password", password)
                            .build()
                            .execute(new MyStringCallback());
                }
            }, 2000);//3秒后执行Runnable中的run方法


        }

    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            setTitle("正在登录,请稍后...");
        }

        @Override
        public void onAfter(int id) {
            setTitle("okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();
            //处理字符串中含引号的情况
            /*String result = response.replace("\"","");
            L.v(TAG,"处理前："+response);
            L.v(TAG,"处理后："+result);
            if(result.equals("login_error")){
                T.testShowShort(LoginActivity.this,"登录失败");
            }else{
                T.testShowShort(LoginActivity.this, "登录成功");
                L.v(TAG, "onResponse:" + result);
                String token = result;

            }*/


            if(response.equals("")){
                T.testShowShort(LoginActivity.this,"登录失败");
            }else{
                Gson gson = new Gson();
                User user = gson.fromJson(response,User.class);
                System.out.println(user.toString());
                T.testShowShort(LoginActivity.this, "登录成功");
                L.v(TAG, "onResponse:" + response);

                Friend friend = new Friend();
                friend.setUserId(user.getPhone());
                friend.setName(user.getNickname());
                friend.setPortraitUri(ConstantUrl.imageDefaultUrl);   //设置默认头像
                userIdList.add(friend);
                connectRongServer(user.getToken());

            }

        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
        }
    }


    private void setUserInfoProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                for (Friend i : userIdList) {
                    if (i.getUserId().equals(s)) {
                        Log.e(TAG, i.getPortraitUri());
                        return new UserInfo(i.getUserId(), i.getName(), Uri.parse(i.getPortraitUri()));
                    }
                }
                Log.e("MainActivity", "UserId is : " + s);
                return null;
            }

        }, true);
    }

    /**
     * 根据RongCloud的Token登录
     * @param token
     */
    private void connectRongServer(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {


            @Override
            public void onSuccess(String userId) {

                if (!userId.equals("")) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    T.testShowShort(LoginActivity.this, "用户Id：" + userId);
                } else {
                    T.testShowShort(LoginActivity.this, "连接失败，请检查网络：");
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                // Log.e("onError", "onError userid:" + errorCode.getValue());//获取错误的错误码
                L.e(TAG, "connect failure errorCode is : " + errorCode.getValue());
            }


            @Override
            public void onTokenIncorrect() {
                L.e(TAG, "token is error ,please check token and appkey");
            }
        });

    }
}
