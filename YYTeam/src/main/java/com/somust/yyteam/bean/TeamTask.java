package com.somust.yyteam.bean;

import java.io.Serializable;
import java.util.Date;

public class TeamTask implements Serializable{
           
        private Integer taskId;
        private String taskTitle;
        private String taskContent;
        private Integer taskReleaseId;
        private TeamMember taskResponsibleId;
        private String taskState;
        private Integer taskMaxNumber;
        private String taskCreateTime;
        private String taskSummary;
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
        public TeamMember getTaskResponsibleId() {
                return taskResponsibleId;
        }
        public void setTaskResponsibleId(TeamMember taskResponsibleId) {
                this.taskResponsibleId = taskResponsibleId;
        }
        public String getTaskState() {
                return taskState;
        }
        public void setTaskState(String taskState) {
                this.taskState = taskState;
        }
        public Integer getTaskMaxNumber() {
                return taskMaxNumber;
        }
        public void setTaskMaxNumber(Integer taskMaxNumber) {
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
        
        

}
