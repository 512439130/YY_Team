package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.utils.RongCloud.RongCloudMethodUtil;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Request;


public class MainActivity extends Activity implements View.OnClickListener {


    private static final String token1 = "6czXKi1E2mZohPUdilrF/qFKKP/7F85h1jIAC7xxzrPpZK7jJRj1wcq4fLku6WT2HHOhJ8ZsJGdilL2nygPD4Msu60IN+uAf";
    private static final String token2 = "ubXdWSTofuJHR8gEczH/r6FKKP/7F85h1jIAC7xxzrPpZK7jJRj1wdM9GSjgQBJjkZ67PMfRnmJZfBbonu5yOv1GHg7FWPok";

    private List<Friend> userIdList;
    private static final String TAG = "MainActivity";

    private Button mUser1, mUser2;
    private Button tokenButton;
    private Button tokenloginButton;

    private RongCloudMethodUtil RongUtil;
    private EditText et_username, et_password, et_nickname;
    private String name = null;
    private String nickname = null;
    private final String urlPath = "http://img.woyaogexing.com/2016/10/14/c248ae77be5a04d2!200x200.jpg";
    private String pass = null;
    private String token = null;


    private String mTestUrl = "http://118.89.47.137/";  //gethtml


    private TextView mTv;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();

        setUserInfoProvider();

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


    private void initView() {
        mUser1 = (Button) findViewById(R.id.connect_user1);
        mUser2 = (Button) findViewById(R.id.connect_user2);
        tokenButton = (Button) findViewById(R.id.id_token);
        tokenloginButton = (Button) findViewById(R.id.id_tokenlogin);
        mUser1.setOnClickListener(this);
        mUser2.setOnClickListener(this);
        tokenButton.setOnClickListener(this);
        tokenloginButton.setOnClickListener(this);

        et_username = (EditText) findViewById(R.id.id_et_username);
        et_password = (EditText) findViewById(R.id.id_et_password);
        et_nickname = (EditText) findViewById(R.id.id_et_nickname);

        userIdList = new ArrayList<Friend>();
        RongUtil = new RongCloudMethodUtil();

        mTv = (TextView) findViewById(R.id.id_textview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    /**
     * 获取用户输入的数据
     */
    private void obtainUserOrPass() {
        //获取用户名密码
        name = et_username.getText().toString();
        pass = et_password.getText().toString();
        nickname = et_nickname.getText().toString();
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
                    if (userId.equals("13160677911")) {
                        mUser1.setText("用户1连接服务器成功");
                    } else if (userId.equals("13662544232")) {
                        mUser2.setText("用户2连接服务器成功");
                    }
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    Toast.makeText(MainActivity.this, "用户Id：" + userId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "连接失败，请检查网络：", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                // Log.e("onError", "onError userid:" + errorCode.getValue());//获取错误的错误码
                Log.e(TAG, "connect failure errorCode is : " + errorCode.getValue());
            }


            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "token is error ,please check token and appkey");
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_user1:
                //添加用户信息
                userIdList.add(new Friend("13160677911", "杨阳", "http://img.woyaogexing.com/2016/08/09/eeff5649932292ff!200x200.jpg"));//联通图标
                //连接融云服务器
                connectRongServer(token1);
                break;
            case R.id.connect_user2:
                //添加用户信息
                userIdList.add(new Friend("13662544232", "李冰红", "http://img.woyaogexing.com/2016/10/14/c248ae77be5a04d2!200x200.jpg"));//移动图标
                //连接融云服务器
                connectRongServer(token2);
                break;
            case R.id.id_token:
                //获取用户输入的用户名和密码
                obtainUserOrPass();
                if (!name.equals("") && !pass.equals("")) {
                    //通过用户注册信息获取token
                    token = obtainToken(name, nickname, urlPath);
                    if(token != null){
                        //发起注册请求
                        String url = mTestUrl + "yy_login?";
                        OkHttpUtils
                                .post()
                                .url(url)
                                .addParams("username",name)
                                .addParams("password",pass)
                                .build()
                                .execute(new MyStringCallback());
                    }else{
                        Toast.makeText(MainActivity.this, "Token不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else if (name.equals("") && pass.equals("")) {
                    Toast.makeText(MainActivity.this, "用户名密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_tokenlogin:
                //现在用户列表中添加Friend实体
                //先在服务器数据库查询用户名密码是否正确，如果正确，取到用户对应的token，如果不正确，提示用户名密码错误
                //根据RongCloud的Token登录
                userIdList.add(new Friend(name, nickname, urlPath));
                connectRongServer(token);
            default:
                break;
        }
    }

    /**
     * 获取token
     * @param username  用户id
     * @param nickname  用户昵称
     * @param urlPath   默认头像地址
     * @return
     */
    private String obtainToken(final String username, final String nickname, final String urlPath) {

        if (username != null && nickname != null && urlPath != null) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    token = RongUtil.getToken(username, nickname, urlPath);  //获取token
                    System.out.println("MainActivity+token:" + token);
                }
            };
            thread.start();
        }
        return token;
    }

    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            Log.e(TAG, "onResponse：complete");
            mTv.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    Toast.makeText(MainActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(MainActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }


}
