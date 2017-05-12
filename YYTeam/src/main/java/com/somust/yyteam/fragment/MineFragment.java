package com.somust.yyteam.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.MyTeamTaskActivity;
import com.somust.yyteam.activity.TeamListActivity;
import com.somust.yyteam.activity.TeamTaskActivity;
import com.somust.yyteam.activity.UpdateUserInfoActivity;
import com.somust.yyteam.activity.FriendRequestActivity;
import com.somust.yyteam.activity.LoginActivity;

import com.somust.yyteam.activity.RePassActivity;
import com.somust.yyteam.activity.TeamActivity;
import com.somust.yyteam.activity.TeamHomeActivity;
import com.somust.yyteam.bean.FriendRequestUser;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.List;

import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * Created by yy on 2017/3/14.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MineFragment:";

    public static MineFragment instance = null;//单例模式

    public static MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }
        return instance;
    }

    private View mView;

    //Tab_Button
    private RelativeLayout user_request;
    private RelativeLayout user_update_message;
    private RelativeLayout mRepass;
    private RelativeLayout mTeam;
    private RelativeLayout mTeamManager;
    private RelativeLayout mTeamTask;

    private RelativeLayout mMyTeamTask;
    private TextView mTeamManagerLine;
    private RelativeLayout mMoney;
    private RelativeLayout mSignout;


    private User user;
    private String userPhone;

    private static final int RESULT_CANCELED = 0;


    //data
    private ImageView iv_headPortrait;
    private TextView id_name;
    private TextView id_phone;
    private Bitmap portraitBitmap;

    private boolean teamFlag;

    private List<TeamMember> teamMembers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_mine, null);


        initView();
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");
        userPhone = user.getUserPhone();

        getUserInfo(userPhone);


        obtainTeamInfo(user.getUserId().toString());
        return mView;

    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(final String userPhone) {
        final String url = ConstantUrl.userUrl + ConstantUrl.getUserInfo_interface;
        if (TextUtils.isEmpty(userPhone)) {
            T.testShowShort(getActivity(), "手机号不能为空");
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
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(getActivity(), Constant.mProgressDialog_error);
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(getActivity(), Constant.mMessage_error);
            } else {
                Gson gson = new Gson();
                user = gson.fromJson(response, User.class);
                System.out.println(user.toString());

                T.testShowShort(getActivity(), Constant.mMessage_success);
                L.v(TAG, "onResponse:" + response);

                obtainImage(user.getUserImage());//刷新界面显示
                //刷新融云用户的头像
                // refreshUserInfo(user.getUserPhone(), user.getUserNickname(), Constant.USER_IMAGE_PATH + fileName);
                //photoUtils.clearCropFile(selectUri);//清除手机内存中刚刚裁剪的图片   必须在上传文件成功后执行
            }

        }

    }


    /**
     * 获取我的所有社团
     *
     * @param userId
     */
    private void obtainTeamInfo(String userId) {
        OkHttpUtils
                .post()
                .url(ConstantUrl.teamUrl + ConstantUrl.getMyTeam_interface)
                .addParams("userId", userId)
                .build()
                .execute(new MyTeamIdCallback());
    }


    public class MyTeamIdCallback extends StringCallback {


        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(getActivity(), Constant.mMessage_error);
                teamFlag = false;
                mTeamManager.setVisibility(View.GONE);
                mTeamManagerLine.setVisibility(View.GONE);
            } else {
                Gson gson = new GsonBuilder().setDateFormat(Constant.formatType).create();
                teamMembers = gson.fromJson(response, new TypeToken<List<TeamMember>>() {
                }.getType());
                teamFlag = true;
                mTeamManager.setVisibility(View.VISIBLE);
                mTeamManagerLine.setVisibility(View.VISIBLE);
            }



        }


    }

    /**
     * 初始化数据
     */
    private void initView() {


        user_request = (RelativeLayout) mView.findViewById(R.id.user_request);
        user_update_message = (RelativeLayout) mView.findViewById(R.id.user_update_message);
        mRepass = (RelativeLayout) mView.findViewById(R.id.id_mine_repass);
        mTeam = (RelativeLayout) mView.findViewById(R.id.id_mine_team);

        mTeamManager = (RelativeLayout) mView.findViewById(R.id.id_mine_team_manager);
        mTeamTask = (RelativeLayout) mView.findViewById(R.id.id_mine_team_task);
        mMyTeamTask = (RelativeLayout) mView.findViewById(R.id.id_mine_my_team_task);
        mTeamManagerLine = (TextView) mView.findViewById(R.id.id_mine_line);
        mMoney = (RelativeLayout) mView.findViewById(R.id.id_mine_money);
        mSignout = (RelativeLayout) mView.findViewById(R.id.id_mine_sign_out);


        user_request.setOnClickListener(this);
        user_update_message.setOnClickListener(this);
        mRepass.setOnClickListener(this);
        mTeam.setOnClickListener(this);
        mTeamManager.setOnClickListener(this);
        mTeamTask.setOnClickListener(this);
        mMyTeamTask.setOnClickListener(this);
        mMoney.setOnClickListener(this);
        mSignout.setOnClickListener(this);


        iv_headPortrait = (ImageView) mView.findViewById(R.id.id_head_portrait);
        id_name = (TextView) mView.findViewById(R.id.id_name);
        id_phone = (TextView) mView.findViewById(R.id.id_phone);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.user_request:  //添加请求
                //查看添加请求的列表（带下拉刷新）
                intent = new Intent(getActivity(), FriendRequestActivity.class);
                //传值
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.user_update_message:  //维护个人信息
                intent = new Intent(getActivity(), UpdateUserInfoActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
                break;
            case R.id.id_mine_repass:   //修改密码
                intent = new Intent();
                intent.setClass(getActivity(), RePassActivity.class);
                startActivity(intent);
                break;
            case R.id.id_mine_team://申请加入社团
                //先判断team_id是否为空
                intent = new Intent(getActivity(), TeamActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;

            case R.id.id_mine_team_manager://我的社团
                if (teamFlag) {
                    //打开我的社团列表
                    intent = new Intent(getActivity(), TeamListActivity.class);
                    intent.putExtra("user", user);
                    intent.putExtra("teamMembers", (Serializable) teamMembers);
                    startActivity(intent);
                }
                break;
            case R.id.id_mine_team_task://社团活动报名
                intent = new Intent(getActivity(), TeamTaskActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.id_mine_my_team_task://我的社团活动
                intent = new Intent(getActivity(), MyTeamTaskActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;

            case R.id.id_mine_money://打开我的钱包页面
                JrmfClient.intentWallet(getActivity());
                break;
            case R.id.id_mine_sign_out:  //切换账户
                showNullDialog(getActivity(), "是否切换账户？");
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Activity.RESULT_FIRST_USER) {

            if (resultCode == RESULT_CANCELED) {
                getUserInfo(userPhone);
            }
        }
    }

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainImage(String url) {
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
                        portraitBitmap = bitmap;
                        //网络请求成功后
                        initDatas();
                    }
                });
    }

    private void initDatas() {
        iv_headPortrait.setImageBitmap(portraitBitmap); //设置用户头像
        id_name.setText(user.getUserNickname());
        id_phone.setText(user.getUserPhone());
    }


    /**
     * 弹出无输入确认框
     */
    public void showNullDialog(Context context, String dialogName) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_null_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        TextView dialog_name = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name.setText(dialogName);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                RongIM.getInstance().logout();//断开融云连接
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
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
