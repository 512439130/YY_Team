package com.somust.yyteam.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.activity.ContactsActivity;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, null);
        initView();


        return mView;

    }

    /**
     * 初始化数据
     *
     *
     */
    private void initView() {

    }

}
