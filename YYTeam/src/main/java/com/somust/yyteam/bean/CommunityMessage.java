package com.somust.yyteam.bean;

import android.graphics.Bitmap;



/**
 * Created by 13160677911 on 2017-5-19.
 * 社团圈
 */

public class CommunityMessage {

    private Integer communityId;
    private Integer teamMemberId;
    private String communityContent;
    private Bitmap communityImage;
    private String communityTime;

    //team_user
    private String userNickname;
    private Bitmap userImage;

    public Integer getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Integer communityId) {
        this.communityId = communityId;
    }

    public Integer getTeamMemberId() {
        return teamMemberId;
    }

    public void setTeamMemberId(Integer teamMemberId) {
        this.teamMemberId = teamMemberId;
    }

    public String getCommunityContent() {
        return communityContent;
    }

    public void setCommunityContent(String communityContent) {
        this.communityContent = communityContent;
    }

    public Bitmap getCommunityImage() {
        return communityImage;
    }

    public void setCommunityImage(Bitmap communityImage) {
        this.communityImage = communityImage;
    }

    public String getCommunityTime() {
        return communityTime;
    }

    public void setCommunityTime(String communityTime) {
        this.communityTime = communityTime;
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
