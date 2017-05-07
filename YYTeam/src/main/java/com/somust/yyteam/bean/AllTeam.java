package com.somust.yyteam.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by 13160677911 on 2017-3-31.
 */

public class AllTeam implements Serializable {
    private Integer teamId;
    private String teamName;
    private String teamType;
    private String teamTime;
    private Bitmap teamImage;
    private String teamIntroduce;

    private String teamMemberRequestReason;


    private String userId;
    private String userPhone;
    private String userNickname;
    private String userPassword;
    private String userToken;
    private Bitmap userImage;
    private String userSex;


    public AllTeam() {

    }


    public AllTeam(Integer teamId, String teamName, String teamType, String teamTime, Bitmap teamImage, String teamIntroduce, String userId, String userPhone, String userNickname, String userPassword, String userToken, Bitmap userImage, String userSex, String teamMemberRequestReason) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamType = teamType;
        this.teamTime = teamTime;
        this.teamImage = teamImage;
        this.teamIntroduce = teamIntroduce;
        this.userId = userId;
        this.userPhone = userPhone;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userToken = userToken;
        this.userImage = userImage;
        this.userSex = userSex;
        this.teamMemberRequestReason = teamMemberRequestReason;
    }

    @Override
    public String toString() {
        return "AllTeam{" +
                "teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", teamType='" + teamType + '\'' +
                ", teamTime='" + teamTime + '\'' +
                ", teamImage=" + teamImage +
                ", teamIntroduce='" + teamIntroduce + '\'' +
                ", userId='" + userId + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userNickname='" + userNickname + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userToken='" + userToken + '\'' +
                ", userImage=" + userImage +
                ", userSex='" + userSex + '\'' +
                ", teamMemberRequestReason='" + teamMemberRequestReason + '\'' +
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

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
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

    public Bitmap getTeamImage() {
        return teamImage;
    }

    public void setTeamImage(Bitmap teamImage) {
        this.teamImage = teamImage;
    }

    public String getTeamIntroduce() {
        return teamIntroduce;
    }

    public void setTeamIntroduce(String teamIntroduce) {
        this.teamIntroduce = teamIntroduce;
    }

    public String getTeamMemberRequestReason() {
        return teamMemberRequestReason;
    }

    public void setTeamMemberRequestReason(String teamMemberRequestReason) {
        this.teamMemberRequestReason = teamMemberRequestReason;
    }
}
