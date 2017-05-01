package com.somust.yyteam.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.LocationSource;
import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.application.YYApplication;
import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.context.BaseContext;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import okhttp3.Call;
import okhttp3.Request;


/**
 * Created by 13160677911 on 2016-11-30.
 * 会话界面
 */


public class ConversationActivity extends FragmentActivity implements RongIM.LocationProvider, RongIM.ConversationBehaviorListener {
    private static final String TAG = "ConversationActivity:";

    private ProgressDialog dialog;

    private User user;

    private TextView mTitle;
    private RelativeLayout mBack;

    private String mTargetId;
    private String sickName;

    //监听输入相关
    private String inputStatus;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    private static final int SET_TEXT_TYPING_TITLE = 0X111;  //正在输入文本的状态
    private static final int SET_VOICE_TYPING_TITLE = 0X222;  //正在录语音的状态
    private static final int SET_TARGETID_TITLE = 0X000;  //当前会话没有用户正在输入，标题栏仍显示原来标题


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_TEXT_TYPING_TITLE:  //文字状态
                    inputStatus = "正在文字输入...";
                    mTitle.setText(inputStatus);
                    break;
                case SET_VOICE_TYPING_TITLE:  //语音状态
                    inputStatus = "正在语音输入...";
                    mTitle.setText(inputStatus);
                    break;
                case SET_TARGETID_TITLE:  //无状态
                    mTitle.setText(sickName);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);

        Intent intent = getIntent();

        initView();
        //设置头部监听
        setActionBar();


        getIntentDate(intent);
        //输入状态的监听
        setTypingStatusListener();

        initLocation();
    }


    private void initView() {
        mTitle = (TextView) findViewById(R.id.id_title_name);
        mBack = (RelativeLayout) findViewById(R.id.id_rl_title_bar);

    }

    private void getIntentDate(Intent intent) {
        mTargetId = getIntent().getData().getQueryParameter("targetId");//targetId:单聊即对方ID，群聊即群组ID
        sickName = getIntent().getData().getQueryParameter("title");//获取昵称
        System.out.println("昵称：" + sickName);
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        enterFragment(mConversationType, mTargetId);

        if (!TextUtils.isEmpty(sickName)) {  //昵称不为空的情况
            mTitle.setText(sickName);


            getUserInfo(mTargetId);  //刷新聊天目标对象的用户信息

        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(final String userPhone) {
        final String url = ConstantUrl.userUrl + ConstantUrl.getUserInfo_interface;
        if (TextUtils.isEmpty(userPhone)) {
            T.testShowShort(ConversationActivity.this, "手机号不能为空");
        } else {

            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("userPhone", userPhone)
                    .build()
                    .execute(new MyStringCallback());


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
            T.testShowShort(ConversationActivity.this, Constant.mProgressDialog_error);
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(ConversationActivity.this, Constant.mMessage_error);
            } else {
                Gson gson = new Gson();
                user = gson.fromJson(response, User.class);
                System.out.println(user.toString());
                T.testShowShort(ConversationActivity.this, Constant.mMessage_success);
                L.v(TAG, "onResponse:" + response);

                refreshUserInfo(user.getUserPhone(), user.getUserNickname(), user.getUserImage());


            }

        }

    }

    /**
     * 刷新用户缓存数据
     *
     * @param userid   需要更换的用户Id
     * @param nickname 用户昵称
     * @param urlPath  头像图片地址
     *                 userInfo 需要更新的用户缓存数据。
     */
    public void refreshUserInfo(String userid, String nickname, String urlPath) {
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(userid, nickname, Uri.parse(urlPath)));
    }


    /**
     * 设置 actionbar_base 事件
     */
    private void setActionBar() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setActionBarTitle(String mTargetId) {
        setActionBarTitle(mTargetId);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }

    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {


        String token = null;

        if (BaseContext.getInstance() != null) {

            token = BaseContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
            System.out.println("TOKEN:" + token);
        }

        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null && intent.getData().getQueryParameter("push").equals("true")) {
                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }

    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(YYApplication.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    public void setTypingStatusListener() {
        RongIMClient.setTypingStatusListener(new RongIMClient.TypingStatusListener() {
            @Override
            public void onTypingStatusChanged(Conversation.ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
                //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
                if (type.equals(mConversationType) && targetId.equals(mTargetId)) {
                    //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                    int count = typingStatusSet.size();
                    if (count > 0) {
                        Iterator iterator = typingStatusSet.iterator();
                        TypingStatus status = (TypingStatus) iterator.next();
                        String objectName = status.getTypingContentType();

                        MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                        MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                        //匹配对方正在输入的是文本消息还是语音消息
                        if (objectName.equals(textTag.value())) {
                            //显示“对方正在输入”
                            mHandler.sendEmptyMessage(SET_TEXT_TYPING_TITLE);

                        } else if (objectName.equals(voiceTag.value())) {
                            //显示"对方正在讲话"
                            mHandler.sendEmptyMessage(SET_VOICE_TYPING_TITLE);
                        }
                    } else {
                        //当前会话没有用户正在输入，标题栏仍显示原来标题
                        mHandler.sendEmptyMessage(SET_TARGETID_TITLE);
                    }
                }
            }
        });
    }


    private void initLocation() {
        //设置地理位置监听事件
        RongIM.setLocationProvider(this);
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
    }

    /**
     * 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
     *
     * @param context          上下文
     * @param locationCallback 回调
     */
    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        L.v("点击地图功能");
    }


    /**
     * 头像点击事件
     *
     * @param context
     * @param conversationType
     * @param userInfo
     * @return
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        L.v("头像点击事件");
        return false;
    }

    /**
     * 头像长点击事件
     *
     * @param context
     * @param conversationType
     * @param userInfo
     * @return
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        L.v("头像长点击事件");
        return false;
    }


    /**
     * 消息点击事件
     *
     * @param context
     * @param view
     * @param message
     * @return
     */
    @Override
    public boolean onMessageClick(Context context, View view, io.rong.imlib.model.Message message) {
        L.v("消息点击事件");
        return false;
    }


    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    /**
     * 消息长按事件
     *
     * @param context
     * @param view
     * @param message
     * @return
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, io.rong.imlib.model.Message message) {
        L.v("消息长按事件");
        return false;
    }


}
