package com.somust.yyteam.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.dialog.BottomMenuDialog;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.utils.photo.PhotoUtils;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Date;


import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 13160677911 on 2017-4-29.
 * 维护信息Activity
 */

public class AccountActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "AccountActivity:";


    private BottomMenuDialog bottomMenuDialog;
    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private PhotoUtils photoUtils;
    private Uri selectUri;


    private ImageView iv_reutrn;
    private TextView titleName;

    //Tab_Button
    private RelativeLayout mImage;


    private User user;
    //data
    private ImageView iv_headPortrait;
    private TextView tv_nickname;

    private ProgressDialog dialog;


    private String fileName;  //剪切后的文件名
    private String filePath; //文件路径(在手机中)


    private String fileTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");
        obtainImage(user.getUserImage()); //通过数据库user表中的图片url地址发起网络请求

        initView();
        initDatas();
        initListener();
    }

    private void initListener() {
        iv_reutrn.setOnClickListener(this);

        mImage.setOnClickListener(this);
    }

    private void initDatas() {
        titleName.setText("修改个人信息");

        tv_nickname.setText(user.getUserNickname());

    }

    private void initView() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);

        mImage = (RelativeLayout) findViewById(R.id.account_update_image_rl);

        iv_headPortrait = (ImageView) findViewById(R.id.user_image_iv);
        tv_nickname = (TextView) findViewById(R.id.user_pass_tv);

        setPortraitChangeListener();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:
                setResult(RESULT_CANCELED,AccountActivity.this.getIntent().putExtra("user",user));
                finish();
            case R.id.account_update_image_rl:   //修改个人信息
                showPhotoDialog();
                break;
            default:
                break;
        }
    }


    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     */
    public void obtainImage(String url) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("onError:" + e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        L.v("TAG", "onResponse：complete");
                        iv_headPortrait.setImageBitmap(bitmap); //设置用户头像

                    }
                });
    }


    /**
     * 弹出底部框
     */
    @TargetApi(23)
    private void showPhotoDialog() {
        fileTime = String.valueOf(new Date().getTime());


        L.v(TAG, "弹出底部dialog");
        if (bottomMenuDialog != null && bottomMenuDialog.isShowing()) {
            bottomMenuDialog.dismiss();
        }

        bottomMenuDialog = new BottomMenuDialog(AccountActivity.this);
        bottomMenuDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                L.v(TAG, "点击dialogItem");
                if (bottomMenuDialog != null && bottomMenuDialog.isShowing()) {
                    bottomMenuDialog.dismiss();
                }
                //6.0以上的权限问题
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                        } else {
                            new AlertDialog.Builder(AccountActivity.this)
                                    .setMessage("您需要在设置里打开相机权限。")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .create().show();
                        }
                        return;
                    }
                }
                photoUtils.takePicture(AccountActivity.this, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
            }
        });
        bottomMenuDialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (bottomMenuDialog != null && bottomMenuDialog.isShowing()) {
                    bottomMenuDialog.dismiss();
                }
                photoUtils.selectPicture(AccountActivity.this, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
            }
        });
        bottomMenuDialog.show();
    }

    /**
     * 拍照或本地选择图片成功后，生成图片在sd卡中，并得到图片位置
     */
    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    dialog = ProgressDialog.show(AccountActivity.this, "提示", Constant.mProgressDialog_success, true, true);
                    //根据图片位置发送上传文件的网络请求
                    final String path = selectUri.getPath();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            uploadImage(filePath);
                            uploadImage(path);
                        }
                    }, 1000);

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

                photoUtils.onActivityResult(AccountActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_TAKE:
                photoUtils.onActivityResult(AccountActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(AccountActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
                break;
        }
    }


    public void uploadImage(String path) {
        filePath = Constant.FILE_PATH;             //文件路径(在手机中)
        fileName = path.substring(path.lastIndexOf("/") + 1, path.length());

        File file = new File(filePath, fileName);
        if (!file.exists()) {
            T.testShowShort(AccountActivity.this, "文件不存在，请修改文件路径");
            return;
        }
        OkHttpUtils.post()
                .addFile("image", fileName, file)
                .url(ConstantUrl.FileImageUrl + ConstantUrl.uploadImage_interface)
                .addParams("serverPath", "C:/websoft/image/")
                .build()
                .execute(new MyUploadUserImageCallback());
    }

    public class MyUploadUserImageCallback extends StringCallback {
        @Override
        public boolean validateReponse(Response response, int id) {
            L.v(TAG, "validateReponse");
            L.v(TAG, "上传成功");
            //发送修改用户user_image的请求
            UpdateUserImage();
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

    public void UpdateUserImage() {
        OkHttpUtils
                .post()
                .url(ConstantUrl.userUrl + ConstantUrl.updateUserImage_interface)
                .addParams("userPhone", user.getUserPhone())
                .addParams("userImage", Constant.USER_IMAGE_PATH + fileName)
                .build()
                .execute(new MyUpdateUserImageCallback());
    }

    public class MyUpdateUserImageCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            L.v(TAG, e.toString());

        }

        @Override
        public void onResponse(String response, int id) {
            L.v(TAG, response);
            L.v(TAG, "更换图片成功");

            getUserInfo(user.getUserPhone());
        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(final String userPhone) {
        final String url = ConstantUrl.userUrl + ConstantUrl.getUserInfo_interface;
        if (TextUtils.isEmpty(userPhone)) {
            T.testShowShort(AccountActivity.this, "手机号不能为空");
        } else {
            OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("userPhone", userPhone)
                    .build()
                    .execute(new MyStringCallback());


        }
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(AccountActivity.this, Constant.mProgressDialog_error);
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(AccountActivity.this, Constant.mMessage_error);
            } else {
                dialog.cancel();//关闭圆形进度条
                Gson gson = new Gson();
                user = gson.fromJson(response, User.class);
                System.out.println(user.toString());

                T.testShowShort(AccountActivity.this, Constant.mMessage_success);
                L.v(TAG, "onResponse:" + response);

                obtainImage(user.getUserImage());//刷新界面显示

                //刷新融云用户的头像
                refreshUserInfo(user.getUserPhone(), user.getUserNickname(), Constant.USER_IMAGE_PATH + fileName);
                photoUtils.clearCropFile(selectUri);//清除手机内存中刚刚裁剪的图片   必须在上传文件成功后执行
            }

        }

    }


    /**
     * 刷新用户缓存数据
     *
     * @param userid   需要更换的用户Id
     * @param nickname 用户昵称
     * @param urlPath  头像图片地址
     *                 userInfo 需要更新的用户缓存数据。
     */
    public void refreshUserInfo(String userid, String nickname, String urlPath) {
        RongIM.getInstance().refreshUserInfoCache(new UserInfo(userid, nickname, Uri.parse(urlPath)));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED,AccountActivity.this.getIntent().putExtra("user",user));
            this.finish();
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    protected void onDestroy() {
        if (dialog != null){
            dialog.dismiss();
        }
        if(bottomMenuDialog != null){
            bottomMenuDialog.dismiss();
        }
        super.onDestroy();
    }
}
