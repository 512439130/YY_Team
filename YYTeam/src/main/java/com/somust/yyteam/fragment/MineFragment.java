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

import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;
import com.somust.yyteam.activity.RePassActivity;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

/**
 * Created by DELL on 2016/3/14.
 */
public class MineFragment extends Fragment {
    public static MineFragment instance = null;//单例模式

    public static MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }

        return instance;
    }

    private View mView;

    //Tab_Button
    private RelativeLayout mTeam;
    private RelativeLayout mMoney;
    private RelativeLayout mSignout;
    private RelativeLayout mRepass;

    private User user;


    private Intent intent;


    //data
    private ImageView iv_headPortrait;
    private TextView id_name;
    private TextView id_phone;
    private Bitmap portraitBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, null);
        initView();
        Intent intent = getActivity().getIntent();
        user = (User) intent.getSerializableExtra("user");

        obtainImage(user.getUserImage()); //通过数据库user表中的图片url地址发起网络请求

        return mView;

    }




    /**
     * 初始化数据
     */
    private void initView() {
        mTeam = (RelativeLayout) mView.findViewById(R.id.id_mine_team);
        mMoney = (RelativeLayout) mView.findViewById(R.id.id_mine_money);
        mSignout = (RelativeLayout) mView.findViewById(R.id.id_mine_sign_out);
        mRepass = (RelativeLayout) mView.findViewById(R.id.id_mine_repass);
        mTeam.setOnClickListener(new MyOnClickListener());
        mMoney.setOnClickListener(new MyOnClickListener());
        mSignout.setOnClickListener(new MyOnClickListener());
        mRepass.setOnClickListener(new MyOnClickListener());


        iv_headPortrait = (ImageView) mView.findViewById(R.id.id_head_portrait);
        id_name = (TextView) mView.findViewById(R.id.id_name);
        id_phone = (TextView) mView.findViewById(R.id.id_phone);
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_mine_team:
                    //打开大学社团主页面
                    break;
                case R.id.id_mine_money:
                    //打开我的钱包页面
                    //打开红包界面
                    JrmfClient.intentWallet(getActivity());
                    break;

                case R.id.id_mine_repass:
                    //弹出确认退出框
                    T.testShowShort(getActivity(), "修改密码");
                    intent = new Intent();
                    intent.setClass(getActivity(), RePassActivity.class);
                    startActivity(intent);
                case R.id.id_mine_sign_out:
                    //弹出确认退出框
                    showMyDialog(getActivity());
                default:
                    break;
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
     * 弹出输入退出确认框
     */
    private void showMyDialog(Context context) {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_signout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                getActivity().finish();
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
