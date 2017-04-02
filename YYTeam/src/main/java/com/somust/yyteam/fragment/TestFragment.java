package com.somust.yyteam.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.somust.yyteam.R;
import com.somust.yyteam.activity.ContactsActivity;


import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * Created by DELL on 2016/3/14.
 */
public class TestFragment extends Fragment {
    public static TestFragment instance = null;//单例模式

    private View mView;
    private Button mButton_Friend;
    private Button mButton_Replace;
    private Button mButton_Contacts;
    private Button mButton_RedPackage;

    private EditText et_nickname;
    private String userId;


    public static TestFragment getInstance() {
        if (instance == null) {
            instance = new TestFragment();
        }

        return instance;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initView(inflater);

        mButton_Friend.setOnClickListener(new View.OnClickListener() {  //私聊
            @Override
            public void onClick(View v) {
                userId = et_nickname.getText().toString();
                if (!userId.equals("")) {
                    if (RongIM.getInstance() != null) {
                        RongIM.getInstance().startPrivateChat(getActivity(), userId, "私聊");
                    }
                } else {
                    Toast.makeText(getActivity(), "请输入用户昵称", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mButton_Replace.setOnClickListener(new View.OnClickListener() {  //更换头像
            @Override
            public void onClick(View v) {
                String userId = "zhong";
                String nickname = "钟良洁";
                String urlPath = "http://img.woyaogexing.com/2016/10/04/5da81cd8b7bc3f79!200x200.jpg";
                refreshUserInfo(userId, nickname, urlPath);
            }
        });
        mButton_Contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactsActivity.class));
            }
        });

        mButton_RedPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开红包界面
                //JrmfClient.intentWallet(ContactsActivity);
            }
        });

        return mView;

    }

    /**
     * 初始化数据
     *
     * @param inflater
     */
    private void initView(LayoutInflater inflater) {
        mView = inflater.inflate(R.layout.text_fragment, null);
        mButton_Friend = (Button) mView.findViewById(R.id.friend);
        mButton_Replace = (Button) mView.findViewById(R.id.id_bt_replace);
        mButton_Contacts = (Button) mView.findViewById(R.id.id_bt_opencontacts);
        et_nickname = (EditText) mView.findViewById(R.id.id_et_nickname);
        mButton_RedPackage = (Button) mView.findViewById(R.id.id_bt_redpackage);
    }
    /**
     * 刷新用户缓存数据
     * 测试更换头像http://img.woyaogexing.com/2016/10/04/5da81cd8b7bc3f79!200x200.jpg
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
     * 开启聊天组相关
     */
    public void DiscussionChat() {
        /**
         * 启动讨论组聊天界面。
         *
         * @param context            应用上下文。
         * @param targetDiscussionId 要聊天的讨论组 Id。
         * @param title              聊天的标题，如果传入空值，则默认显示讨论组名称。
         */
        RongIM.getInstance().startDiscussionChat(getActivity(), "9527", "标题");
    }


    /**
     * 创建讨论组会话并进入会话界面。
     * 讨论组创建成功后，会返回讨论组 id。
     *
     * @param context 应用上下文。
     * @param targetUserIds 要与之聊天的讨论组用户 Id 列表。
     * @param title 聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
     * @param callback 讨论组回调，成功时，返回讨论组 id。
     */
    public void createDiscussionChat(final Context context, final List<String> targetUserIds, final String title, final RongIMClient.CreateDiscussionCallback callback) {

    }
}
