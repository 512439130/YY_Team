package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.somust.yyteam.adapter.TaskResponsibleAdapter;
import com.somust.yyteam.bean.Friend;
import com.somust.yyteam.bean.PersonBean;
import com.somust.yyteam.bean.TeamMember;
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

public class TaskResponsibleActivity extends Activity implements View.OnClickListener {
    private ImageView returnView;
    private TextView name;

    private ListView teamMemberListView;


    //SideBar相关
    private List<PersonBean> personBeenList;
    private SideBar sidebar;
    private TextView dialogTextView;
    private TaskResponsibleAdapter taskResponsibleAdapter;

    private static final String TAG = "TaskResponsibleActivity:";

    private User user;    //登录用户信息
    private List<TeamMember> teamMembers;   //登录用户的好友信息
    private Bitmap[] portraitBitmaps;





    private List<Friend> userIdList;//用户信息提供者


    private TeamMember teamMember;
    private Integer teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        immersiveStatusBar();
        initView();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        teamMember = (TeamMember) intent.getSerializableExtra("teamMember");
        teamId = teamMember.getTeamId().getTeamId();
        //发送网络请求，获取好友列表
        obtainTeamList(teamId.toString());
    }

    /**
     * 沉浸式状态栏（伪）
     */
    private void immersiveStatusBar() {
        //沉浸式状态栏（伪）
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 获取社团人员列表
     *
     * @param teamId 手机号
     */
    private void obtainTeamList(String teamId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamMemberUrl + ConstantUrl.obtainTeamMember_interface)
                .addParams("teamId", teamId)
                .build()
                .execute(new MyTeamMemberCallback());

    }


    /**
     * 回调
     */
    public class MyTeamMemberCallback extends StringCallback {
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
            T.testShowShort(TaskResponsibleActivity.this, "获取失败,服务器正在维护中");
        }

        @Override
        public void onResponse(String response, int id) {

            if (response.equals("[]")) {

                T.testShowShort(TaskResponsibleActivity.this, "您当前无好友");
            } else {

                T.testShowShort(TaskResponsibleActivity.this, "好友获取成功");
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                teamMembers = gson.fromJson(response, new TypeToken<List<TeamMember>>() {
                }.getType());

                for (int i = 0; i < teamMembers.size(); i++) {
                    L.v(TAG, "第1个好友信息" + teamMembers.get(i));
                }
                portraitBitmaps = new Bitmap[teamMembers.size()];
                for (int i = 0; i < teamMembers.size(); i++) {
                    obtainImage(teamMembers.get(i).getUserId().getUserImage(), i);
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
        name.setText("选择负责人");
        teamMemberListView = (ListView) findViewById(R.id.id_lv_friend);


        sidebar = (SideBar) findViewById(R.id.sidebar);
        dialogTextView = (TextView) findViewById(R.id.dialog);
        sidebar.setTextView(dialogTextView);

        returnView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:  //返回键
                finish();
                break;
            default:
                break;
        }
    }


    private void initEvent() {
        teamMemberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                System.out.println("你选中了第" + (position + 1) + "个好友");
                final Integer taskResponsibleUserId = personBeenList.get(position).getUserId();
                final Integer taskTeamId = teamId;


                //打开下一个Activity（传值   userId,teamId）
                Intent intent = new Intent(TaskResponsibleActivity.this, CreateTeamTaskActivity.class);
                //传值
                intent.putExtra("taskReleaseUser", user);  //发布任务者
                intent.putExtra("taskResponsibleUserId", taskResponsibleUserId);  //任务负责人
                intent.putExtra("taskTeamId",taskTeamId);//社团编号
                startActivity(intent);

                //更新UI
                taskResponsibleAdapter.notifyDataSetChanged();
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
                int position = taskResponsibleAdapter.getPositionForSelection(s.charAt(0));

                if (position != -1) {
                    teamMemberListView.setSelection(position);
                }
            }
        });


        String[] names = new String[teamMembers.size()];
        String[] phones = new String[teamMembers.size()];
        for (int i = 0; i < teamMembers.size(); i++) {
            names[i] = teamMembers.get(i).getUserId().getUserNickname();
            phones[i] = teamMembers.get(i).getUserId().getUserPhone();
        }


        personBeenList = getData(names, phones);  //真实数据

        // 数据在放在adapter之前需要排序
        Collections.sort(personBeenList, new PinyinComparator());

        taskResponsibleAdapter = new TaskResponsibleAdapter(TaskResponsibleActivity.this, personBeenList);

        teamMemberListView.setAdapter(taskResponsibleAdapter);
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
            if(!names[i].equals(user.getUserNickname()) ){
                String pinyin = PinyinUtils.getPingYin(names[i]);
                String Fpinyin = pinyin.substring(0, 1).toUpperCase();

                PersonBean person = new PersonBean();
                person.setUserId(teamMembers.get(i).getUserId().getUserId());
                person.setName(names[i]);
                person.setImage(portraitBitmaps[i]);
                person.setPinYin(pinyin);
                person.setPhone(phones[i]);
                person.setTeamMemberPosition(teamMembers.get(i).getTeamMemberPosition());
                // 正则表达式，判断首字母是否是英文字母
                if (Fpinyin.matches("[A-Z]")) {
                    person.setFirstPinYin(Fpinyin);
                } else {
                    person.setFirstPinYin("#");
                }

                listarray.add(person);
            }

        }
        return listarray;

    }


}
