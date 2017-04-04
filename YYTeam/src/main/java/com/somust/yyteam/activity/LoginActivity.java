package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initEvent();
    }


    private void initView() {
        tv_register = (TextView) findViewById(R.id.id_tv_register);
        tv_testLogin = (TextView) findViewById(R.id.id_tv_testlogin);
        btn_login = (Button) findViewById(R.id.id_bt_login);
        btn_exit = (Button) findViewById(R.id.id_bt_exit);
        et_phone = (EditText) findViewById(R.id.id_login_phone);
        et_password = (EditText) findViewById(R.id.id_login_password);
    }

    private void initEvent() {
        tv_register.setOnClickListener(this);
        tv_testLogin.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
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
            default:
                break;
        }
    }

    private void Login() {
        String url = ConstantUrl.userUrl + "yy_login?";
        String phone = et_phone.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            T.testShowShort(LoginActivity.this, "用户名密码不能为空");
        }else{
            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("phone", phone)
                    .addParams("password", password)
                    .build()
                    .execute(new MyStringCallback());
        }

    }

    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id) {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            T.testShowShort(LoginActivity.this, "登录成功");
            L.v(TAG, "onResponse:" + response);

            switch (id) {
                case 100:
                    T.testShowShort(LoginActivity.this, "http");
                    break;
                case 101:
                    T.testShowShort(LoginActivity.this, "https");
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
        }
    }
}
