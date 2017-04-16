package com.somust.yyteam.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.somust.yyteam.R;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * 聚合会话页面
 */
public class SubConversationListActivtiy extends FragmentActivity {
    private Fragment mConversationList;  //会话界面Fragment
    private Fragment mConversationFragment = null;

    private List<Fragment> mFragment = new ArrayList<>();

    private static final String TAG = "SubConversationListActivtiy:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_conversation_list_activtiy);

        mConversationList = initConversationList();  //获取融云会话列表的对象

        mFragment.add(mConversationList);//加入会话列表（第一页）
    }
    private Fragment initConversationList() {
        if (mConversationFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversationlist")
                    .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true")//设置私聊会话是否聚合显示
                    .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//仅仅显示私聊和群组类型
                    .build();
            listFragment.setUri(uri);
            return listFragment;
        } else {
            return mConversationFragment;
        }
    }
}
