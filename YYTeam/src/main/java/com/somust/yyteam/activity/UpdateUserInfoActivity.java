package com.somust.yyteam.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * Created by 13160677911 on 2017-4-29.
 * 维护信息Activity
 */

public class UpdateUserInfoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "UpdateUserInfoActivity:";


    private BottomMenuDialog bottomMenuDialog;
    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    private PhotoUtils photoUtils;
    private Uri selectUri;


    private ImageView iv_reutrn;
    private TextView titleName;

    //Tab_Button
    private RelativeLayout mUserImage;
    private RelativeLayout mUserNickname;
    private RelativeLayout mUserSex;

    private User user;
    //data
    private ImageView iv_userImage;
    private TextView tv_nickName;
    private TextView tv_Sex;

    private ProgressDialog dialog;


    private String fileName;  //剪切后的文件名
    private String filePath; //文件路径(在手机中)


    private String fileTime;


    private String nickname;
    private String sex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");


        initView();
        obtainImage(user.getUserImage()); //通过数据库user表中的图片url地址发起网络请求

        initListener();
    }

    private void initListener() {
        iv_reutrn.setOnClickListener(this);

        mUserImage.setOnClickListener(this);
        mUserNickname.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
    }

    private void initDatas() {
        titleName.setText("维护个人信息");
        tv_nickName.setText(user.getUserNickname());
        tv_Sex.setText(user.getUserSex());

    }

    private void initView() {
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);
        titleName = (TextView) findViewById(R.id.actionbar_name);

        mUserImage = (RelativeLayout) findViewById(R.id.account_update_image_rl);
        mUserNickname = (RelativeLayout) findViewById(R.id.account_update_nickname_rl);
        mUserSex = (RelativeLayout) findViewById(R.id.account_update_sex_rl);

        iv_userImage = (ImageView) findViewById(R.id.user_image_iv);
        tv_nickName = (TextView) findViewById(R.id.user_nickname_tv);
        tv_Sex = (TextView) findViewById(R.id.user_sex_tv);

        setPortraitChangeListener();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_title_back:
                setResult(RESULT_CANCELED,UpdateUserInfoActivity.this.getIntent().putExtra("user",user));
                finish();
            case R.id.account_update_image_rl:   //修改头像
                showPhotoDialog();
                break;
            case R.id.account_update_nickname_rl:   //修改昵称
                showEditDialog(UpdateUserInfoActivity.this,"请输入昵称","NICKNAME");
                break;
            case R.id.account_update_sex_rl:   //修改性别
                showEditDialog(UpdateUserInfoActivity.this,"请输入性别","SEX");
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
                        iv_userImage.setImageBitmap(bitmap); //设置用户头像
                        initDatas();
                    }
                });
    }


    /**
     * 输入提示框
     */
    private void showEditDialog(final Context context, String dialogName , final String flag) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final TextView dialog_name_tv = (TextView) promptsView.findViewById(R.id.dialog_name_tv);
        dialog_name_tv.setText(dialogName);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        if(flag.equals("NICKNAME")){
            userInput.setText(user.getUserNickname());
        }else if(flag.equals("SEX")){
            userInput.setText(user.getUserSex());
        }
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                if(flag.equals("NICKNAME")){
                                    nickname = userInput.getText().toString();
                                    updateUserInfo(new User(user.getUserId(),nickname,user.getUserSex()));
                                }else if(flag.equals("SEX")){
                                    sex = userInput.getText().toString();
                                    updateUserInfo(new User(user.getUserId(),user.getUserNickname(),sex));
                                }

                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * 修改用户信息的网络请求
     */
    private void updateUserInfo(final User user) {
        dialog = ProgressDialog.show(UpdateUserInfoActivity.this, "提示", Constant.mProgressDialog_success, true, true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String jsonString = new Gson().toJson(user);

                //发起添加请求
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.userUrl + ConstantUrl.updateUserInfo_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonString)
                        .build()
                        .execute(new MyUpdateUserInfoCallback());
            }
        }, 600);//2秒后执行Runnable中的run方法

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

        bottomMenuDialog = new BottomMenuDialog(UpdateUserInfoActivity.this);
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
                            new AlertDialog.Builder(UpdateUserInfoActivity.this)
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
                photoUtils.takePicture(UpdateUserInfoActivity.this, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
            }
        });
        bottomMenuDialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (bottomMenuDialog != null && bottomMenuDialog.isShowing()) {
                    bottomMenuDialog.dismiss();
                }
                photoUtils.selectPicture(UpdateUserInfoActivity.this, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
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
                    dialog = ProgressDialog.show(UpdateUserInfoActivity.this, "提示", Constant.mProgressDialog_success, true, true);
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
                photoUtils.onActivityResult(UpdateUserInfoActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_TAKE:
                photoUtils.onActivityResult(UpdateUserInfoActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(UpdateUserInfoActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime,Constant.CROP_USER_IMAGE_NAME);
                break;
        }
    }


    public void uploadImage(String path) {
        filePath = Constant.FILE_PATH;             //文件路径(在手机中)
        fileName = path.substring(path.lastIndexOf("/") + 1, path.length());

        File file = new File(filePath, fileName);
        if (!file.exists()) {
            T.testShowShort(UpdateUserInfoActivity.this, "文件不存在，请修改文件路径");
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
                .execute(new MyUpdateUserInfoCallback());
    }

    public class MyUpdateUserInfoCallback extends StringCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            L.v(TAG, e.toString());

        }

        @Override
        public void onResponse(String response, int id) {
            L.v(TAG, response);
            L.v(TAG, "用户信息更新成功");

            getUserInfo(user.getUserPhone());
        }
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo(final String userPhone) {
        final String url = ConstantUrl.userUrl + ConstantUrl.getUserInfo_interface;
        if (TextUtils.isEmpty(userPhone)) {
            T.testShowShort(UpdateUserInfoActivity.this, "手机号不能为空");
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
            T.testShowShort(UpdateUserInfoActivity.this, Constant.mProgressDialog_error);
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(UpdateUserInfoActivity.this, Constant.mMessage_error);
            } else {
                dialog.cancel();//关闭圆形进度条
                Gson gson = new Gson();
                user = gson.fromJson(response, User.class);
                System.out.println(user.toString());

                T.testShowShort(UpdateUserInfoActivity.this, Constant.mMessage_success);
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
            setResult(RESULT_CANCELED,UpdateUserInfoActivity.this.getIntent().putExtra("user",user));
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
