package com.somust.yyteam.fragment;

import android.app.Activity;
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
import com.somust.yyteam.activity.AccountActivity;
import com.somust.yyteam.activity.LoginActivity;

import com.somust.yyteam.activity.TeamHomeActivity;
import com.somust.yyteam.activity.UserManagerActivity;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;

import io.rong.imkit.RongIM;
import okhttp3.Call;

/**
 * Created by yy on 2017/3/14.
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    public static MineFragment instance = null;//单例模式

    public static MineFragment getInstance() {
        if (instance == null) {
            instance = new MineFragment();
        }

        return instance;
    }

    private View mView;

    //Tab_Button
    private RelativeLayout mUser;
    private RelativeLayout mTeam;
    private RelativeLayout mMoney;
    private RelativeLayout mSignout;


    private User user;

    private static final int RESULT_CANCELED = 0;



    //data
    private ImageView iv_headPortrait;
    private TextView id_name;
    private TextView id_phone;
    private Bitmap portraitBitmap;
    private static final String TAG = "MineFragment:";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, null);

        L.isDebug = true;
        T.isShow = true;
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
        mUser = (RelativeLayout) mView.findViewById(R.id.id_mine_user);
        mTeam.setOnClickListener(this);
        mMoney.setOnClickListener(this);
        mSignout.setOnClickListener(this);
        mUser.setOnClickListener(this);




        iv_headPortrait = (ImageView) mView.findViewById(R.id.id_head_portrait);
        iv_headPortrait.setOnClickListener(this);
        id_name = (TextView) mView.findViewById(R.id.id_name);
        id_phone = (TextView) mView.findViewById(R.id.id_phone);


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.id_mine_user:   //用户管理
                intent = new Intent(getActivity(), UserManagerActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                break;
            case R.id.id_mine_team://社团管理
                startActivity(new Intent(getActivity(), TeamHomeActivity.class));
                break;
            case R.id.id_mine_money://打开我的钱包页面
                JrmfClient.intentWallet(getActivity());
                break;
            case R.id.id_mine_sign_out:  //切换账户
                showNullDialog(getActivity(),"是否切换账户？");
                break;

            case R.id.id_head_portrait:  //更换头像

                intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra("user", user);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Activity.RESULT_FIRST_USER) {

            if (resultCode == RESULT_CANCELED) {
                user = (User) intent.getSerializableExtra("user");
                obtainImage(user.getUserImage());//刷新头像

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
                            public void onClick(DialogInterface dialog,int id) {
                                RongIM.getInstance().logout();//断开融云连接
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
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
