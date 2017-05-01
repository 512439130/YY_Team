package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.GroupChatAdapter;
import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.bean.PersonBean;
import com.somust.yyteam.bean.TeamFriend;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.SideBar.PinyinComparator;
import com.somust.yyteam.utils.SideBar.PinyinUtils;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.SideBar;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Request;

public class GroupChatActivity extends Activity implements View.OnClickListener {
    private ImageView returnView;  //头像
    private TextView name;  //昵称

    private ListView friendListView;


    //SideBar相关
    private List<PersonBean> personBeenList;
    private SideBar sidebar;
    private TextView dialogTextView;
    private GroupChatAdapter friendAdapter;

    private static final String TAG = "GroupChatActivity:";

    private User user;    //登录用户信息
    private List<TeamFriend> friendlist;   //登录用户的好友信息
    private Bitmap[] portraitBitmaps;


    //title_bar相关
    private ImageView back;
    private TextView createGroup;

    List mLists = new ArrayList();

    private CheckBox mCheckBox;



    private List<Friend> userIdList;//用户信息提供者

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        initView();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        //发送网络请求，获取好友列表
        obtainFriendList(user.getUserPhone());
    }

    /**
     * 获取好友列表
     *
     * @param phone 手机号
     */
    private void obtainFriendList(String phone) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.friendUrl + ConstantUrl.friend_interface)
                .addParams("user_id", phone)
                .build()
                .execute(new MyStringCallback());

    }


    /**
     * 回调
     */
    public class MyStringCallback extends StringCallback {
        @Override
        public void onBefore(Request request, int id) {

        }

        @Override
        public void onAfter(int id) {

        }

        @Override
        public void onError(Call call, Exception e, int id) {

            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(GroupChatActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {

                T.testShowShort(GroupChatActivity.this, "您当前无好友");
            } else {

                T.testShowShort(GroupChatActivity.this, "好友获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                friendlist = gson.fromJson(response, new TypeToken<List<TeamFriend>>() {
                }.getType());

                for (int i = 0; i < friendlist.size(); i++) {
                    L.v(TAG, "第1个好友信息" + friendlist.get(i));
                }
                portraitBitmaps = new Bitmap[friendlist.size()];
                for (int i = 0; i < friendlist.size(); i++) {
                    obtainImage(friendlist.get(i).getFriendPhone().getUserImage(), i);
                }


            }

        }

        @Override
        public void inProgress(float progress, long total, int id) {
            L.v(TAG, "inProgress:" + progress);
        }

    }

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     * @param i   需要保存在bitmaps的对应位置
     */
    public void obtainImage(String url, final int i) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        L.v("TAG", "onResponse：complete");
                        portraitBitmaps[i] = bitmap;
                        //网络请求成功后

                        initSidebar();
                        initEvent();
                    }
                });
    }

    private void initView() {
        returnView = (ImageView) findViewById(R.id.id_title_back);
        name = (TextView) findViewById(R.id.id_title_name);
        friendListView = (ListView) findViewById(R.id.id_lv_friend);

        back = (ImageView) findViewById(R.id.id_title_back);
        createGroup = (TextView) findViewById(R.id.id_title_creategroup);

        createGroup.setText("创建讨论组");
        sidebar = (SideBar) findViewById(R.id.sidebar);
        dialogTextView = (TextView) findViewById(R.id.dialog);
        sidebar.setTextView(dialogTextView);

        back.setOnClickListener(this);
        createGroup.setOnClickListener(this);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:  //返回键
                finish();
                break;
            case R.id.id_title_creategroup:
                showMyDialog();  //打开提示框，点击确认后，成功创建讨论组
                break;
            default:
                break;
        }
    }

    private String groupName;
    /**
     * 弹出输入提示框
     */
    private void showMyDialog() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_group, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                groupName = userInput.getText().toString();
                                L.v(TAG,"讨论组名称："+groupName);
                                for(int i = 0; i < friendlist.size(); i++){
                                    Friend friend = new Friend();
                                    friend.setUserId(friendlist.get(i).getFriendPhone().getUserPhone());
                                    friend.setName(friendlist.get(i).getFriendPhone().getUserNickname());
                                    friend.setPortraitUri(friendlist.get(i).getFriendPhone().getUserImage());   //设置默认头像(修改为获取用户的头像)
                                    userIdList = new ArrayList<Friend>();
                                    userIdList.add(friend);
                                    setUserInfoProvider();   //把用户信息发送给融云
                                }

                                createContacts(groupName);
                                finish();
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void initEvent() {
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                System.out.println("你选中了第" + (position + 1) + "个好友");
                final String userId = personBeenList.get(position).getPhone();

                mCheckBox = (CheckBox) view.findViewById(R.id.id_cb_checkbox);

                final Boolean isCheck = mCheckBox.isChecked();
                if (!isCheck) {
                    //选择一个item给list中添加一人
                    mLists.add(userId);//添加选择
                    mCheckBox.setChecked(true);
                    System.out.println("Item有值");
                } else if (isCheck) {
                    //选择一个item给list中添加一人
                    mLists.remove(userId);//删除选择
                    mCheckBox.setChecked(false);
                    System.out.println("Item无值");
                }

                //更新UI
                friendAdapter.notifyDataSetChanged();
                L.v(TAG, "群聊讨论组添加成功，人数为" + mLists.size());
                for (int i = 0; i < mLists.size(); i++) {
                    L.v(TAG, "群聊讨论组列表" + mLists.get(i));
                }
            }
        });
    }


    private void initSidebar() {
        //insert


        // 设置字母导航触摸监听
        sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                // 该字母首次出现的位置
                int position = friendAdapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    friendListView.setSelection(position);
                }
            }
        });


        String[] names = new String[friendlist.size()];
        String[] phones = new String[friendlist.size()];
        for (int i = 0; i < friendlist.size(); i++) {
            names[i] = friendlist.get(i).getFriendPhone().getUserNickname();
            phones[i] = friendlist.get(i).getFriendPhone().getUserPhone();
        }


        personBeenList = getData(names, phones);  //真实数据

        // 数据在放在adapter之前需要排序
        Collections.sort(personBeenList, new PinyinComparator());

        friendAdapter = new GroupChatAdapter(GroupChatActivity.this, personBeenList);

        friendListView.setAdapter(friendAdapter);
    }


    /**
     * 模拟数据获取（通过资源文件）
     * <p>
     * name : 昵称
     * image :
     *
     * @return
     */
    private List<PersonBean> getData(String[] names, String[] phones) {
        List<PersonBean> listarray = new ArrayList<PersonBean>();
        for (int i = 0; i < names.length; i++) {
            String pinyin = PinyinUtils.getPingYin(names[i]);
            String Fpinyin = pinyin.substring(0, 1).toUpperCase();

            PersonBean person = new PersonBean();
            person.setName(names[i]);
            person.setImage(portraitBitmaps[i]);
            person.setPinYin(pinyin);
            person.setPhone(phones[i]);
            // 正则表达式，判断首字母是否是英文字母
            if (Fpinyin.matches("[A-Z]")) {
                person.setFirstPinYin(Fpinyin);
            } else {
                person.setFirstPinYin("#");
            }

            listarray.add(person);
        }
        return listarray;

    }


    /**
     * 创建聊天组
     */
    private void createContacts(String contactsName) {
        if (RongIM.getInstance() != null) {
            /**创建讨论组会话并进入会话界面。
             *创建讨论组时，mLists为要添加的讨论组成员，创建者一定不能在 mLists 中
             */
            RongIM.getInstance().createDiscussionChat(GroupChatActivity.this, mLists, contactsName, new RongIMClient.CreateDiscussionCallback() {
                @Override
                public void onSuccess(String s) {
                    System.out.println("创建讨论组成功");
                    Toast.makeText(GroupChatActivity.this, "创建讨论组成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Toast.makeText(GroupChatActivity.this, "创建讨论组失败", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * 提供用户信息
     */
    private void setUserInfoProvider() {
        RongIM.setUserInfoProvider(new RongIM.UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String s) {
                for (Friend friend : userIdList) {
                    if (friend.getUserId().equals(s)) {
                        L.e(TAG, friend.getPortraitUri());
                        return new UserInfo(friend.getUserId(), friend.getName(), Uri.parse(friend.getPortraitUri()));
                    }
                }
                L.e(TAG, "UserId is : " + s);
                return null;
            }

        }, true);
    }


}
