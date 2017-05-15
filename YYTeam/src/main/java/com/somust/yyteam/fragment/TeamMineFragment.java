package com.somust.yyteam.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.CreateTeamNewsActivity;
import com.somust.yyteam.activity.ExamineTeamTaskActivity;
import com.somust.yyteam.activity.HomeActivity;
import com.somust.yyteam.activity.LoginActivity;
import com.somust.yyteam.activity.TaskResponsibleActivity;
import com.somust.yyteam.activity.TeamInformationActivity;
import com.somust.yyteam.activity.TeamMemberRequestActivity;
import com.somust.yyteam.activity.UpdateTeamInfoActivity;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamMemberRequest;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.ImageViewPlus;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.MediaType;

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
    private LinearLayout mSignOutTeamRequest;
    private LinearLayout mUpdateTeamMessage;
    private LinearLayout mExamineTask;
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


    private String jsonString;

    //请求理由
    private String TeamMemberRequestReason;
    private Team team;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team_mine, null);
        //T.isShow = true;  //关闭toast
        //L.isDebug = true;  //关闭Log
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        teamMember = (TeamMember) intent.getSerializableExtra("teamMember");
        team = teamMember.getTeamId();
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
            mTeamnout.setVisibility(View.GONE);
            mSendNew.setVisibility(View.GONE);
        } else if (teamMember.getTeamMemberPosition().equals("部长")) {
            //隐藏发布社团新闻
            //隐藏完善社团信息
            mSendNew.setVisibility(View.GONE);
            mUpdateTeamMessage.setVisibility(View.GONE);
        } else if (teamMember.getTeamMemberPosition().equals("干事")) {
            //隐藏社团添加请求
            //隐藏社团退出请求
            //隐藏发送任务安排
            //隐藏完善社团信息
            //隐藏审核任务总结
            mTeamRequest.setVisibility(View.GONE);
            mSignOutTeamRequest.setVisibility(View.GONE);
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

        mSignOutTeamRequest = (LinearLayout) mView.findViewById(R.id.id_member_mine_sign_out_team_request);


        mUpdateTeamMessage = (LinearLayout) mView.findViewById(R.id.id_member_mine_team_update_message);
        mExamineTask = (LinearLayout) mView.findViewById(R.id.id_member_mine_team_examine_task);


        mSendNew = (LinearLayout) mView.findViewById(R.id.id_member_mine_send_new);
        mSendTask = (LinearLayout) mView.findViewById(R.id.id_member_mine_send_task);
        mTeamnout = (LinearLayout) mView.findViewById(R.id.id_member_mine_sign_out);

        mTeamRequest.setOnClickListener(this);
        mSignOutTeamRequest.setOnClickListener(this);

        mUpdateTeamMessage.setOnClickListener(this);
        mExamineTask.setOnClickListener(this);

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
                intent.putExtra("flag","insert");
                startActivity(intent);
                break;
            case R.id.id_member_mine_sign_out_team_request:
                L.v(TAG, "社团退出请求");
                intent = new Intent(getActivity(), TeamMemberRequestActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("team", teamMember.getTeamId());
                intent.putExtra("flag","signout");
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
                intent = new Intent(getActivity(), ExamineTeamTaskActivity.class);
                //传值
                intent.putExtra("user", user);
                intent.putExtra("team", teamMember.getTeamId());
                startActivity(intent);
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
                intent = new Intent(getActivity(), TaskResponsibleActivity.class);
                //传值
                intent.putExtra("user", user);
                intent.putExtra("teamMember", teamMember);
                startActivity(intent);
                break;
            case R.id.id_member_mine_sign_out:  //申请退出社团
                L.v(TAG, "申请退出社团");
                showEditDialog(getActivity(), "是否申请退出社团？");
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
     * 弹出输入提示框
     */
    private void showEditDialog(final Context context, String dialogName) {
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.dialog_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_name_tv = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name_tv.setText(dialogName);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                TeamMemberRequestReason = userInput.getText().toString();
                                requestSignOutTeam();
                                ForceHidekeyboard(promptsView);//强制隐藏软键盘
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    /**
     * 申请加入社团的网络请求
     */
    private void requestSignOutTeam() {
        L.v(TAG,"申请退出社团");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jsonString = new Gson().toJson(new TeamMemberRequest(user, team.getTeamId(),TeamMemberRequestReason,"signout"));

                //发起添加请求
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.teamMemberUrl + ConstantUrl.addTeamMemberRequest_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonString)
                        .build()
                        .execute(new MyRequestSignOutTeamCallback());
            }
        }, 600);//2秒后执行Runnable中的run方法





    }
    public class MyRequestSignOutTeamCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.v(TAG, "请求失败");
            T.testShowShort(getActivity(), Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(getActivity(), Constant.mMessage_success);
            getActivity().finish();
            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(getActivity(), HomeActivity.class));  //跳转回主页面

        }


    }
    /**
     * 强制隐藏软键盘
     *
     * @param view
     */
    private void ForceHidekeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }
}
