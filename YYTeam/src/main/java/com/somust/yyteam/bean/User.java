package com.somust.yyteam.bean;

import java.io.Serializable;

/**
 * Created by 13160677911 on 2017-3-31.
 */

public class User implements Serializable {
    private Integer userId;
    private String userPhone;
    private String userNickname;
    private String userPassword;
    private String userToken;
    private String userImage;
    private String userSex;

    public User() {

    }



    public User(String userPhone, String userNickname, String userPassword, String userToken, String userImage, String userSex) {
        this.userPhone = userPhone;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userToken = userToken;
        this.userImage = userImage;
        this.userSex = userSex;
    }

    public User(Integer userId, String userPhone, String userNickname, String userPassword, String userToken, String userImage, String userSex) {
        this.userId = userId;
        this.userPhone = userPhone;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userToken = userToken;
        this.userImage = userImage;
        this.userSex = userSex;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    @Override
    public String toString() {
        return "User{" +
                "userPhone='" + userPhone + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userToken='" + userToken + '\'' +
                ", userImage='" + userImage + '\'' +
                ", userSex='" + userSex + '\'' +
                '}';
    }
}
