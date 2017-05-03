package com.somust.yyteam.constant;

import android.os.Environment;

/**
 * Created by 13160677911 on 2017-4-14.  常量类
 */

public class Constant {
    //ProgressDialog_message
    public static final String mProgressDialog_success = "正在操作，请稍后...";
    public static final String mProgressDialog_message = "正在操作，请稍后...";
    public static final String mProgressDialog_error = "操作失败，服务器正在维护";
    public static final String mMessage_success = "操作成功!";
    public static final String mMessage_error = "操作失败!";


    //formatType
    public static final String formatType = "yyyy-MM-dd HH:mm:ss";

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString();

    //上传文件名称
    public static final String CROP_USER_IMAGE_NAME = "user_image.png";     //用户头像名称
    public static final String CROP_TEAM_IMAGE_NAME = "team_image.png";     //用户头像名称

    public static final String USER_IMAGE_PATH = "http://118.89.47.137/image/";         //用户头像存放的服务器地址
    public static final String TEAM_IMAGE_PATH = "http://118.89.47.137/image/team/";         //用户头像存放的服务器地址
}
