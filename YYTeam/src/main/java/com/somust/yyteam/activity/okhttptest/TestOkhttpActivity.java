package com.somust.yyteam.activity.okhttptest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.somust.yyteam.R;
import com.somust.yyteam.constant.ConstantUrl;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;

/**
 * create by yy testOkhttp
 */
public class TestOkhttpActivity extends Activity {


    private static final String TAG = "TestOkhttpActivity";

    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp);
        initView();
    }

    private void initView() {
        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    public void getHtml(View view)
    {
        String url = ConstantUrl.mTestUrl + "yy_login?";
        OkHttpUtils
                .get()
                .url(url)
                .addParams("username","yangyang")
                .addParams("password","zz0114yhbb")
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }
    public void postLogin(View view){
        String url = ConstantUrl.mTestUrl + "yy_login?";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username","yy")
                .addParams("password","zz0114yhbb")
                .build()
                .execute(new MyStringCallback());
    }
    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            mTv.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            Log.e(TAG, "onResponse：complete");
            mTv.setText("onResponse:" + response);

            switch (id)
            {
                case 100:
                    Toast.makeText(TestOkhttpActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(TestOkhttpActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
            mProgressBar.setProgress((int) (100 * progress));
        }
    }


    public void Asyncposts(View view){
    //发起注册请求
        OkHttpUtils
                .postString()
                .url(ConstantUrl.userUrl + ConstantUrl.userlogin_interface)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content("{\"nickname\":\"yy\",\"password\":\"123\",\"phone\":\"13160677911\",\"token\":\"C0TxNolWPbXcgztzuTcImSeao0497FbF82b4kDtaLRDm10eCUvEfH/i5r1kKArrDKFmEGuegEOYlZxa0d9wem+Xjo9AsBtyr\"}")
                .build()
                .execute(new MyStringCallback());
    }
}
