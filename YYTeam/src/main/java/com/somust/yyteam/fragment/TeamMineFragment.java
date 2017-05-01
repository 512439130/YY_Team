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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.somust.yyteam.R;
import com.somust.yyteam.activity.LoginActivity;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.utils.log.L;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

/**
 * Created by yy on 2017/4/14.
 */
public class TeamMineFragment extends Fragment implements View.OnClickListener {
    public static TeamMineFragment instance = null;//单例模式

    public static TeamMineFragment getInstance() {
        if (instance == null) {
            instance = new TeamMineFragment();
        }

        return instance;
    }

    private View mView;

    //Tab_Button
    private RelativeLayout mTaskMessage;
    private RelativeLayout mSendNew;
    private RelativeLayout mSendTask;
    private RelativeLayout mTeamnout;


    private User user;





    //data
    private ImageView iv_headPortrait;
    private TextView id_name;
    private TextView id_phone;
    private Bitmap portraitBitmap;
    private static final String TAG = "TeamMineFragment:";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_team_mine, null);
        //T.isShow = true;  //关闭toast
        //L.isDebug = true;  //关闭Log
        initView();
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");

//        obtainImage(user.getUserImage()); //通过数据库user表中的图片url地址发起网络请求

        return mView;

    }




    /**
     * 初始化数据
     */
    private void initView() {
        mTaskMessage = (RelativeLayout) mView.findViewById(R.id.id_member_mine_task_message);
        mSendNew = (RelativeLayout) mView.findViewById(R.id.id_member_mine_send_new);
        mSendTask = (RelativeLayout) mView.findViewById(R.id.id_member_mine_send_task);
        mTeamnout = (RelativeLayout) mView.findViewById(R.id.id_member_mine_sign_out);

        mTaskMessage.setOnClickListener(this);
        mSendNew.setOnClickListener(this);
        mSendTask.setOnClickListener(this);
        mTeamnout.setOnClickListener(this);


        iv_headPortrait = (ImageView) mView.findViewById(R.id.id_head_portrait);
        id_name = (TextView) mView.findViewById(R.id.id_name);
        id_phone = (TextView) mView.findViewById(R.id.id_phone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_member_mine_task_message://任务通知消息
                L.v(TAG,"任务通知消息");
                break;
            case R.id.id_member_mine_send_new:  //发布社团新闻
                L.v(TAG,"发布社团新闻");
                break;
            case R.id.id_member_mine_send_task://发送任务安排
                L.v(TAG,"发送任务安排");
                break;
            case R.id.id_member_mine_sign_out:  //申请退出社团
                L.v(TAG,"申请退出社团");
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
     * 弹出输入退出确认框
     */
    private void showMyDialog(Context context) {
        // get prompts.xml view
        L.v(TAG,"调用dialog");
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_null_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), LoginActivity.class));

                            }
                        })
                .setPositiveButton("取消",
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
}
