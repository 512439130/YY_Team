package com.somust.yyteam.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

public class TeamMemberMessage implements Serializable{
        private Integer teamMemberId;
        private User userId;
        private Team teamId;
        private String teamMemberPosition;
        private String teamMemberJoinTime;
        private Bitmap teamImage;


        public TeamMemberMessage(){

        }

        public TeamMemberMessage(User userId, Team teamId, String teamMemberPosition, String teamMemberJoinTime,Bitmap teamImage) {
                this.userId = userId;
                this.teamId = teamId;
                this.teamMemberPosition = teamMemberPosition;
                this.teamMemberJoinTime = teamMemberJoinTime;
                this.teamImage = teamImage;
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

        public Bitmap getTeamImage() {
                return teamImage;
        }

        public void setTeamImage(Bitmap teamImage) {
                this.teamImage = teamImage;
        }
}
