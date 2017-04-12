package com.somust.yyteam.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Bob on 15/8/21.
 */
public class BaseContext {

    private static BaseContext mBaseContext;
    public Context mContext;
    private SharedPreferences mPreferences;

    public static BaseContext getInstance() {

        if (mBaseContext == null) {
            mBaseContext = new BaseContext();
        }
        return mBaseContext;
    }

    private BaseContext() {
    }

    private BaseContext(Context context) {
        mContext = context;
        mBaseContext = this;
        //http初始化 用于登录、注册使用
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }


    public static void init(Context context) {
        mBaseContext = new BaseContext(context);
    }

    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }

}
