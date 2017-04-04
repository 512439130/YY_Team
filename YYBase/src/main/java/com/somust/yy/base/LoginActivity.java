package com.somust.yy.base;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by tangyangkai on 16/5/10.
 */
public class LoginActivity extends AppActivity {

    private String username;
    @Override
    protected void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            username = bundle.getString("username");
        }
    }

    @Override
    protected BaseFragment getFirstFragment() {
        return FirstFragment.newInstance(username);
    }

}

