package com.somust.yyteam.bean;

import java.io.Serializable;
import java.util.Date;

public class Team implements Serializable{
        private Integer teamId;
        private String teamName;
        private String teamType;
        private User teamPresident;
        private String teamTime;
        private String teamImage;
        private String teamIntroduce;
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
        public User getTeamPresident() {
                return teamPresident;
        }
        public void setTeamPresident(User teamPresident) {
                this.teamPresident = teamPresident;
        }
        public String getTeamTime() {
                return teamTime;
        }
        public void setTeamTime(String teamTime) {
                this.teamTime = teamTime;
        }
        public String getTeamImage() {
                return teamImage;
        }
        public void setTeamImage(String teamImage) {
                this.teamImage = teamImage;
        }
        public String getTeamIntroduce() {
                return teamIntroduce;
        }
        public void setTeamIntroduce(String teamIntroduce) {
                this.teamIntroduce = teamIntroduce;
        }

        @Override
        public String toString() {
                return "Team{" +
                        "teamId=" + teamId +
                        ", teamName='" + teamName + '\'' +
                        ", teamType='" + teamType + '\'' +
                        ", teamPresident=" + teamPresident.toString() +
                        ", teamTime='" + teamTime + '\'' +
                        ", teamImage='" + teamImage + '\'' +
                        ", teamIntroduce='" + teamIntroduce + '\'' +
                        '}';
        }
}
