package com.somust.yyteam.bean;

/**
 * Created by yy on 2017/3/14.
 */
public class Friend {

    private String userId;
    private String name;
    private String portraitUri;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public Friend(String userId, String name, String portraitUri) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
    }
    public Friend(){

    }

    @Override
    public String toString() {
        return "Friend{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", portraitUri='" + portraitUri + '\'' +
                '}';
    }
}
