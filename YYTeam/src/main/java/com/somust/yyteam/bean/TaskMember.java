package com.somust.yyteam.bean;

import java.io.Serializable;
import java.util.Date;

public class TaskMember implements Serializable{
        private Integer taskMemberId;
        private User userId;
        private TeamTask taskId;
        private String taskMemberJoinTime;
        public Integer getTaskMemberId() {
                return taskMemberId;
        }
        public void setTaskMemberId(Integer taskMemberId) {
                this.taskMemberId = taskMemberId;
        }
        
        public User getUserId() {
                return userId;
        }
        public void setUserId(User userId) {
                this.userId = userId;
        }
        public TeamTask getTaskId() {
                return taskId;
        }
        public void setTaskId(TeamTask taskId) {
                this.taskId = taskId;
        }
        public String getTaskMemberJoinTime() {
                return taskMemberJoinTime;
        }
        public void setTaskMemberJoinTime(String taskMemberJoinTime) {
                this.taskMemberJoinTime = taskMemberJoinTime;
        }
        
        
        
}
