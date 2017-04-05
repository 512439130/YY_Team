package com.somust.yyteam.bean;

import java.io.Serializable;

/**
 * Created by 13160677911 on 2017-3-31.
 */

public class User implements Serializable {
    private String userPhone;
    private String userNickname;
    private String userPassword;
    private String userToken;

    public User() {

    }
    public User(String userPhone, String userNickname, String userPassword, String userToken) {
        this.userPhone = userPhone;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userToken = userToken;
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

    @Override
    public String toString() {
        return "User{" +
                "userPhone='" + userPhone + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userToken='" + userToken + '\'' +
                '}';
    }
}
