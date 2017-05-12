package com.somust.yyteam.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by 13160677911 on 2017-3-31.
 */

public class AllTask implements Serializable {

    private Integer taskId;
    private String taskTitle;
    private String taskContent;
    private Integer taskReleaseId;
    private Integer taskResponsibleId;

    private String taskMemberRequestReason;


    private String userId;
    private String userPhone;
    private String userNickname;
    private String userPassword;
    private String userToken;
    private Bitmap userImage;
    private String userSex;


    public AllTask() {

    }

    public AllTask(Integer taskId, String taskTitle, String taskContent, Integer taskReleaseId, Integer taskResponsibleId, String taskMemberRequestReason, String userId, String userPhone, String userNickname, String userPassword, String userToken, Bitmap userImage, String userSex) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.taskContent = taskContent;
        this.taskReleaseId = taskReleaseId;
        this.taskResponsibleId = taskResponsibleId;
        this.taskMemberRequestReason = taskMemberRequestReason;
        this.userId = userId;
        this.userPhone = userPhone;
        this.userNickname = userNickname;
        this.userPassword = userPassword;
        this.userToken = userToken;
        this.userImage = userImage;
        this.userSex = userSex;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public Integer getTaskReleaseId() {
        return taskReleaseId;
    }

    public void setTaskReleaseId(Integer taskReleaseId) {
        this.taskReleaseId = taskReleaseId;
    }

    public Integer getTaskResponsibleId() {
        return taskResponsibleId;
    }

    public void setTaskResponsibleId(Integer taskResponsibleId) {
        this.taskResponsibleId = taskResponsibleId;
    }

    public String getTaskMemberRequestReason() {
        return taskMemberRequestReason;
    }

    public void setTaskMemberRequestReason(String taskMemberRequestReason) {
        this.taskMemberRequestReason = taskMemberRequestReason;
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
}
