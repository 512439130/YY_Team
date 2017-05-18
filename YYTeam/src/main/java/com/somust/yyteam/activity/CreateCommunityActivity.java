package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.Community;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.TeamMember;
import com.somust.yyteam.bean.TeamNews;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.utils.photo.BigPhotoUtils;
import com.somust.yyteam.utils.photo.PhotoUtils;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Date;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;


/**
 * Created by 13160677911 on 2017-5-3.
 * 发布社团圈
 */

public class CreateCommunityActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CreateCommunityActivity:";

    private Community community = new Community();

    private TeamMember teamMember;

    private ImageView mImg_Background;   //动态背景


    //分享时间
    private TextView communityTime;


    //社团圈图片
    private ImageView communityImage;
    //分享内容
    private EditText communityContent;

    //创建button
    private Button btn_createCmmunity;

    private String jsonString;

    //相册
    private BigPhotoUtils bigPhotoUtils;
    private Uri selectUri;
    private ProgressDialog dialog;
    private String teamNewsSdPath;


    //photoUtils相关
    private String fileName;  //剪切后的文件名
    private String filePath; //文件路径(在手机中)
    private String fileTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_community);
        immersiveStatusBar();
        Intent intent = getIntent();
        teamMember = (TeamMember) intent.getSerializableExtra("teamMember");

        initViews();

        dynamaicBackground();

        initListeners();
        initDatas();

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
    /**
     * 动态背景
     */
    private void dynamaicBackground() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(CreateCommunityActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }


    private void initViews() {
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);

        //分享时间
        communityTime = (TextView) findViewById(R.id.create_community_time_tv);
        //社团圈图片
        communityImage = (ImageView) findViewById(R.id.create_community_image_iv);
        //分享内容
        communityContent = (EditText) findViewById(R.id.create_community_content_et);

        btn_createCmmunity = (Button) findViewById(R.id.create_community_btn);

        setPortraitChangeListener();

    }

    private void initListeners() {
        communityImage.setOnClickListener(this);
        btn_createCmmunity.setOnClickListener(this);
    }

    private void initDatas() {
        //获取创建时间
        Date nowTime = new Date();
        String createTime = DateUtil.dateToString(nowTime, Constant.formatType);  //创建社团的时间
        fileTime = String.valueOf(new Date().getTime());  //剪切图片的时间

        communityTime.setText(createTime);

        community.setTeamMemberId(teamMember);
        community.setCommunityTime(createTime);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_community_image_iv:
                //打开照片选择器
                bigPhotoUtils.selectPicture(CreateCommunityActivity.this, teamMember.getTeamMemberId().toString(), fileTime, Constant.CROP_COMMUNITY_IMAGE_NAME);
                break;
            case R.id.create_community_btn:   //分享社团圈
                //发送创建社团请求
                //先获取et的值，再判空，再请求
                obtainEtValue();
                break;
            default:
                break;
        }
    }

    /**
     * 获取et的值，如果不为空则执行网络请求
     */
    private void obtainEtValue() {
        String teamCommunityContent = communityContent.getText().toString().trim();
        String teamNewsImage = Constant.COMMUNITY_IMAGE_PATH+fileName;

        if(TextUtils.isEmpty(teamCommunityContent)){
            T.testShowShort(CreateCommunityActivity.this,"分享内容不能为空");
        }else if(TextUtils.isEmpty(teamNewsSdPath)){
            T.testShowShort(CreateCommunityActivity.this,"请选择分享的图片");
        }else if(!TextUtils.isEmpty(teamCommunityContent)&&!TextUtils.isEmpty(teamNewsSdPath)){
            community.setCommunityContent(teamCommunityContent);
            community.setCommunityImage(teamNewsImage);
            CreateTeamNewsRequest(community);
        }

    }


    /**
     * 拍照或本地选择图片成功后，生成图片在sd卡中，并得到图片位置
     */
    private void setPortraitChangeListener() {
        bigPhotoUtils = new BigPhotoUtils(new BigPhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    dialog = ProgressDialog.show(CreateCommunityActivity.this, "提示", Constant.mProgressDialog_success, true, true);
                    //根据图片位置发送上传文件的网络请求
                    teamNewsSdPath = selectUri.getPath();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadImage(teamNewsSdPath);
                        }
                    }, 500);

                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:

                bigPhotoUtils.onActivityResult(CreateCommunityActivity.this, requestCode, resultCode, data, teamMember.getTeamMemberId().toString(), fileTime, Constant.CROP_COMMUNITY_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_SELECT:
                bigPhotoUtils.onActivityResult(CreateCommunityActivity.this, requestCode, resultCode, data, teamMember.getTeamMemberId().toString(), fileTime, Constant.CROP_COMMUNITY_IMAGE_NAME);
                break;
        }
    }


    public void uploadImage(String path) {
        filePath = Constant.FILE_PATH;             //文件路径(在手机中)
        fileName = path.substring(path.lastIndexOf("/") + 1, path.length());

        L.v(TAG + "filePath=", filePath);
        L.v(TAG + "fileName=", fileName);
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            T.testShowShort(CreateCommunityActivity.this, "文件不存在，请修改文件路径");
            return;
        }

        OkHttpUtils.post()
                .addFile("image", fileName, file)
                .url(ConstantUrl.FileImageUrl + ConstantUrl.uploadImage_interface)
                .addParams("serverPath", "C:/websoft/image/community/")
                .build()
                .execute(new MyUploadTeamImageCallback());
    }

    public class MyUploadTeamImageCallback extends StringCallback {
        @Override
        public boolean validateReponse(Response response, int id) {
            L.v(TAG, "validateReponse");
            L.v(TAG, "上传成功");
            dialog.cancel();//关闭圆形进度条
            setSdImage(teamNewsSdPath);
            return super.validateReponse(response, id);

        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            super.inProgress(progress, total, id);
            L.v(TAG + "上传进度=", (progress * 100) + "%");
            if (progress == 1.0f) {

                L.v(TAG, "上传成功");

            }
        }

    }

    /**
     * 剪切后的图片保存在sd卡中，imageview设置sd的image
     */
    private void setSdImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        communityImage.setImageBitmap(bitmap);
    }


    public void CreateTeamNewsRequest(Community community){
        jsonString = new Gson().toJson(community);

        dialog = ProgressDialog.show(CreateCommunityActivity.this, "提示", Constant.mProgressDialog_success, true, true);
        //根据图片位置发送上传文件的网络请求
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.communityUrl + ConstantUrl.createCommunity_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonString)
                        .build()
                        .execute(new MyRequestCreateCommunityCallback());
            }
        }, 2000);
        //发起添加请求

    }
    public class MyRequestCreateCommunityCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dialog.cancel();//关闭圆形进度条
            e.printStackTrace();
            L.v(TAG, "请求失败");
            T.testShowShort(CreateCommunityActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(CreateCommunityActivity.this, Constant.mMessage_success);

            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(CreateCommunityActivity.this, TeamHomeActivity.class));  //跳转回主页面
            bigPhotoUtils.clearCropFile(selectUri);//清除手机内存中刚刚裁剪的图片   必须在上传文件成功后执行

        }
    }
}
