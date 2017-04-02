package com.somust.yyteam.bean;

import java.io.Serializable;

/**
 * Created by 13160677911 on 2017-3-31.
 */

public class User implements Serializable {
    private String phone;
    private String nickname;
    private String password;
    private String token;

    public User() {

    }
    public User(String phone, String nickname, String password, String token) {
        this.phone = phone;
        this.nickname = nickname;
        this.password = password;
        this.token = token;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
