package com.somust.yyteam.activity.basetest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.somust.yy.base.BaseFragment;
import com.somust.yy.base.LoginActivity;
import com.somust.yy.base.SecondFragment;
import com.somust.yy.base.TabLayoutActivity;
import com.somust.yyteam.R;

/**
 * Created by tangyangkai on 16/5/10.
 */
public class MainFragment extends BaseFragment {

    private Button mainBtn, mainSecondBtn,mainTabBtn;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mainBtn = (Button) view.findViewById(R.id.main_btn);
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("username", "tangyankai");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtras(data);
                startActivity(intent);

            }
        });

        mainSecondBtn = (Button) view.findViewById(R.id.main_second_btn);
        mainSecondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(SecondFragment.newInstance("从首界面跳转过来的"));
            }
        });

        mainTabBtn= (Button) view.findViewById(R.id.main_tablayout_btn);
        mainTabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),TabLayoutActivity.class));
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }
}
