package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.somust.yyteam.R;


public class AppStartActivity extends Activity
{


	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_appstart);
		immersiveStatusBar();

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
	/**
	 * 沉浸式状态栏（伪）
	 */
	private void immersiveStatusBar() {
		//沉浸式状态栏（伪）
		if (Build.VERSION.SDK_INT >= 21) {
			View decorView = getWindow().getDecorView();
			int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
			decorView.setSystemUiVisibility(option);
			getWindow().setStatusBarColor(Color.TRANSPARENT);
		}
	}

}
