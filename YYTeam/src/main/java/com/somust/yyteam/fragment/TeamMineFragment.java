package com.somust.yyteam.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.activity.CreateTeamNewsActivity;
import com.somust.yyteam.activity.LoginActivity;
import com.somust.yyteam.activity.TeamMemberRequestActivity;
import com.somust.yyteam.activity.UpdateTeamInfoActivity;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamMemberRequest;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.view.ImageViewPlus;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

/**
 * Created by yy on 2017/4/14.
 * 我的社团页
 */
public class TeamMineFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TeamMineFragment:";
    public static TeamMineFragment instance = null;//单例模式

    public static TeamMineFragment getInstance() {
        if (instance == null) {
            instance = new TeamMineFragment();
        }

        return instance;
    }

    private View mView;

    //Tab_Button
    private LinearLayout mTeamRequest;
    private LinearLayout mUpdateTeamMessage;
    private LinearLayout mExamineTask;
    private LinearLayout mTaskMessage;
    private LinearLayout mSendNew;
    private LinearLayout mSendTask;
    private LinearLayout mTeamnout;


    private User user;
    private TeamMember teamMember;
    private Integer teamId = 0;
    //top
    //left
    private ImageViewPlus teamImage;
    private TextView teamName;

    //right
    private ImageViewPlus userImage;
    private TextView teamPosition;
    private TextView userName;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team_mine, null);
        //T.isShow = true;  //关闭toast
        //L.isDebug = true;  //关闭Log
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        teamMember = (TeamMember) intent.getSerializableExtra("teamMember");
        teamId = teamMember.getTeamId().getTeamId();
        initView();
        initDatas();

        obtainTeamImage(teamMember.getTeamId().getTeamImage()); //获取Team头像
        obtainUserImage(user.getUserImage());  //获取用户头像


        itemUIController();

        return mView;

    }

    /**
     * 根据社团职位，显示或隐藏部分按钮
     */
    private void itemUIController() {
        if (teamMember.getTeamMemberPosition().equals("社长")) {
            //隐藏发布社团新闻
            //隐藏社团任务通知
            mTaskMessage.setVisibility(View.GONE);
            mSendNew.setVisibility(View.GONE);
        } else if (teamMember.getTeamMemberPosition().equals("部长")) {
            //隐藏发布社团新闻
            //隐藏完善社团信息
            mSendNew.setVisibility(View.GONE);
            mUpdateTeamMessage.setVisibility(View.GONE);
        } else if (teamMember.getTeamMemberPosition().equals("干事")) {
            //隐藏社团添加请求
            //隐藏发送任务安排
            //隐藏完善社团信息
            //隐藏审核任务总结
            mTeamRequest.setVisibility(View.GONE);
            mSendTask.setVisibility(View.GONE);
            mUpdateTeamMessage.setVisibility(View.GONE);
            mExamineTask.setVisibility(View.GONE);
        }
    }


    /**
     * 初始化数据
     */
    private void initView() {
        mTeamRequest = (LinearLayout) mView.findViewById(R.id.id_member_mine_team_request);
        mUpdateTeamMessage = (LinearLayout) mView.findViewById(R.id.id_member_mine_team_update_message);
        mExamineTask = (LinearLayout) mView.findViewById(R.id.id_member_mine_team_examine_task);

        mTaskMessage = (LinearLayout) mView.findViewById(R.id.id_member_mine_task_message);
        mSendNew = (LinearLayout) mView.findViewById(R.id.id_member_mine_send_new);
        mSendTask = (LinearLayout) mView.findViewById(R.id.id_member_mine_send_task);
        mTeamnout = (LinearLayout) mView.findViewById(R.id.id_member_mine_sign_out);

        mTeamRequest.setOnClickListener(this);
        mUpdateTeamMessage.setOnClickListener(this);
        mExamineTask.setOnClickListener(this);
        mTaskMessage.setOnClickListener(this);
        mSendNew.setOnClickListener(this);
        mSendTask.setOnClickListener(this);
        mTeamnout.setOnClickListener(this);


        teamImage = (ImageViewPlus) mView.findViewById(R.id.team_image);
        teamName = (TextView) mView.findViewById(R.id.team_name);

        userImage = (ImageViewPlus) mView.findViewById(R.id.user_image);
        teamPosition = (TextView) mView.findViewById(R.id.team_position);
        userName = (TextView) mView.findViewById(R.id.user_name);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_member_mine_team_request:  //社团添加请求

                L.v(TAG, "社团添加请求");
                //查看添加请求的列表（带下拉刷新）
                intent = new Intent(getActivity(), TeamMemberRequestActivity.class);
                //传值
                intent.putExtra("user", user);
                intent.putExtra("team", teamMember.getTeamId());
                startActivity(intent);
                break;
            case R.id.id_member_mine_team_update_message:  //完善社团信息
                L.v(TAG, "完善社团信息");
                intent = new Intent(getActivity(), UpdateTeamInfoActivity.class);
                //传值
                intent.putExtra("user", user);
                intent.putExtra("team", teamMember.getTeamId());
                startActivity(intent);
                break;
            case R.id.id_member_mine_team_examine_task:  //审核任务总结
                L.v(TAG, "审核任务总结");
                break;
            case R.id.id_member_mine_task_message://任务通知消息
                L.v(TAG, "任务通知消息");
                break;
            case R.id.id_member_mine_send_new:  //发布社团新闻
                L.v(TAG, "发布社团新闻");
                intent = new Intent(getActivity(), CreateTeamNewsActivity.class);
                //传值
                intent.putExtra("team", teamMember.getTeamId());
                startActivity(intent);

                break;
            case R.id.id_member_mine_send_task://发送任务安排
                L.v(TAG, "发送任务安排");

                break;
            case R.id.id_member_mine_sign_out:  //申请退出社团
                L.v(TAG, "申请退出社团");
                break;
            default:
                break;
        }
    }


    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainTeamImage(String url) {
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
                        teamImage.setImageBitmap(bitmap); //设置社团icon
                    }
                });
    }

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainUserImage(String url) {
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
                        userImage.setImageBitmap(bitmap); //设置用户头像
                    }
                });
    }

    private void initDatas() {
        teamName.setText(teamMember.getTeamId().getTeamName());
        teamPosition.setText(teamMember.getTeamMemberPosition());
        userName.setText(user.getUserNickname());
    }

    /**
     * 弹出输入退出确认框
     */
    private void showMyDialog(Context context) {
        // get prompts.xml view
        L.v(TAG, "调用dialog");
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_null_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), LoginActivity.class));

                            }
                        })
                .setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
