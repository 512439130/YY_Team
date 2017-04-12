package com.somust.yyteam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jrmf360.rylib.JrmfClient;
import com.somust.yyteam.R;

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

    private RelativeLayout mTeam;

    private RelativeLayout mMoney;
    private RelativeLayout mSignout;


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
        mTeam = (RelativeLayout) mView.findViewById(R.id.id_mine_team);
        mMoney = (RelativeLayout) mView.findViewById(R.id.id_mine_money);
        mSignout = (RelativeLayout) mView.findViewById(R.id.id_mine_sign_out);
        mTeam.setOnClickListener(new MyOnClickListener());
        mMoney.setOnClickListener(new MyOnClickListener());
        mSignout.setOnClickListener(new MyOnClickListener());
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.id_mine_team:
                    //打开大学社团主页面
                    break;
                case R.id.id_mine_money:
                    //打开我的钱包页面
                    //打开红包界面
                    JrmfClient.intentWallet(getActivity());
                    break;
                case R.id.id_mine_sign_out:
                    //弹出确认退出框
                    getActivity().finish();
                default:
                    break;
            }
        }
    }
}
