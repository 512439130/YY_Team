package com.somust.yyteam.activity.basetest;


import com.somust.yy.base.AppActivity;
import com.somust.yy.base.BaseFragment;

public class BaseTestActivity extends AppActivity {


    @Override
    protected BaseFragment getFirstFragment() {
        return MainFragment.newInstance();
    }
}
