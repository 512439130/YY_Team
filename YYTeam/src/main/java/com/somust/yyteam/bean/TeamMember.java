package com.somust.yyteam.bean;

import java.io.Serializable;
import java.util.Date;

public class TeamMember implements Serializable{
        private Integer teamMemberId;
        private User userId;
        private Team teamId;
        private String teamMemberPosition;
        private String teamMemberJoinTime;


        public TeamMember(){

        }

        public TeamMember(User userId, Team teamId, String teamMemberPosition, String teamMemberJoinTime) {
                this.userId = userId;
                this.teamId = teamId;
                this.teamMemberPosition = teamMemberPosition;
                this.teamMemberJoinTime = teamMemberJoinTime;
        }



        public Integer getTeamMemberId() {
                return teamMemberId;
        }
        public void setTeamMemberId(Integer teamMemberId) {
                this.teamMemberId = teamMemberId;
        }
        public User getUserId() {
                return userId;
        }
        public void setUserId(User userId) {
                this.userId = userId;
        }
        public Team getTeamId() {
                return teamId;
        }
        public void setTeamId(Team teamId) {
                this.teamId = teamId;
        }
        public String getTeamMemberPosition() {
                return teamMemberPosition;
        }
        public void setTeamMemberPosition(String teamMemberPosition) {
                this.teamMemberPosition = teamMemberPosition;
        }

        public String getTeamMemberJoinTime() {
                return teamMemberJoinTime;
        }

        public void setTeamMemberJoinTime(String teamMemberJoinTime) {
                this.teamMemberJoinTime = teamMemberJoinTime;
        }
}
