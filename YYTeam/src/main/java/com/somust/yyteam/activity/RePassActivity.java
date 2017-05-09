package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Request;

public class RePassActivity extends Activity implements View.OnClickListener{
    private ImageView mImg_Background;

    private static final String TAG = "RePassActivity:";
    private Button mBtnCode;
    private Button mBtnSubmit;

    private int mTime;
    private Timer mTimer;


    private EditText edt_phone;
    private EditText edt_code;

    private EditText et_password;
    private EditText et_confirm_password;

    private Button bt_repass;

    private ProgressDialog dialog;

    private Handler mRepassCountDownHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (bundle.getString("sms_verification") == ("verification_success")) {  //手机号验证成功
                bt_repass.setEnabled(true);  //设置注册按钮为可点击
                mBtnSubmit.setEnabled(false);  //设置验证手机号按钮为不可点击
                T.testShowShort(RePassActivity.this, "手机号验证成功");
            }
            if (bundle.getString("sms_verification") == ("verification_error")) {  //手机号验证失败
                T.testShowShort(RePassActivity.this, "手机号验证失败，请输入正确的验证码");
            }
            if (bundle.getString("code_obtain") == ("obtain_success")) {  //验证码获取成功
                T.testShowShort(RePassActivity.this, "验证码获取成功");

                edt_phone.setFocusable(false);
                edt_phone.setTextColor(Color.GREEN);

            }

            if (bundle.getString("code_obtain") == ("obtain_error")) {    //验证码获取失败
                T.testShowLong(RePassActivity.this, "验证码获取失败，请60秒后重新输入正确的手机号");
            }

            //handler更新获取验证码按钮UI动态时间
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repass);
        initView();
        initEvent();
        initSms();
        //动态背景
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(RePassActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }

    private void initView() {
        bt_repass = (Button) findViewById(R.id.id_bt_repass);




        mBtnCode = (Button) findViewById(R.id.btn_code);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit_button);

        et_password = (EditText) findViewById(R.id.id_et_password);
        et_confirm_password = (EditText) findViewById(R.id.id_et_confirm_password);
    }

    private void initEvent() {
        mBtnSubmit.setOnClickListener(this);
        bt_repass.setOnClickListener(this);

        mBtnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });
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
                            L.v(TAG, "手机号验证成功");
                            L.v(TAG, "手机号：" + phone);
                            L.v(TAG, "国家：" + country);

                            UpdateUiAndToast(mRepassCountDownHandler, "sms_verification", "verification_success");
                        } else {
                            L.v(TAG, "手机号验证失败，验证码输入有误");

                            UpdateUiAndToast(mRepassCountDownHandler, "sms_verification", "verification_error");

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
                                UpdateUiAndToast(mRepassCountDownHandler, "code_obtain", "obtain_success");
                            }
                        } else {
                            L.v(TAG, "获取验证失败，请输入正确的手机号");

                            UpdateUiAndToast(mRepassCountDownHandler, "code_obtain", "obtain_error");

                        }
                        break;
                }
            }
        });
    }
    private void sendSMS() {
        String phone = edt_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            T.testShowShort(RePassActivity.this, "请输入手机号");
        } else if (phone.length() != 11) {
            T.testShowShort(RePassActivity.this, "请输入11位手机号");
        } else {
            // count down
            mTimer = new Timer();
            mTime = 60;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    mTime--;
                    Message msg = mRepassCountDownHandler.obtainMessage();
                    mRepassCountDownHandler.sendMessage(msg);
                }
            };
            mTimer.schedule(task, 100, 1000);


            SMSSDK.getVerificationCode("86", phone);//发送短信验证码到手机号  86表示的是中国
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_button:   //验证手机号
                String phone = edt_phone.getText().toString().trim();
                String code = edt_code.getText().toString().trim();
                SMSSDK.submitVerificationCode("86", phone, code);//提交验证码  在eventHandler里面查看验证结果
                break;

            case R.id.id_bt_repass:  //修改密码
                obtainUserRepassInfo();
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证过的手机和输入框的值
     */
    private void obtainUserRepassInfo() {
        String phone = edt_phone.getText().toString().trim();
        //密码
        String pass = et_password.getText().toString();
        //确认密码
        String confirm_pass = et_confirm_password.getText().toString();


        //先验证填写是否为空
        if (TextUtils.isEmpty(phone)) {
            T.testShowShort(RePassActivity.this, "需要先进行手机验证");
        }  else if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirm_pass) || (TextUtils.isEmpty(pass) && TextUtils.isEmpty(confirm_pass))) {  //密码和确认密码不同
            T.testShowShort(RePassActivity.this, "密码和确认密码不能为空");
        } else if (!pass.equals(confirm_pass)) {
            T.testShowShort(RePassActivity.this, "密码和确认密码不同");
        } else if (!phone.equals("")&& !pass.equals("") && !confirm_pass.equals("") && pass.equals(confirm_pass)) {  //如果为空请重新填写
            T.testShowShort(RePassActivity.this, "信息不为空");

            UpdateUserPass(phone,pass);
        }
    }

    /**
     * 修改密码/找回密码网络请求
     */
    private void UpdateUserPass(final String phone, final String password) {
        final String url = ConstantUrl.userUrl + ConstantUrl.userUpdatePass_interface;


        // 方式四 使用静态方式创建并显示，这种进度条只能是圆形条,这里最后一个参数boolean cancelable 设置是否进度条是可以取消的
        dialog = ProgressDialog.show(this, "提示",Constant.mProgressDialog_success, true, true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .post()
                        .url(url)
                        .addParams("userPhone", phone)
                        .addParams("userPassword", password)
                        .build()
                        .execute(new MyStringCallback());
            }
        }, 600);//3秒后执行Runnable中的run方法
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
            dialog.cancel();
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(RePassActivity.this, Constant.mProgressDialog_error);
        }

        @Override
        public void onResponse(String response, int id) {
            int result = Integer.parseInt(response);
            if (result == 0) {
                dialog.cancel();
                T.testShowShort(RePassActivity.this, Constant.mMessage_error);
            } else {
                T.testShowShort(RePassActivity.this,  Constant.mMessage_success);
                //startActivity(new Intent(RePassActivity.this,LoginActivity.class));
                finish();
            }

        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
        }

    }
    /**
     * 更新UI和控制子线程弹出Toast
     *
     * @param handler
     * @param key
     * @param value
     */
    private void UpdateUiAndToast(Handler handler, String key, String value) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    @Override
    protected void onDestroy() {
        if(dialog != null){
            dialog.dismiss();
        }
        super.onDestroy();
    }
}
