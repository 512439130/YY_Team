package com.somust.yyteam.bean;

import android.graphics.Bitmap;

/**
 * Created by 13160677911 on 2017-4-27.
 * 用户保存所有社团申请后的数据
 */

public class TeamMessage {

    //team
    private String teamName;
    private Bitmap teamImage;
    private String teamType;
    private String teamTime;


    //team_user
    private String userNickname;
    private Bitmap userImage;


    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Bitmap getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(Bitmap teamImage) {
        this.teamImage = teamImage;
    }

    public String getTeamType() {
        return teamType;
    }

    public void setTeamType(String teamType) {
        this.teamType = teamType;
    }

    public String getTeamTime() {
        return teamTime;
    }

    public void setTeamTime(String teamTime) {
        this.teamTime = teamTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public Bitmap getUserImage() {
        return userImage;
    }

    public void setUserImage(Bitmap userImage) {
        this.userImage = userImage;
    }
}
