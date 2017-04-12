package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.google.gson.Gson;

import com.somust.yyteam.R;

import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.RongCloud.RongCloudMethodUtil;
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
import okhttp3.MediaType;
import okhttp3.Request;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private Button bt_register;


    //添加
    private EditText et_nickname;
    private EditText et_password;
    private EditText et_confirm_password;

    private String token = null;  //获取的token
    private User user;

    private RongCloudMethodUtil RongUtil;

    private String userJsonString;

    private ProgressDialog dialog;

    private RadioGroup radioGroupSex;

    private String DEFAULT_IMAGE = ConstantUrl.imageDefaultManUrl;


    private Button mBtnCode;
    private Button mBtnSubmit;
    private EditText edt_phone;
    private EditText edt_code;

    private String sex = "男";

    private int mTime;
    private Timer mTimer;

    private static final String TAG = "RegisterActivity:";
    private ImageView mImg_Background;

    private Handler mCountDownHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();

            if (bundle.getString("token") == "token_success") {
                L.v(TAG, "开始执行第二个请求");
                try {
                    SendHttpRegister();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (bundle.getString("sms_verification") == ("verification_success")) {  //手机号验证成功
                bt_register.setEnabled(true);  //设置注册按钮为可点击
                mBtnSubmit.setEnabled(false);  //设置验证手机号按钮为不可点击
                T.testShowShort(RegisterActivity.this, "手机号验证成功");
            }
            if (bundle.getString("sms_verification") == ("verification_error")) {  //手机号验证失败
                T.testShowShort(RegisterActivity.this, "手机号验证失败，请输入正确的验证码");
            }
            if (bundle.getString("code_obtain") == ("obtain_success")) {  //验证码获取成功
                T.testShowShort(RegisterActivity.this, "验证码获取成功");

                edt_phone.setFocusable(false);
                edt_phone.setTextColor(Color.GREEN);

            }

            if (bundle.getString("code_obtain") == ("obtain_error")) {    //验证码获取失败
                T.testShowLong(RegisterActivity.this, "验证码获取失败，请60秒后重新输入正确的手机号");
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
        setContentView(R.layout.activity_register);

        initView();
        initEvent();
        initSms();

        //动态背景
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }


    private void initView() {


        bt_register = (Button) findViewById(R.id.id_bt_register);

        et_nickname = (EditText) findViewById(R.id.id_et_nickname);
        et_password = (EditText) findViewById(R.id.id_et_password);
        et_confirm_password = (EditText) findViewById(R.id.id_et_confirm_password);

        radioGroupSex = (RadioGroup) this.findViewById(R.id.id_rg_sex);

        mBtnCode = (Button) findViewById(R.id.btn_code);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_code = (EditText) findViewById(R.id.edt_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit_button);


        user = new User();
    }

    private void initEvent() {

        mBtnSubmit.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        radioGroupSex.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        mBtnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendSMS();


            }
        });
    }


    /**
     * 获取用户输入的数据
     *
     * @return
     */
    public void obtainUserRegisterInfo(String phone) {

        //获取用户输入的值
        //昵称
        String nickname = et_nickname.getText().toString();
        //密码
        String pass = et_password.getText().toString();

        //确认密码
        String confirm_pass = et_confirm_password.getText().toString();
        String code = edt_code.getText().toString().trim();
        L.v(TAG, phone + "/" + pass + "/" + confirm_pass);

        //先验证填写是否为空
        if (TextUtils.isEmpty(phone)) {
            T.testShowShort(RegisterActivity.this, "需要先进行手机验证");
        } else if (TextUtils.isEmpty(code)) {
            T.testShowShort(RegisterActivity.this, "请输入验证码");
        } else if (TextUtils.isEmpty(nickname)) {
            T.testShowShort(RegisterActivity.this, "昵称不能为空");
        } else if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirm_pass) || (TextUtils.isEmpty(pass) && TextUtils.isEmpty(confirm_pass))) {  //密码和确认密码不同
            T.testShowShort(RegisterActivity.this, "密码和确认密码不能为空");
        } else if (!pass.equals(confirm_pass)) {
            T.testShowShort(RegisterActivity.this, "密码和确认密码不同");
        } else if (!phone.equals("") && !nickname.equals("") && !pass.equals("") && !confirm_pass.equals("") && pass.equals(confirm_pass)) {  //如果为空请重新填写
            //注册无响应

            user.setUserPhone(phone);
            user.setUserNickname(nickname);
            user.setUserPassword(pass);
            user.setUserSex(sex);
            user.setUserImage(DEFAULT_IMAGE);

            token = obtainUserToken(user.getUserPhone(), user.getUserNickname(), user.getUserImage());  //获取token
            user.setUserToken(token);
            System.out.println(user.toString());
        }

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

                            UpdateUiAndToast(mCountDownHandler, "sms_verification", "verification_success");
                        } else {
                            L.v(TAG, "手机号验证失败，验证码输入有误");

                            UpdateUiAndToast(mCountDownHandler, "sms_verification", "verification_error");

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
                                UpdateUiAndToast(mCountDownHandler, "code_obtain", "obtain_success");
                            }
                        } else {
                            L.v(TAG, "获取验证失败，请输入正确的手机号");

                            UpdateUiAndToast(mCountDownHandler, "code_obtain", "obtain_error");

                        }
                        break;
                }
            }
        });
    }


    private void sendSMS() {
        String phone = edt_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            T.testShowShort(RegisterActivity.this, "请输入手机号");
        } else if (phone.length() != 11) {
            T.testShowShort(RegisterActivity.this, "请输入11位手机号");
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


    /**
     * 获取token
     *
     * @param phone    用户id（手机号作为用户id)
     * @param nickname 用户昵称
     * @param urlPath  默认头像地址
     * @return
     */
    private String obtainUserToken(final String phone, final String nickname, final String urlPath) {
        if (phone != null && nickname != null && urlPath != null) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    token = RongUtil.getToken(phone, nickname, urlPath);  //获取token
                    L.v(TAG, "RegisterActivity+token:" + token);

                    UpdateUiAndToast(mCountDownHandler, "token", "token_success");
                }
            };
            thread.start();
        }
        return token;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit_button:   //验证手机号
                String phone = edt_phone.getText().toString().trim();
                String code = edt_code.getText().toString().trim();
                SMSSDK.submitVerificationCode("86", phone, code);//提交验证码  在eventHandler里面查看验证结果
                break;

            case R.id.id_bt_register:  //注册
                phone = edt_phone.getText().toString().trim();
                obtainUserRegisterInfo(phone);
                break;
            default:
                break;
        }
    }

    /**
     * 发起注册请求
     *
     * @throws IOException
     */
    private void SendHttpRegister() throws IOException {
        // 方式四 使用静态方式创建并显示，这种进度条只能是圆形条,这里最后一个参数boolean cancelable 设置是否进度条是可以取消的
        dialog = ProgressDialog.show(this, "提示", "正在注册中", true, true);
        final String phone = edt_phone.getText().toString().trim();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                userJsonString = new Gson().toJson(new User(phone, user.getUserNickname(), user.getUserPassword(), token, user.getUserImage(), user.getUserSex()));

                //发起注册请求
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.userUrl + ConstantUrl.userlogin_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(userJsonString)
                        .build()
                        .execute(new MyStringCallback());
            }
        }, 1200);//3秒后执行Runnable中的run方法

        L.v(TAG, "json处理后格式：" + userJsonString);
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
            dialog.cancel();//关闭圆形进度条
            e.printStackTrace();
            L.v(TAG, "注册失败");
            T.testShowShort(RegisterActivity.this, "注册失败");
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            Log.v(TAG, "onResponse：complete");
            L.v(response);
            L.v(TAG, "注册成功");
            T.testShowShort(RegisterActivity.this, "注册成功");
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));  //跳转到登录界面
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            Log.e(TAG, "inProgress:" + progress);
        }
    }

    /**
     * 性别选择器监听
     */
    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            // TODO Auto-generated method stub
            //获取变更后的选中项的ID
            int radioButtonId = group.getCheckedRadioButtonId();
            //根据ID获取RadioButton的实例
            RadioButton sexRadioButton = (RadioButton) RegisterActivity.this.findViewById(radioButtonId);
            String sex = sexRadioButton.getText().toString();
            L.v(sexRadioButton.getText().toString());
            if (sex != null) {
                L.e("sex不为空,sex:" + sex);
                user.setUserSex(sex);
            } else {
                L.e("sex为空,sex:" + sex);
            }


            //通过判断男女选择器来判断使用默认头像
            if (sex.equals("男")) {
                DEFAULT_IMAGE = ConstantUrl.imageDefaultManUrl;
            } else {
                DEFAULT_IMAGE = ConstantUrl.imageDefaultWomanUrl;
            }

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
}
