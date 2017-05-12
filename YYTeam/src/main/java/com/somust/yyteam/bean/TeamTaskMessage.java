package com.somust.yyteam.bean;

import android.graphics.Bitmap;

public class TeamTaskMessage {
           
        private Integer taskId;
        private String taskTitle;
        private String taskContent;
        private String taskReleaseId;
        private String teamMemberId;
        private String taskState;
        private String taskMaxNumber;
        private String taskCreateTime;
        private String taskSummary;
        private String teamName;
        private Bitmap teamImage;
        private String taskResponsibleNickname;

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

        public String getTaskReleaseId() {
                return taskReleaseId;
        }

        public void setTaskReleaseId(String taskReleaseId) {
                this.taskReleaseId = taskReleaseId;
        }

        public String getTeamMemberId() {
                return teamMemberId;
        }

        public void setTeamMemberId(String teamMemberId) {
                this.teamMemberId = teamMemberId;
        }

        public String getTaskState() {
                return taskState;
        }

        public void setTaskState(String taskState) {
                this.taskState = taskState;
        }

        public String getTaskMaxNumber() {
                return taskMaxNumber;
        }

        public void setTaskMaxNumber(String taskMaxNumber) {
                this.taskMaxNumber = taskMaxNumber;
        }

        public String getTaskCreateTime() {
                return taskCreateTime;
        }

        public void setTaskCreateTime(String taskCreateTime) {
                this.taskCreateTime = taskCreateTime;
        }

        public String getTaskSummary() {
                return taskSummary;
        }

        public void setTaskSummary(String taskSummary) {
                this.taskSummary = taskSummary;
        }

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

        public String getTaskResponsibleNickname() {
                return taskResponsibleNickname;
        }

        public void setTaskResponsibleNickname(String taskResponsibleNickname) {
                this.taskResponsibleNickname = taskResponsibleNickname;
        }
}
