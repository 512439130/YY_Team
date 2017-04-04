package com.somust.yyteam.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.adapter.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;


import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * Created by Bob on 15/8/19.
 * 默认展示了几个好友 ID，展示一下 讨论组的功能  (联系人界面)
 */
public class ContactsActivity extends Activity{

    private ListView mListView;
    private ContactsAdapter mAdapter;
    private TextView mTitle;
    private RelativeLayout mBack;
    private TextView mSet;

    private CheckBox mCheckBox;

    /**
     * ids 收消息人的 id（模拟，需要查询好友列表的数据库）
     */
    String[] contacts = {"56145", "56146", "56147", "56148", "56149", "56150", "56151", "56152", "56153", "56154", "56155", "56156"};
    List mLists = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        initView();

        initEvent();
        /*//创建聊天组相关(通过check选择，决定聊天组的成员)
        for (int i = 0; i < contacts.length; i++) {
            mLists.add(contacts[i]);
        }*/


    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.list);
        mTitle = (TextView) findViewById(R.id.txt1);
        mBack = (RelativeLayout) findViewById(R.id.back);
        mSet = (TextView) findViewById(R.id.img3);
        mSet.setText("创建讨论组");

        mAdapter = new ContactsAdapter(ContactsActivity.this, contacts);
        mListView.setAdapter(mAdapter);
    }

    private void initEvent() {

        mSet.setOnClickListener(new MyOnclickListener());
        mBack.setOnClickListener(new MyOnclickListener());
        mListView.setOnItemClickListener(new MyListItemOnclicklistener());
        mListView.setOnItemLongClickListener(new MyListItemLongOnclicklistener());
    }
    class MyListItemOnclicklistener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            System.out.println("你选中了第" + (position + 1) + "个Item");
            mCheckBox = (CheckBox) view.findViewById(R.id.id_cb_checkbox);
            Boolean isCheck = mCheckBox.isChecked();
            if(!isCheck){
                mLists.add(contacts[position]);//添加选择
                mCheckBox.setChecked(true);
                System.out.println("Item有值");
            }else if(isCheck){
                mLists.remove(contacts[position]);//删除选择
                mCheckBox.setChecked(false);
                System.out.println("Item无值");
            }
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                Boolean isCheck = mCheckBox.isChecked();
                @Override
                public void onClick(View v) {
                    if(!isCheck){
                        mLists.add(contacts[position]);//添加选择

                    }else if(isCheck){
                        mLists.remove(contacts[position]);//删除选择
                    }
                }
            });
            //待解决问题（点击checkbox和点击Item混淆）


            //更新UI
            mAdapter.notifyDataSetChanged();

        }
    }


    private class MyOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img3:  //创建聊天组
                    String contactsName = "群聊test";
                    finish();
                    createContacts(contactsName);
                    break;
                case R.id.back:  //返回键
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 创建聊天组
     */
    private void createContacts(String contactsName) {
        if (RongIM.getInstance() != null) {
            /**创建讨论组会话并进入会话界面。
             *创建讨论组时，mLists为要添加的讨论组成员，创建者一定不能在 mLists 中
             */
            RongIM.getInstance().createDiscussionChat(ContactsActivity.this, mLists,contactsName, new RongIMClient.CreateDiscussionCallback() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("创建讨论组成功");
                    Toast.makeText(ContactsActivity.this, "创建讨论组成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(ContactsActivity.this, "创建讨论组失败", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


    private class MyListItemLongOnclicklistener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            //打开会话界面
            if (RongIM.getInstance() != null) {
                RongIM.getInstance().startPrivateChat(ContactsActivity.this, contacts[position], "title");

            }
            return true;
        }
    }
}
