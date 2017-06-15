package com.somust.yyteam.constant;

import android.os.Environment;

/**
 * Created by 13160677911 on 2017-4-14.  常量类
 */

public class Constant {
    //test
    public static final String BASE_URL = "http://www.wamvm.cn";  //服务器ip地址

    //ProgressDialog_message
    public static final String mProgressDialog_success = "正在操作，请稍后...";
    public static final String mProgressDialog_message = "正在操作，请稍后...";
    public static final String mProgressDialog_error = "操作失败，服务器正在维护";
    public static final String mMessage_success = "操作成功!";
    public static final String mMessage_error = "操作失败!";


    //formatType
    public static final String formatType = "yyyy-MM-dd HH:mm:ss";
    public static final String formatTypeNoTile = "yyyy-MM-dd";

    public static final String FILE_PATH = Environment.getExternalStorageDirectory().toString();

    //上传文件名称
    public static final String CROP_USER_IMAGE_NAME = "user_image.png";     //用户头像名称
    public static final String CROP_TEAM_IMAGE_NAME = "team_image.png";     //社团头像名称
    public static final String CROP_TEAM_NEWS_IMAGE_NAME = "team_news_image.png";     //社团新闻图片名称
    public static final String CROP_COMMUNITY_IMAGE_NAME = "community_image.png";     //社团圈图片名称



    public static final String USER_IMAGE_PATH = BASE_URL + "/image/";         //用户头像存放的服务器地址
    public static final String TEAM_IMAGE_PATH = BASE_URL + "/image/team/";         //社团头像存放的服务器地址
    public static final String TEAM_NEWS_IMAGE_PATH = BASE_URL + "/image/news/";         //社团新闻存放的服务器地址

    public static final String COMMUNITY_IMAGE_PATH = BASE_URL + "/image/community/";   //社团圈存放的服务器地址
}
