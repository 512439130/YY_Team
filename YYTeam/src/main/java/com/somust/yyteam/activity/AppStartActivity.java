package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.somust.yyteam.R;


public class AppStartActivity extends Activity
{


	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appstart);

		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				String userId = "呵呵";//通过存入shareprefenence来判断是否登录过
				if (TextUtils.isEmpty(userId))//判断当前用户是否登陆过
				{
					Intent intent = new Intent(AppStartActivity.this, HomeActivity.class);
					startActivity(intent);

				} else
				{
					Intent intent = new Intent(AppStartActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				AppStartActivity.this.finish();
			}
		},2000);

	}

}
