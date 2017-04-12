package com.somust.yyteam.activity.okhttptest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.somust.yyteam.R;
import com.somust.yyteam.activity.LoginActivity;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * create by yy testOkhttp
 */
public class TestSmsActivity extends Activity {


    private static final String TAG = "TestSmsActivity";


    private int mTime;
    private Timer mTimer;
    private Handler mCountDownHandler;
    private Button mBtnCode;
    private Button mBtnSubmit;
    private EditText edt_phone;
    private EditText edt_code;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sms);
        initView();
        initSms();
        initTimer();
    }

    private void initView() {
        mBtnCode = (Button) findViewById(R.id.btn_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit_button);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
    }

    private void initSms() {

        //注册短信回调
        //EventHandler 在这个handler中可以查看到短信验证的结果
        SMSSDK.initSDK(this, "1998b3d482ecc", "ce951c6979aa06187bed08d9b8b167f2");
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {

                switch (event) {

                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                            String country = (String) phoneMap.get("country");
                            String phone = (String) phoneMap.get("phone");
                            L.v(TAG, "手机号验证成功,通过智能验证");
                            L.v(TAG, "手机号：" + phone);
                            L.v(TAG, "国家：" + country);
                        } else {
                            L.v(TAG, "手机号验证失败，验证码输入有误");
                        }
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            boolean smart = (Boolean) data;
                            if (smart) {
                                //通过智能验证
                                L.v(TAG, "获取验证成功,通过智能验证");
                            } else {
                                //依然走短信验证
                                L.v(TAG, "获取验证成功,依然走短信验证");
                            }
                        } else {
                            L.v(TAG, "获取验证失败");
                        }
                        break;
                }
            }
        });
    }

    private void initTimer() {

        mBtnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = edt_code.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    T.testShowShort(TestSmsActivity.this, "请输入手机号");
                } else if (phone.length() != 11) {
                    T.testShowShort(TestSmsActivity.this, "请输入11位手机号");
                } else if (TextUtils.isEmpty(code)) {
                    T.testShowShort(TestSmsActivity.this, "请输入验证码");
                } else if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(code)) {
                    SMSSDK.submitVerificationCode("86", phone, code);//提交验证码  在eventHandler里面查看验证结果
                    //验证成功，跳转登录界面
                    //startActivity(new Intent(TestSmsActivity.this, LoginActivity.class));
                }

            }
        });
        mCountDownHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (mTime > 0) {
                    mBtnCode.setEnabled(false);
                    mBtnCode.setText(getString(R.string.count_down, mTime));
                } else {
                    mTimer.cancel();
                    mBtnCode.setEnabled(true);
                    mBtnCode.setText(R.string.login_verification_code);
                }
            }
        };

    }


    private void sendSMS() {
        String phone = edt_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            T.testShowShort(TestSmsActivity.this, "请输入手机号");
        } else if (phone.length() != 11) {
            T.testShowShort(TestSmsActivity.this, "请输入11位手机号");
        } else {
            // count down
            mTimer = new Timer();
            mTime = 60;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    mTime--;
                    Message msg = mCountDownHandler.obtainMessage();
                    mCountDownHandler.sendMessage(msg);
                }
            };
            mTimer.schedule(task, 100, 1000);


            doSendSMS(phone);
        }
    }

    private void doSendSMS(String phone) {
        SMSSDK.getVerificationCode("86", phone);//发送短信验证码到手机号  86表示的是中国
    }


}
