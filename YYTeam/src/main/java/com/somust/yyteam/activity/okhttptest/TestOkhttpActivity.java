package com.somust.yyteam.activity.okhttptest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Request;

/**
 * create by yy testOkhttp
 */
public class TestOkhttpActivity extends Activity {


    private static final String TAG = "TestOkhttpActivity";

    private TextView mTv;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp);
        initView();
        initTimer();
    }


    private void initView() {
        mTv = (TextView) findViewById(R.id.id_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    public void getHtml(View view) {
        String url = ConstantUrl.mTestUrl + "yy_login?";
        OkHttpUtils
                .get()
                .url(url)
                .addParams("username", "yangyang")
                .addParams("password", "zz0114yhbb")
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    public void postLogin(View view) {
        String url = ConstantUrl.mTestUrl + "yy_login?";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username", "yy")
                .addParams("password", "zz0114yhbb")
                .build()
                .execute(new MyStringCallback());
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
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            Log.e(TAG, "onResponse：complete");
            mTv.setText("onResponse:" + response);

            switch (id) {
                case 100:
                    Toast.makeText(TestOkhttpActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(TestOkhttpActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            Log.e(TAG, "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }

    private int mTime;
    private Timer mTimer;
    private Handler mCountDownHandler;
    private Button mBtnCode;
    private Button mBtnSubmit;
    private String phone;

    private void initTimer() {
        mBtnCode = (Button) findViewById(R.id.btn_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit_button);
        mBtnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edt_code = (EditText) findViewById(R.id.edt_code);
                final String code = edt_code.getText().toString().trim();
                //开启子线程调用网络请求
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SMSSDK.submitVerificationCode("86", phone, code);//提交验证码  在eventHandler里面查看验证结果
                    }
                });

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
        EditText txtPhone = (EditText) findViewById(R.id.edt_phone);
        phone = txtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            T.testShowShort(TestOkhttpActivity.this, "请输入手机号");
        } else if (phone.length() != 11) {
            T.testShowShort(TestOkhttpActivity.this, "请输入11位手机号");
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
            L.e(phone);
            doSendSMS(phone);
        }
    }

    private void doSendSMS(String phone) {
        SMSSDK.initSDK(this, "1998b3d482ecc", "ce951c6979aa06187bed08d9b8b167f2");
        //注册短信回调
        //EventHandler 在这个handler中可以查看到短信验证的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                switch (event) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            boolean smart = (Boolean)data;
                            if(smart) {
                                //通过智能验证
                                L.e("手机号验证成功,通过智能验证");
                                L.e("event="+data.toString());
                                L.e("result="+data.toString());
                                L.e("data="+data.toString());
                            } else {
                                //依然走短信验证
                                L.e("手机号验证成功,通过短信验证");
                                L.e("event="+data.toString());
                                L.e("result="+data.toString());
                                L.e("data="+data.toString());
                            }
                        } else {
                            L.e("验证失败");

                        }
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            boolean smart = (Boolean)data;
                            if(smart) {
                                //通过智能验证
                                L.e("获取验证成功,通过智能验证");
                                L.e("event="+data.toString());
                                L.e("result="+data.toString());
                                L.e("data="+data.toString());
                            } else {
                                //依然走短信验证
                                L.e("获取验证成功,依然走短信验证");
                                L.e("event="+data.toString());
                                L.e("result="+data.toString());
                                L.e("data="+data.toString());
                            }
                        } else {
                            L.e("获取验证失败");
                        }
                        break;
                }
            }
        });

        SMSSDK.getVerificationCode("86", phone);//发送短信验证码到手机号  86表示的是中国



    }


}
