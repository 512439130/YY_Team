package com.somust.yyteam.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by 13160677911 on 2017-3-31.
 */

public class AllUser implements Serializable {
    private String userId;
    private String userPhone;
    private String userNickname;
    private String userPassword;
    private String userToken;
    private Bitmap userImage;
    private String userSex;
    private String friendRequestReason;

    public AllUser() {

    }


    public AllUser(String userId, String userPhone, String userNickname, String userPassword, String userToken, Bitmap userImage, String userSex, String friendRequestReason) {
        this.userId = userId;
        this.userPhone = userPhone;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userToken = userToken;
        this.userImage = userImage;
        this.userSex = userSex;
        this.friendRequestReason = friendRequestReason;
    }

    @Override
    public String toString() {
        return "AllUser{" +
                "userId='" + userId + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userToken='" + userToken + '\'' +
                ", userImage=" + userImage +
                ", userSex='" + userSex + '\'' +
                ", friendRequestReason='" + friendRequestReason + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getFriendRequestReason() {
        return friendRequestReason;
    }

    public void setFriendRequestReason(String friendRequestReason) {
        this.friendRequestReason = friendRequestReason;
    }
}
